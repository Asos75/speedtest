//
// Created by andra on 19/12/2024.
//

#ifndef BLOCKCHAIN_BLOCK_H
#define BLOCKCHAIN_BLOCK_H


#include <iostream>
#include <vector>
#include <string>
#include <sstream>
#include <iomanip>
#include <ctime>
#include <cstring>
#include <openssl/sha.h>

class Block {
public:
    int id;
    std::vector<unsigned char> data;
    long timeStamp;
    int nonce;
    int difficulty;
    std::vector<unsigned char> currentHash;
    std::vector<unsigned char> previousHash;

    Block() {
        id = 0;
        timeStamp = std::time(nullptr);
        nonce = 0;
        difficulty = 0;
        currentHash.resize(SHA256_DIGEST_LENGTH);
        previousHash.resize(SHA256_DIGEST_LENGTH);
    }

    Block(int id, const std::vector<unsigned char>& data, long timeStamp, int nonce, int difficulty, const std::vector<unsigned char>& currentHash, const std::vector<unsigned char>& previousHash)
            : id(id), data(data), timeStamp(timeStamp), nonce(nonce), difficulty(difficulty), currentHash(currentHash), previousHash(previousHash) {}

    Block(int id, const std::vector<unsigned char>& data, int difficulty)
            : id(id), data(data), difficulty(difficulty) {
        timeStamp = std::time(nullptr);
        previousHash.resize(SHA256_DIGEST_LENGTH);
        nonce = 0;
    }

    std::vector<unsigned char> calculateHash() {
        std::stringstream ss;
        ss << id << std::string(data.begin(), data.end()) << nonce << difficulty << timeStamp;

        std::string input = ss.str();
        std::vector<unsigned char> hash(SHA256_DIGEST_LENGTH);
        SHA256(reinterpret_cast<const unsigned char*>(input.c_str()), input.size(), hash.data());

        return hash;
    }

    void print() {
        std::cout << "Id: " << id << std::endl;
        std::cout << "Data: " << std::string(data.begin(), data.end()) << std::endl;
        std::cout << "Nonce: " << nonce << std::endl;
        std::cout << "Difficulty: " << difficulty << std::endl;
        std::cout << "Timestamp: " << timeStamp << std::endl;
        std::cout << "Current Hash: " << toHex(currentHash) << std::endl;
        std::cout << "Previous Hash: " << toHex(previousHash) << std::endl;
    }

    static std::string toHex(const std::vector<unsigned char>& in) {
        std::stringstream ss;
        for (unsigned char c : in) {
            ss << std::hex << std::setw(2) << std::setfill('0') << (int)c;
        }
        return ss.str();
    }

    static std::vector<unsigned char> fromHex(const std::string& hex) {
        std::vector<unsigned char> result;
        for (size_t i = 0; i < hex.length(); i += 2) {
            std::string byteString = hex.substr(i, 2);
            unsigned char byte = (unsigned char) strtol(byteString.c_str(), nullptr, 16);
            result.push_back(byte);
        }
        return result;
    }
};


#endif //BLOCKCHAIN_BLOCK_H
