#include <iostream>
#include <string>
#include <sstream>
#include <iomanip>
#include <ctime>
#include <cstring>
#include <openssl/sha.h>
#include <vector>
#include <cmath>
#include <atomic>

#ifndef BLOCKCHAIN_BLOCK_H
#define BLOCKCHAIN_BLOCK_H

class Block {
public:
    int id;
    std::string data;
    time_t timeStamp;
    unsigned int nonce;
    unsigned int difficulty;
    std::string currentHash;
    std::string previousHash;

    Block() {
        id = 0;
        timeStamp = std::time(nullptr);
        nonce = 0;
        difficulty = 0;
        currentHash = std::string(SHA256_DIGEST_LENGTH * 2, '0');
        previousHash = std::string(SHA256_DIGEST_LENGTH * 2, '0');
    }

    Block(int id, const std::string& data, long timeStamp, int nonce, int difficulty, const std::string& currentHash, const std::string& previousHash)
            : id(id), data(data), timeStamp(timeStamp), nonce(nonce), difficulty(difficulty), currentHash(currentHash), previousHash(previousHash) {}

    Block(int id, const std::string& data, int difficulty)
            : id(id), data(data), difficulty(difficulty) {
        timeStamp = std::time(nullptr);
        previousHash = std::string(SHA256_DIGEST_LENGTH * 2, '0');
        nonce = 0;
    }

    std::string calculateHash() const{
        std::stringstream ss;
        ss << id << data << timeStamp << previousHash << difficulty << nonce;

        std::string input = ss.str();
        unsigned char hash[SHA256_DIGEST_LENGTH];
        SHA256(reinterpret_cast<const unsigned char*>(input.c_str()), input.size(), hash);

        return toHex(hash, SHA256_DIGEST_LENGTH);
    }

    void print() {
        std::cout << "Id: " << id << std::endl;
        std::cout << "Data: " << data << std::endl;
        std::cout << "Nonce: " << nonce << std::endl;
        std::cout << "Difficulty: " << difficulty << std::endl;
        std::cout << "Timestamp: " << std::put_time(std::localtime(&timeStamp), "%Y-%m-%d %H:%M:%S") << std::endl;
        std::cout << "Current Hash: " << currentHash << std::endl;
        std::cout << "Previous Hash: " << previousHash << std::endl;
    }

    static std::string toHex(const unsigned char* in, size_t length) {
        std::stringstream ss;
        for (size_t i = 0; i < length; ++i) {
            ss << std::hex << std::setw(2) << std::setfill('0') << (int)in[i];
        }
        return ss.str();
    }

    static std::string fromHex(const std::string& hex) {
        std::string result;
        for (size_t i = 0; i < hex.length(); i += 2) {
            std::string byteString = hex.substr(i, 2);
            unsigned char byte = (unsigned char) strtol(byteString.c_str(), nullptr, 16);
            result.push_back(byte);
        }
        return result;
    }

     bool isTimestampValidForward() const {
        long currentTime = std::time(nullptr);
        return timeStamp <= (currentTime + 60);
    }

    bool isTimestampValidBackward(const Block& previousBlock) const {
        return timeStamp >= (previousBlock.timeStamp - 60);
    }

    /*uint64_t getBlockDifficulty() const {
        return static_cast<uint64_t>(std::pow(2, difficulty));
    }*/

    uint64_t getBlockDifficulty() const {
        // Using bit shift instead of pow
        return 1ULL << difficulty;
    }

    /*
    bool hasValidHashDifficulty() const {
        std::string hash = calculateHash();
        return hash.substr(0, difficulty) == std::string(difficulty, '0');
    }*/

    bool hasValidHashDifficulty() const {
        // Check if hash starts with required number of zeros
        return currentHash.substr(0, difficulty) == std::string(difficulty, '0');
    }

    static uint64_t calculateChainDifficulty(const std::vector<Block>& chain) {
        uint64_t totalDifficulty = 0;
        for (const auto& block : chain) {
            totalDifficulty += block.getBlockDifficulty();
        }
        return totalDifficulty;
    }
};

#endif //BLOCKCHAIN_BLOCK_H