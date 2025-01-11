#include "BlockChain.h"

#include <iostream>
#include <mutex>
#include <iomanip>  // For std::put_time

BlockChain::BlockChain(int difficulty) : difficulty(difficulty) {
    chain = std::vector<Block>();
}

bool BlockChain::add(Block block) {
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

uint64_t BlockChain::getTotalDifficulty() const {
    return Block::calculateChainDifficulty(chain);
}

void BlockChain::printLast() {
    chain.back().print();
}

Block BlockChain::mine(std::vector<Block>& chain, std::string data, int difficulty) {
    Block block(chain.size(), data, difficulty);
    if (chain.empty()) {
        block.previousHash = std::string(SHA256_DIGEST_LENGTH * 2, '0');
    } else {
        block.previousHash = chain.back().currentHash;
    }
    while (true) {
        block.nonce++;
        block.currentHash = block.calculateHash();
        if (block.currentHash.substr(0, difficulty) == std::string(difficulty, '0')) {
            return block;
        }
    }
}

Block BlockChain::mineParallel(std::vector<Block>& chain, const std::string& data, int difficulty, int numThreads) {
    std::atomic<bool> found(false);
    std::atomic<uint64_t> nonce(0);
    std::vector<std::thread> threads;
    Block resultBlock;
    std::mutex resultMutex;

    auto miningTask = [&](int threadId) {
        std::cout << "Thread " << threadId << " started" << std::endl;
        Block block(chain.size(), data, difficulty);

        if (chain.empty()) {
            block.previousHash = std::string(SHA256_DIGEST_LENGTH * 2, '0');
        } else {
            block.previousHash = chain.back().currentHash;
        }

        while (!found) {
            uint64_t currentNonce = nonce++;
            block.nonce = currentNonce;
            block.currentHash = block.calculateHash();

            if (block.currentHash.substr(0, difficulty) == std::string(difficulty, '0')) {
                std::lock_guard<std::mutex> lock(resultMutex);
                if (!found) {
                    found = true;
                    resultBlock = block;
                }
                break;
            }
        }
    };

    for (int i = 0; i < numThreads; ++i) {
        threads.emplace_back(miningTask, i);
    }

    for (auto& t : threads) {
        t.join();
    }

    return resultBlock;
}

bool BlockChain::validateParallel(const std::vector<Block>& chain) {
    if (chain.empty()) return true;

    std::atomic<bool> valid(true);
    std::vector<std::thread> threads;

    threads.emplace_back([&chain, &valid]() {
        for (size_t i = 0; i < chain.size(); i++) {
            if (!valid) return;
            if (!chain[i].isTimestampValidForward()) {
                std::cout << "Invalid forward timestamp" << std::endl;
                valid = false;
                return;
            }
            if (i > 0 && !chain[i].isTimestampValidBackward(chain[i - 1])) {
                std::cout << "Invalid backward timestamp" << std::endl;
                valid = false;
                return;
            }
        }
    });

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

    threads.emplace_back([&chain, &valid]() {
        for (int i = 0; i < chain.size(); ++i) {
            if (!valid) return;
            auto calculatedHash = chain[i].calculateHash();
            if (chain[i].currentHash != calculatedHash) {
                std::cout << "Hash mismatch at block: " << i << std::endl;
                std::cout << "Expected: " << calculatedHash << std::endl;
                std::cout << "Actual:   " << chain[i].currentHash << std::endl;
                valid = false;
                return;
            }
        }
    });

    threads.emplace_back([&chain, &valid]() {
        for (int i = 1; i < chain.size(); ++i) {
            if (!valid) return;
            if (chain[i].previousHash != chain[i - 1].currentHash) {
                std::cout << "Link mismatch at block: " << i << std::endl;
                valid = false;
                return;
            }
        }
    });

    for (auto& t : threads) {
        t.join();
    }

    return valid;
}

void BlockChain::changeDifficulty() {
    if (chain.size() < ADJUST_RATE) return;

    const long timeExpected = MINING_RATE * ADJUST_RATE;
    long timeTaken = chain.back().timeStamp - chain[chain.size() - ADJUST_RATE].timeStamp;

    std::cout << "Adjusting difficulty..." << std::endl;
    std::cout << "Previous block timestamp: " << chain[chain.size() - ADJUST_RATE].timeStamp << " ("
              << std::put_time(std::localtime(&chain[chain.size() - ADJUST_RATE].timeStamp), "%Y-%m-%d %H:%M:%S") << ")" << std::endl;
    std::cout << "Current block timestamp: " << chain.back().timeStamp << " ("
              << std::put_time(std::localtime(&chain.back().timeStamp), "%Y-%m-%d %H:%M:%S") << ")" << std::endl;
    std::cout << "Time taken: " << timeTaken << std::endl;
    std::cout << "Time expected: " << timeExpected << std::endl;

    if (timeTaken < timeExpected / 2) {
        difficulty++;
    } else if ((timeTaken > timeExpected * 2) && difficulty > 1) {
        difficulty = std::max(4, difficulty - 1);
    }


    std::cout << "New difficulty: " << difficulty << std::endl;
}

// BlockChain JSON Serialization and Deserialization

void to_json(nlohmann::json& j, const BlockChain& blockchain) {
    j = nlohmann::json{
            {"difficulty", blockchain.difficulty},
            {"chain", blockchain.chain}
    };
}

void from_json(const nlohmann::json& j, BlockChain& blockchain) {
    j.at("difficulty").get_to(blockchain.difficulty);

    blockchain.chain.clear();
    for (const auto& block_json : j.at("chain")) {
        Block block = block_json.get<Block>();
        blockchain.chain.push_back(block);
    }
}


void BlockChain::saveToFile(const std::string& filename) const {
    nlohmann::json j;
    to_json(j, *this);

    std::ofstream file(filename);
    if (file.is_open()) {
        file << j.dump(4);
        file.close();
    }
}

void BlockChain::loadFromFile(const std::string& filename) {
    std::ifstream file(filename);
    if (file.is_open()) {
        nlohmann::json j;
        file >> j;
        file.close();

        from_json(j, *this);
    }
}

std::string BlockChain::serialize() const {
    nlohmann::json j;
    to_json(j, *this); // Serialize the BlockChain object to JSON
    return j.dump(4);  // Return the JSON as a pretty-printed string (4 spaces indentation)
}

void BlockChain::deserialize(const std::string& jsonString) {
    nlohmann::json j = nlohmann::json::parse(jsonString); // Parse the JSON string
    from_json(j, *this); // Deserialize the JSON into the BlockChain object
}

