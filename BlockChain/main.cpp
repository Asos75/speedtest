#include <iostream>
#include "BlockChain.h"


int main() {

    BlockChain blockChain(1);
    while(true){
        Block block = BlockChain::mine(blockChain.chain.size(), "test", blockChain.difficulty);
        blockChain.add(block);
        blockChain.printLast();
    }
    return 0;
}
