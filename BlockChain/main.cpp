#include <iostream>
#include "BlockChain.h"

using namespace std;

int main() {
    BlockChain blockChain(1);
    bool running = true;
    
    while(running) {
        Block block = BlockChain::mine(blockChain.chain.size(), "test", blockChain.difficulty);
        if (!blockChain.add(block)) {
            cout << "Failed to add block!" << endl;
            continue;
        }
        blockChain.printLast();

        if (!blockChain.validateParallel(blockChain.chain)) {
            cout << "Validation failed!" << endl;
            running = false;
        }
    }
    return 0;
}