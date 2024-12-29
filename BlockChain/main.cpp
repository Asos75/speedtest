#include <iostream>
#include "BlockChain.h"

using namespace std;


int main() {

    BlockChain blockChain(1);
    while(true){
        Block block = BlockChain::mine(blockChain.chain.size(), "test", blockChain.difficulty);
        blockChain.add(block);
        blockChain.printLast();
    }

     if (!blockChain.validate()) {
            cout << "Validation failed!" << endl;
            break;
        }
    return 0;
}
