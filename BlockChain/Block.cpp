#include "Block.h"
#include <cstring>
#include <ctime>
#include <iostream>

Block::Block() {
    id = 0;
    timeStamp = std::time(nullptr);
    nonce = 0;
    difficulty = 0;
    currentHash = std::string(SHA256_DIGEST_LENGTH * 2, '0');
    previousHash = std::string(SHA256_DIGEST_LENGTH * 2, '0');
}

Block::Block(int id, const std::string& data, long timeStamp, int nonce, int difficulty, const std::string& currentHash, const std::string& previousHash)
        : id(id), data(data), timeStamp(timeStamp), nonce(nonce), difficulty(difficulty), currentHash(currentHash), previousHash(previousHash) {}

Block::Block(int id, const std::string& data, int difficulty)
        : id(id), data(data), difficulty(difficulty) {
    timeStamp = std::time(nullptr);
    previousHash = std::string(SHA256_DIGEST_LENGTH * 2, '0');
    nonce = 0;
}

std::string Block::calculateHash() const {
    std::stringstream ss;
    ss << id << data << timeStamp << previousHash << difficulty << nonce;

    std::string input = ss.str();
    unsigned char hash[SHA256_DIGEST_LENGTH];
    SHA256(reinterpret_cast<const unsigned char*>(input.c_str()), input.size(), hash);

    return toHex(hash, SHA256_DIGEST_LENGTH);
}

void Block::print() {
    std::cout << "Id: " << id << std::endl;
    std::cout << "Data: " << data << std::endl;
    std::cout << "Nonce: " << nonce << std::endl;
    std::cout << "Difficulty: " << difficulty << std::endl;
    std::cout << "Timestamp: " << std::put_time(std::localtime(&timeStamp), "%Y-%m-%d %H:%M:%S") << std::endl;
    std::cout << "Current Hash: " << currentHash << std::endl;
    std::cout << "Previous Hash: " << previousHash << std::endl;
}

std::string Block::toHex(const unsigned char* in, size_t length) {
    std::stringstream ss;
    for (size_t i = 0; i < length; ++i) {
        ss << std::hex << std::setw(2) << std::setfill('0') << (int)in[i];
    }
    return ss.str();
}

std::string Block::fromHex(const std::string& hex) {
    std::string result;
    for (size_t i = 0; i < hex.length(); i += 2) {
        std::string byteString = hex.substr(i, 2);
        unsigned char byte = (unsigned char) strtol(byteString.c_str(), nullptr, 16);
        result.push_back(byte);
    }
    return result;
}

bool Block::isTimestampValidForward() const {
    long currentTime = std::time(nullptr);
    return timeStamp <= (currentTime + 60);
}

bool Block::isTimestampValidBackward(const Block& previousBlock) const {
    return timeStamp >= (previousBlock.timeStamp - 60);
}

uint64_t Block::getBlockDifficulty() const {
    return 1ULL << difficulty;
}

bool Block::hasValidHashDifficulty() const {
    return currentHash.substr(0, difficulty) == std::string(difficulty, '0');
}

uint64_t Block::calculateChainDifficulty(const std::vector<Block>& chain) {
    uint64_t totalDifficulty = 0;
    for (const auto& block : chain) {
        totalDifficulty += block.getBlockDifficulty();
    }
    return totalDifficulty;
}

void Block::serialize(char* buffer) const {
    size_t offset = 0;

    memcpy(buffer + offset, &id, sizeof(id));
    offset += sizeof(id);
    size_t dataLength = data.size();
    memcpy(buffer + offset, &dataLength, sizeof(dataLength));
    offset += sizeof(dataLength);
    memcpy(buffer + offset, data.c_str(), dataLength);
    offset += dataLength;

    memcpy(buffer + offset, &timeStamp, sizeof(timeStamp));
    offset += sizeof(timeStamp);
    memcpy(buffer + offset, &nonce, sizeof(nonce));
    offset += sizeof(nonce);
    memcpy(buffer + offset, &difficulty, sizeof(difficulty));
    offset += sizeof(difficulty);

    memcpy(buffer + offset, currentHash.c_str(), currentHash.size() + 1);
    offset += currentHash.size() + 1;
    memcpy(buffer + offset, previousHash.c_str(), previousHash.size() + 1);
}

void Block::deserialize(const char* buffer) {
    size_t offset = 0;

    memcpy(&id, buffer + offset, sizeof(id));
    offset += sizeof(id);
    size_t dataLength;
    memcpy(&dataLength, buffer + offset, sizeof(dataLength));
    offset += sizeof(dataLength);
    data.assign(buffer + offset, dataLength);
    offset += dataLength;

    memcpy(&timeStamp, buffer + offset, sizeof(timeStamp));
    offset += sizeof(timeStamp);
    memcpy(&nonce, buffer + offset, sizeof(nonce));
    offset += sizeof(nonce);
    memcpy(&difficulty, buffer + offset, sizeof(difficulty));
    offset += sizeof(difficulty);

    currentHash.assign(buffer + offset);
    offset += currentHash.size() + 1;
    previousHash.assign(buffer + offset);
}
