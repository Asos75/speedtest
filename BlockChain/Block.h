#ifndef BLOCKCHAIN_BLOCK_H
#define BLOCKCHAIN_BLOCK_H

#include <iostream>
#include <string>
#include <ctime>
#include <vector>
#include <sstream>
#include <iomanip>
#include <openssl/sha.h>

class Block {
public:
    int id;
    std::string data;
    time_t timeStamp;
    unsigned int nonce;
    unsigned int difficulty;
    std::string currentHash;
    std::string previousHash;

    Block();
    Block(int id, const std::string& data, long timeStamp, int nonce, int difficulty, const std::string& currentHash, const std::string& previousHash);
    Block(int id, const std::string& data, int difficulty);

    [[nodiscard]] std::string calculateHash() const;
    void print();

    static std::string toHex(const unsigned char* in, size_t length);
    static std::string fromHex(const std::string& hex);

    [[nodiscard]] bool isTimestampValidForward() const;
    [[nodiscard]] bool isTimestampValidBackward(const Block& previousBlock) const;
    [[nodiscard]] uint64_t getBlockDifficulty() const;
    [[nodiscard]] bool hasValidHashDifficulty() const;
    static uint64_t calculateChainDifficulty(const std::vector<Block>& chain);

    // Serialization and Deserialization methods
    void serialize(char* buffer) const;
    void deserialize(const char* buffer);
};

#endif //BLOCKCHAIN_BLOCK_H
