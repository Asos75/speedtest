#include <iostream>
#include <string>
#include <sstream>
#include <iomanip>
#include <ctime>
#include <cstring>
#include <openssl/sha.h>
#include <vector>

class Block {
public:
    int id;
    std::string data;
    long timeStamp;
    int nonce;
    int difficulty;
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

    std::string calculateHash() {
        std::stringstream ss;
        ss << id << data << nonce << difficulty << timeStamp;

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
        std::cout << "Timestamp: " << timeStamp << std::endl;
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
};