#ifndef BLOCKCHAIN_BLOCKCHAIN_H
#define BLOCKCHAIN_BLOCKCHAIN_H

#include <vector>
#include <thread>
#include <atomic>
#include <mutex>
#include "Block.h"
#include "libs/json.hpp"
#include <fstream>


class BlockChain {
private:
    static const int MINING_RATE = 10;
    static const int ADJUST_RATE = 5;

public:
    std::vector<Block> chain;
    int difficulty;

    explicit BlockChain(int difficulty);

    bool add(Block block, bool constDifficulty);
    [[nodiscard]] uint64_t getTotalDifficulty() const;
    void printLast();

    static Block mine(std::vector<Block>& chain, std::string data, int difficulty);
    static Block mineParallel(std::vector<Block>& chain, const std::string& data, int difficulty, int numThreads);

    bool validateParallel(const std::vector<Block>& chain);
    void changeDifficulty();
    long getCumulativeDifficulty() const;

    friend void to_json(nlohmann::json& j, const BlockChain& blockchain);
    friend void from_json(const nlohmann::json& j, BlockChain& blockchain);

    void loadFromFile(const std::string &filename);
    void saveToFile(const std::string &filename) const;

    std::string serialize() const;

    void deserialize(const std::string &jsonString);
};

#endif // BLOCKCHAIN_BLOCKCHAIN_H
