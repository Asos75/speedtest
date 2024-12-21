//
// Created by andra on 19/12/2024.
//

#ifndef BLOCKCHAIN_BLOCKCHAIN_H
#define BLOCKCHAIN_BLOCKCHAIN_H


#include <vector>
#include "Block.h"

class BlockChain {
private:
    static const int MINING_RATE = 10000;
    static const int ADJUST_RATE = 10;
    static const int MAX_TIME_DIFF = 60000;
public:
    std::vector<Block> chain;
    int difficulty;

    BlockChain(int difficulty) : difficulty(difficulty) {
        chain = std::vector<Block>();
    }

    void add(Block block) {
        if(chain.empty()) {
            block.previousHash = std::string(SHA256_DIGEST_LENGTH * 2, '0');
        } else {
            block.previousHash = chain.back().currentHash;
        }

        chain.push_back(block);
        if(chain.size() % ADJUST_RATE == 0) {
            long timeExpected = MINING_RATE * ADJUST_RATE;
            long timeTaken = chain.back().timeStamp - chain[chain.size() - ADJUST_RATE].timeStamp;
            if(timeTaken < timeExpected / 2) {
                difficulty++;
            } else if(timeTaken > timeExpected * 2) {
                difficulty--;
            }
        }
    }

    void printLast() {
        chain.back().print();
    }

    static Block mine(int id, std::string data, int difficulty) {
        Block block(id, data, difficulty);
        while(true) {
            block.nonce++;
            block.currentHash = block.calculateHash();
            if(block.currentHash.substr(0, difficulty) == std::string(difficulty, '0')) {
                return block;
            }
        }
    }

};


#endif //BLOCKCHAIN_BLOCKCHAIN_H
