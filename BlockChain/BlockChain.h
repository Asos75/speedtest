#ifndef BLOCKCHAIN_BLOCKCHAIN_H
#define BLOCKCHAIN_BLOCKCHAIN_H
using namespace std;

#include <vector>
#include <thread>
#include <atomic>
#include "Block.h"

class BlockChain{
private:
    static const int MINING_RATE = 10000;
    static const int ADJUST_RATE = 10;
    static const int MAX_TIME_DIFF = 60000;

public:
    std::vector<Block> chain;
    int difficulty;

    BlockChain(int difficulty) : difficulty(difficulty){
        chain = std::vector<Block>();
    }

    bool add(Block block){
        // Validate timestamp
        if (!block.isTimestampValidForward()) {
            std::cout << "Invalid forward timestamp" << std::endl;
            return false;
        }

        if (!chain.empty() && block.previousHash != chain.back().currentHash) {
            std::cout << "Invalid previous hash" << std::endl;
            return false;
        }

        if (!chain.empty() && !block.isTimestampValidBackward(chain.back())) {
            std::cout << "Invalid backward timestamp" << std::endl;
            return false;
        }


        if (!block.hasValidHashDifficulty()) {
            std::cout << "Invalid hash difficulty" << std::endl;
            return false;
        }

        chain.push_back(block);
        changeDifficulty();
        return true;
    }

    uint64_t getTotalDifficulty() const {
        return Block::calculateChainDifficulty(chain);
    }

    void printLast(){
        chain.back().print();
    }

    static Block mine(vector<Block>& chain, std::string data, int difficulty){
        Block block(chain.size(), data, difficulty);
        if(chain.empty()){
            block.previousHash = std::string(SHA256_DIGEST_LENGTH * 2, '0');
        }
        else{
            block.previousHash = chain.back().currentHash;
        }
        while (true){
            block.nonce++;
            block.currentHash = block.calculateHash();
            if (block.currentHash.substr(0, difficulty) == std::string(difficulty, '0')){
                return block;
            }
        }
    }

    static Block mineParallel(vector<Block>& chain, std::string data, int difficulty, int numThreads){

        std::atomic_int nonce = 0;
        std::atomic_bool found = false;
        std::vector<std::thread> threads;

        Block foundBlock;

        for(int i = 0; i < numThreads; i++){
            threads.emplace_back([&chain, &data, &nonce, &found, &foundBlock, difficulty](){
                Block block(chain.size(), data, difficulty);
                if(chain.empty()){
                    block.previousHash = std::string(SHA256_DIGEST_LENGTH * 2, '0');
                }
                else{
                    block.previousHash = chain.back().currentHash;
                }
                while (!found){
                    int n = nonce++;
                    block.nonce = n;
                    block.currentHash = block.calculateHash();
                    if (!found && block.currentHash.substr(0, difficulty) == std::string(difficulty, '0')){
                        found = true;
                        foundBlock = block;
                        return;
                    }
                }
            });
        }

        return foundBlock;
    }

    bool validateParallel(const std::vector<Block> &chain){
        if (chain.empty())
            return true;

        std::atomic<bool> valid(true);
        std::vector<std::thread> threads;

        
        // Add timestamp validation thread
        threads.emplace_back([&chain, &valid]() {
            for (size_t i = 0; i < chain.size(); i++) {
                if (!valid) return;
                if (!chain[i].isTimestampValidForward()) {
                    std::cout << "Invalid forward timestamp" << std::endl;
                    valid = false;
                    return;
                }
                if (i > 0 && !chain[i].isTimestampValidBackward(chain[i-1])) {
                    std::cout << "Invalid backward timestamp" << std::endl;
                    valid = false;
                    return;
                }
            }
        });

        // Add difficulty validation thread
        threads.emplace_back([&chain, &valid]() {
            for (const auto& block : chain) {
                if (!valid) return;
                if (!block.hasValidHashDifficulty()) {
                    std::cout << "Invalid hash difficulty" << std::endl;
                    valid = false;
                    return;
                }
            }
        });

        threads.emplace_back([&chain, &valid](){
        for (int i = 0; i < chain.size(); ++i) {
            if (!valid) return;
            auto calculatedHash = chain[i].calculateHash();
            if (chain[i].currentHash != calculatedHash) {
                cout << "Hash mismatch at block: " << i << endl;
                cout << "Expected: " << calculatedHash << endl;
                cout << "Actual:   " << chain[i].currentHash << endl;
                valid = false;
                return;
            }
        } });

        threads.emplace_back([&chain, &valid]()
                             {
        for (int i = 1; i < chain.size(); ++i) {
            if (!valid) return;

            if (chain[i].previousHash != chain[i - 1].currentHash) {
                cout << "Link mismatch at block: " << i << endl;
                valid = false;
                return;
            }
        } });

        for (auto &t : threads)
        {
            t.join();
        }

        return valid;
    }

    void changeDifficulty()
    {
        if (chain.size() < ADJUST_RATE)
        {
            return;
        }

        long timeExpected = MINING_RATE * ADJUST_RATE;
        long timeTaken = chain.back().timeStamp - chain[chain.size() - ADJUST_RATE].timeStamp;

        if (timeTaken < timeExpected / 2)
        {
            difficulty++;
        }
        else if (timeTaken > timeExpected * 2)
        {
            difficulty = max(1, difficulty - 1);
        }

        cout << "New difficulty: " << difficulty << endl;
    }
};

#endif // BLOCKCHAIN_BLOCKCHAIN_H
