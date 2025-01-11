#ifndef BLOCKCHAIN_MININGPOOL_H
#define BLOCKCHAIN_MININGPOOL_H

#include <vector>
#include <thread>
#include <atomic>
#include <iostream>
#include <string>
#include <queue>
#include <mutex>
#include <condition_variable>
#include <barrier>
#include "Block.h"

class MiningPool {
private:
    BlockChain& blockChain;

    std::vector<std::thread> workers;
    std::queue<std::string> data_queue;
    std::mutex queueMutex;
    std::mutex blockFoundMutex;
    std::condition_variable condition;

    std::atomic<bool> blockFound;
    std::atomic<bool> stop;
    std::atomic<unsigned int> currentNonce;
    std::atomic<unsigned int> operations;

    std::barrier<> barrier;

    std::chrono::time_point<std::chrono::system_clock> start;
    std::chrono::time_point<std::chrono::system_clock> end;

public:
    MiningPool(BlockChain& blockChain, size_t numThreads) : blockChain(blockChain), stop(false), blockFound(false), barrier(numThreads) {
        currentNonce = 0;
        operations = 0;

        for (size_t i = 0; i < numThreads; ++i) {
            size_t tId = i;
#ifdef DEBUG
            std::cout << "Creating thread: " << tId << std::endl;
#endif
            workers.emplace_back([this, &blockChain, tId]() {
                size_t threadId = tId;
                std::string out = "Thread: " + std::to_string(threadId) + " started\n";
                std::cout << out;

                while (true) {
                    barrier.arrive_and_wait();
                    if(threadId == 0){
                        start = std::chrono::system_clock::now();
                    }

                    blockFound = false;

                    std::string data;

                    {
                        std::unique_lock<std::mutex> lock(queueMutex);
                        // Wait for new data to arrive in the queue or for stop signal
                        condition.wait(lock, [this]() {
                            return !data_queue.empty() || stop;
                        });

                        // If stop signal is received and the queue is empty, break out of the loop
                        if (stop && data_queue.empty())
                            return;

                        // Get the next task from the queue
                        data = data_queue.front();
                    }

                    Block block(blockChain.chain.size(), data, blockChain.difficulty);

                    if (blockChain.chain.empty()) {
                        block.previousHash = std::string(SHA256_DIGEST_LENGTH * 2, '0');
                    } else {
                        block.previousHash = blockChain.chain.back().currentHash;
                    }

                    while (!blockFound) {
                        block.nonce = currentNonce.fetch_add(1);
                        if(currentNonce % 10000000 == 0){
                            std::cout << "Thread: " << threadId << " Nonce: " << currentNonce << std::endl;
                        }
                        block.currentHash = block.calculateHash();

#ifdef DEBUG
                        out = "Thread: " + std::to_string(threadId) + " Hash: " + block.currentHash;
                        std::cout << out << std::endl;
#endif
                        operations++;
                        if (block.currentHash.substr(0, blockChain.difficulty) == std::string(blockChain.difficulty, '0')) {
                            {
                                std::lock_guard<std::mutex> lock(blockFoundMutex);

                                if(!blockFound){
                                    blockFound = true;

                                    end = std::chrono::system_clock::now();

                                    if (!blockChain.add(block)) {
                                        std::cout << "Thread: " << threadId << " failed to add block!" << std::endl;
                                        std::cout << "Wrong Prev. Hash: " << block.previousHash << std::endl;
                                        std::cout << "Expected P. Hash: " << blockChain.chain.back().currentHash << std::endl;
                                        std::cout << "Current Hash: " << block.currentHash << std::endl;
                                        continue;
                                    } else{
                                        std::cout << "Thread: " << threadId << " found a block. Nonce " << currentNonce << std::endl;
                                        auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(end - start).count();
                                        std::cout << "Time: " << duration << "ms" << std::endl;
                                        std::cout << "Operations/s: " << (float)operations / ((float)duration / 1000) << std::endl;
                                    }

                                    data_queue.pop();
                                    currentNonce = 0;
                                    operations = 0;

                                    std::cout << "<---- BLOCK ADDED ---->\n";
                                    blockChain.printLast();
                                    std::cout << "<--------------------->\n";
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    ~MiningPool() {
        // Instead of immediately stopping, just notify threads to stop when all tasks are processed
        {
            std::unique_lock<std::mutex> lock(queueMutex);
            stop = true;
        }
        condition.notify_all();

        for (std::thread &worker : workers) {
            worker.join();
        }
    }

    void enqueue(std::string add_data) {
        {
            std::unique_lock<std::mutex> lock(queueMutex);
            data_queue.push(add_data);
        }
        condition.notify_all();
    }
};

#endif //BLOCKCHAIN_MININGPOOL_H
