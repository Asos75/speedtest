#include <mpi.h>
#include <curl/curl.h>
#include "BlockChain.h"
#include <iostream>
#include <string>
#include <vector>


//DEBUG OPTIONS
#define DEBUG_
#define PRINT_FREQUENCY 50000

#define INITIAL_DIFFICULTY 6
#define STOP_MINING_TAG 2
#define OPERATION_TAG 1

const int TAG_DIFFICULTY = 100;
const int TAG_PREVIOUS_HASH = 101;
const int TAG_DATA = 102;
const int TAG_BLOCK_ID = 103;
const int TAG_START_NONCE = 104;
const int TAG_END_NONCE = 105;
const int TAG_STOP_MINING = 200;
const int TAG_BLOCK = 300;
const int TAG_OPERATIONS = 400;


size_t WriteCallback(void* contents, size_t size, size_t nmemb, std::string* userData) {
    size_t totalSize = size * nmemb;
    userData->append((char*)contents, totalSize);
    return totalSize;
}

std::string httpGet(const std::string& url) {
    CURL* curl = curl_easy_init();
    std::string response;
    if (curl) {
        curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteCallback);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, &response);
        curl_easy_perform(curl);
        curl_easy_cleanup(curl);
    }
    return response;
}

void httpPost(const std::string& url, const std::string& jsonData) {
    CURL* curl = curl_easy_init();
    if (curl) {
        curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
        curl_easy_setopt(curl, CURLOPT_POSTFIELDS, jsonData.c_str());
        curl_easy_setopt(curl, CURLOPT_POSTFIELDSIZE, jsonData.size());

        struct curl_slist* headers = nullptr;
        headers = curl_slist_append(headers, "Content-Type: application/json");

        curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);

        CURLcode res = curl_easy_perform(curl);
        if (res != CURLE_OK) {
            std::cerr << "curl_easy_perform() failed: " << curl_easy_strerror(res) << "\n";
        }

        curl_slist_free_all(headers);
        curl_easy_cleanup(curl);
    }
}

void mainProcess(int size, unsigned int difficulty) {
    const std::string BASE_URL = "http://192.168.1.102:5000";

    int currentDifficulty = difficulty;
    BlockChain blockchain(currentDifficulty);

    // Load blockchain from server
    std::string blockchainJson = httpGet(BASE_URL + "/measurements/blockchain");

// Check if the response is empty
    if (blockchainJson.empty()) {
        std::cout << "No previous blockchain found on the server. Starting a new one.\n";
        // Initialize an empty blockchain
        blockchain = BlockChain(currentDifficulty);
    } else {
        try {
            blockchain.deserialize(blockchainJson);
            std::cout << "Loaded existing blockchain with " << blockchain.chain.size() << " blocks.\n";
        } catch (const std::exception& e) {
            std::cerr << "Error parsing blockchain data: " << e.what() << "\n";
            // Optionally: Start a new blockchain if parsing fails
            blockchain = BlockChain(currentDifficulty);
        }
    }



    while (true) {


        auto start = std::chrono::system_clock::now();
        std::string previousHash = blockchain.chain.empty() ? std::string(SHA256_DIGEST_LENGTH * 2, '0') : blockchain.chain.back().currentHash;
        std::string data = "Block data: " + std::to_string(blockchain.chain.size() + 1);

        uint32_t rangeSize = UINT_MAX / (size - 1);
        uint32_t startNonce = 0;
        uint32_t endNonce = rangeSize;

        unsigned int id = blockchain.chain.empty() ? 0 : blockchain.chain.back().id + 1;

        for (int i = 1; i < size; ++i) {
            MPI_Send(&blockchain.difficulty, 1, MPI_INT, i, 0, MPI_COMM_WORLD);
            MPI_Send(previousHash.c_str(), previousHash.size() + 1, MPI_CHAR, i, 0, MPI_COMM_WORLD);
            MPI_Send(data.c_str(), data.size() + 1, MPI_CHAR, i, 0, MPI_COMM_WORLD);
            MPI_Send(&id, 1, MPI_UINT32_T, i, 0, MPI_COMM_WORLD);

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
            std::cout << "<-------------------------------------------->\n";
            blockchain.printLast();
            std::cout << "<-------------------------------------------->\n";


            if(blockchain.validateParallel(blockchain.chain)){
                std::cout << "Blockchain is valid\n";
            } else {
                std::cout << "Blockchain is invalid\n";
            }
            for (int i = 1; i < size; ++i) {
                int stopMessage = 1;
                if (i != senderRank) {
#ifdef DEBUG
                    std::cout << "Sending stop message to process " << i << "\n";
#endif
                    MPI_Send(&stopMessage, 1, MPI_INT, i, STOP_MINING_TAG, MPI_COMM_WORLD);
                }
            }
        } else {
            std::cerr << "Received invalid block from process " << senderRank << "\n";
        }

        int totalOperations = 0;
        for (int i = 1; i < size; ++i) {
            int operations;
            MPI_Recv(&operations, 1, MPI_INT, i, TAG_OPERATIONS, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
            totalOperations += operations;
        }

        auto end = std::chrono::system_clock::now();
        auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(end - start).count();

        std::cout << "Operations/s: " << (float)totalOperations / ((float)duration / 1000) << "\n";

        if(receivedBlock.hasValidHashDifficulty()){
            blockchainJson = blockchain.serialize();
            httpPost(BASE_URL + "/measurements/blockchain", blockchainJson);
        }

    }
}

void miningTask(unsigned int threadId, unsigned int blockId, char* data, unsigned int difficulty, std::string& previousHash, uint32_t startNonce, uint32_t endNonce, std::atomic<bool>& stopMiningFlag, Block& minedBlock, std::atomic<uint64_t>& operationsInThread){
    char* localData = new char[256];
    for(int i = 0; i < 256; i++){
        localData[i] = data[i];
    }
    Block block(blockId, localData, difficulty);
    block.previousHash = previousHash;

    uint64_t operations = 0;

    while (startNonce <= endNonce && !stopMiningFlag) {
        block.nonce = startNonce++;
        block.currentHash = block.calculateHash();
        operations++;
        if(operations % 1000000 == 0){
            std::cout << "Thread: " << threadId << " Operations: " << operations << std::endl;
        }
        if (!stopMiningFlag && block.hasValidHashDifficulty()) {
            minedBlock = block;
            stopMiningFlag = true;
            break;
        }
    }

    operationsInThread.fetch_add(operations, std::memory_order_relaxed);

}


void workerProcess(int rank, unsigned int numThreads) {
    while (true) {
        int difficulty;
        char previousHash[SHA256_DIGEST_LENGTH * 2 + 1];
        char data[256];
        uint32_t startNonce, endNonce;
        int stopMining = 0;
        int operations = 0;
        unsigned int blockId;

        std::atomic<bool> stopMiningFlag(false);
        std::atomic<uint64_t> operationsInThread(0);



        MPI_Recv(&difficulty, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        MPI_Recv(previousHash, sizeof(previousHash), MPI_CHAR, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        MPI_Recv(data, sizeof(data), MPI_CHAR, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        MPI_Recv(&blockId, 1, MPI_UINT32_T, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        MPI_Recv(&startNonce, 1, MPI_UINT32_T, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        MPI_Recv(&endNonce, 1, MPI_UINT32_T, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);

        previousHash[SHA256_DIGEST_LENGTH * 2] = '\0';
        std::string previousHashStr(previousHash);
        Block minedBlock(blockId, data, difficulty);
        minedBlock.previousHash = previousHashStr;

#ifdef DEBUG
        std::cout << "Worker process " << rank << " started mining for: " << data << " on range: " << startNonce << " - " << endNonce << "\n";
#endif

        std::vector <std::thread> threads;
        uint32_t chunkSize = endNonce - (startNonce + 1);
        if(numThreads > 1) chunkSize = (endNonce == UINT_MAX) ? (UINT_MAX - (startNonce + 1) + 1) / numThreads : (endNonce - (startNonce + 1) + 1) / numThreads;
        uint32_t start = startNonce;
        uint32_t end = start + chunkSize;

        for(int i = 0; i < numThreads; i++){
              threads.emplace_back(miningTask, i, blockId, std::ref(data), difficulty, std::ref(previousHashStr), start, end, std::ref(stopMiningFlag), std::ref(minedBlock), std::ref(operationsInThread));
              start = end + 1;
              end = (i == numThreads - 2) ? endNonce : end + chunkSize;
        }


        while (!stopMiningFlag) {
            MPI_Iprobe(0, STOP_MINING_TAG, MPI_COMM_WORLD, &stopMining, MPI_STATUS_IGNORE);
            if (stopMining) {
                MPI_Recv(&stopMining, sizeof(int), MPI_INT, 0, STOP_MINING_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
                stopMiningFlag = true;
#ifdef DEBUG
                std::cout << "Worker process " << rank << " received stop message\n";
#endif
                break;
            }
        }

        for (auto& t : threads) {
            if(t.joinable()) {
                t.join();
            }
        }

        operations = operationsInThread;

        if (!stopMining && minedBlock.hasValidHashDifficulty()) {
            char blockBuffer[1024];
            minedBlock.serialize(blockBuffer);
            MPI_Send(blockBuffer, sizeof(blockBuffer), MPI_BYTE, 0, 0, MPI_COMM_WORLD);
        }

        MPI_Send(&operations, 1, MPI_INT, 0, TAG_OPERATIONS, MPI_COMM_WORLD);
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
    unsigned int difficulty = INITIAL_DIFFICULTY;

    for (int i = 1; i < argc; i++) {
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
        workerProcess(worldRank, threads);
    }

    // Finalize MPI
    MPI_Finalize();
    return 0;
}