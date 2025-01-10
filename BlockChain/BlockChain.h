#ifndef BLOCKCHAIN_BLOCKCHAIN_H
#define BLOCKCHAIN_BLOCKCHAIN_H

#include <vector>
#include <thread>
#include <atomic>
#include <mutex>
#include "Block.h"

class BlockChain {
private:
    static const int MINING_RATE = 10;
    static const int ADJUST_RATE = 5;
    static const int MAX_TIME_DIFF = 60;

public:
    std::vector<Block> chain;
    int difficulty;

    explicit BlockChain(int difficulty);

    bool add(Block block);
    [[nodiscard]] uint64_t getTotalDifficulty() const;
    void printLast();

    static Block mine(std::vector<Block>& chain, std::string data, int difficulty);
    static Block mineParallel(std::vector<Block>& chain, const std::string& data, int difficulty, int numThreads);

    bool validateParallel(const std::vector<Block>& chain);
    void changeDifficulty();
};

#endif // BLOCKCHAIN_BLOCKCHAIN_H
