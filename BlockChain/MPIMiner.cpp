#include <mpi.h>
#include "BlockChain.h"
#include <iostream>
#include <string>
#include <vector>

//DEBUG OPTIONS
#define DEBUG
#define PRINT_FREQUENCY 50000


#define INITIAL_DIFFICULTY 5
#define STOP_MINING_TAG 2


void mainProcess(int size, unsigned int difficulty) {
    int currentDifficulty = difficulty;
    BlockChain blockchain(currentDifficulty);

    while (true) {
        std::string previousHash = blockchain.chain.empty() ? std::string(SHA256_DIGEST_LENGTH * 2, '0') : blockchain.chain.back().currentHash;
        std::string data = "Block data: " + std::to_string(blockchain.chain.size() + 1);

        uint32_t rangeSize = UINT_MAX / (size - 1);
        uint32_t startNonce = 0;
        uint32_t endNonce = rangeSize;

        for (int i = 1; i < size; ++i) {
            MPI_Send(&blockchain.difficulty, 1, MPI_INT, i, 0, MPI_COMM_WORLD);
            MPI_Send(previousHash.c_str(), previousHash.size() + 1, MPI_CHAR, i, 0, MPI_COMM_WORLD);
            MPI_Send(data.c_str(), data.size() + 1, MPI_CHAR, i, 0, MPI_COMM_WORLD);

            MPI_Send(&startNonce, 1, MPI_UINT32_T, i, 0, MPI_COMM_WORLD);
            MPI_Send(&endNonce, 1, MPI_UINT32_T, i, 0, MPI_COMM_WORLD);

            startNonce = endNonce;
            endNonce = (i == size - 1) ? UINT_MAX : endNonce + rangeSize;
        }

        std::cout << "Main process sent mining data to workers\n";

        Block receivedBlock;
        MPI_Status status;
        char blockBuffer[1024];
        MPI_Recv(blockBuffer, sizeof(blockBuffer), MPI_BYTE, MPI_ANY_SOURCE, 0, MPI_COMM_WORLD, &status);

        receivedBlock.deserialize(blockBuffer);

        int senderRank = status.MPI_SOURCE;
        std::cout << "Received block from process " << senderRank << "\n";

        if (receivedBlock.hasValidHashDifficulty() && receivedBlock.previousHash == previousHash) {
            blockchain.add(receivedBlock);
            std::cout << "Block added by process " << senderRank << " with hash: " << receivedBlock.currentHash << "\n";

            for (int i = 1; i < size; ++i) {
                int stopMessage = 1;
                if (i != senderRank) {
                    std::cout << "Sending stop message to process " << i << "\n";
                    MPI_Send(&stopMessage, 1, MPI_INT, i, STOP_MINING_TAG, MPI_COMM_WORLD);
                }
            }
        } else {
            std::cerr << "Received invalid block from process " << senderRank << "\n";
        }
    }
}


void workerProcess(int rank) {
    while (true) {
        int difficulty;
        char previousHash[SHA256_DIGEST_LENGTH * 2 + 1];
        char data[256];
        uint32_t startNonce, endNonce;
        int stopMining = 0;

        MPI_Recv(&difficulty, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        MPI_Recv(previousHash, sizeof(previousHash), MPI_CHAR, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        MPI_Recv(data, sizeof(data), MPI_CHAR, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        MPI_Recv(&startNonce, 1, MPI_UINT32_T, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        MPI_Recv(&endNonce, 1, MPI_UINT32_T, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);

        previousHash[SHA256_DIGEST_LENGTH * 2] = '\0';
        std::string previousHashStr(previousHash);
        Block minedBlock(rank, data, difficulty);
        minedBlock.previousHash = previousHashStr;

#ifdef DEBUG
        std::cout << "Worker process " << rank << " started mining for: " << data << " on range: " << startNonce << " - " << endNonce << "\n";
#endif

        while (startNonce <= endNonce) {
            MPI_Iprobe(0, STOP_MINING_TAG, MPI_COMM_WORLD, &stopMining, MPI_STATUS_IGNORE);

            if (stopMining) {
                // Assuming stop message is an int
                MPI_Recv(&stopMining, sizeof(int), MPI_INT, 0, STOP_MINING_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
                std::cout << "Worker process " << rank << " received stop message\n";
                break;
            }

#ifdef DEBUG
            if(minedBlock.nonce % PRINT_FREQUENCY == 0){
                std::cout << "Worker process " << rank << " is mining " << data << " with nonce: " << minedBlock.nonce << "\n";
            }
#endif

            minedBlock.nonce = startNonce++;
            minedBlock.currentHash = minedBlock.calculateHash();

            if (minedBlock.hasValidHashDifficulty()) {
                break;
            }
        }



        if (!stopMining && minedBlock.hasValidHashDifficulty()) {
            char blockBuffer[1024];
            minedBlock.serialize(blockBuffer);

            MPI_Send(blockBuffer, sizeof(blockBuffer), MPI_BYTE, 0, 0, MPI_COMM_WORLD);
        }

        if (stopMining) {
            stopMining = 0;
        }

    }
}

void printHelp() {
    std::cout << "Usage: MPIMiner [options]\n";
    std::cout << "Options:\n";
    std::cout << "  -h               Display this help message\n";
    std::cout << "  -t <threads>     Number of threads to use for mining\n";
    std::cout << "  -d <difficulty>  Difficulty of the blockchain\n";
}




int main(int argc, char* argv[]) {

    unsigned int threads = 1;
    unsigned int difficulty = 5;

    for (int i = 2; i < argc; i++) {
        std::string arg = argv[i];
        if (arg == "-t" && i + 1 < argc) {
            threads = std::stoi(argv[i + 1]);
            i++;
        }
        else if (arg == "-d" && i + 1 < argc) {
            difficulty = std::stoi(argv[i + 1]);
            i++;
        }
        else if(arg == "-h") {
            printHelp();
            exit(0);
        }
        else {
            std::cerr << "Unknown or incomplete argument: " << arg << std::endl;
            exit(1);
        }
    }

    // Initialize MPI
    MPI_Init(&argc, &argv);

    int worldRank, worldSize;
    MPI_Comm_rank(MPI_COMM_WORLD, &worldRank);
    MPI_Comm_size(MPI_COMM_WORLD, &worldSize);

    if (worldSize < 2) {
        std::cerr << "This program requires at least 2 MPI processes.\n";
        MPI_Abort(MPI_COMM_WORLD, 1);
    }

    if (worldRank == 0) {
        mainProcess(worldSize, difficulty);
    } else {
        workerProcess(worldRank);
    }

    // Finalize MPI
    MPI_Finalize();
    return 0;
}
