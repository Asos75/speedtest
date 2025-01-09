#include <iostream>
#include "BlockChain.h"
#include "P2PClient.h"
#include "MiningPool.h"

using namespace std;

#define BLOCKCHAIN_PARALLEL_TEST




int main() {

#ifdef BLOCKCHAIN_TEST
    BlockChain blockChain(1);
    bool running = true;

    while(running) {
        Block block = BlockChain::mineParallel(blockChain.chain, "Hello, World!", blockChain.difficulty, 8);
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
#endif

#ifdef BLOCKCHAIN_PARALLEL_TEST

    int difficulty = 4;
    int numThreads = 16;

    BlockChain blockChain(difficulty);

    MiningPool pool(blockChain, numThreads);

    for(int i = 0; i < 100; i++){
        pool.enqueue("Data: " + std::to_string(i));
    }



    return 0;
#endif

#ifdef P2P_TEST
    try {
        asio::io_context io_context;

        short port;
        std::cout << "Enter your listening port: ";
        std::cin >> port;
        std::cin.ignore(); // Ignore leftover newline character

        P2PClient client(io_context, port);

        std::thread listener_thread(&P2PClient::start, &client);

        while (true) {
            std::cout << "\nMenu:\n";
            std::cout << "1. Connect to a peer\n";
            std::cout << "2. Send a message\n";
            std::cout << "3. Exit\n";
            std::cout << "Choose an option: ";

            int choice;
            std::cin >> choice;
            std::cin.ignore(); // Ignore leftover newline character

            if (choice == 1) {
                // Connect to another peer
                std::string peer_host;
                short peer_port;
                std::cout << "Enter peer host (e.g., 127.0.0.1): ";
                std::cin >> peer_host;
                std::cout << "Enter peer port: ";
                std::cin >> peer_port;
                std::cin.ignore(); // Ignore leftover newline character

                client.connect_to_peer(peer_host, peer_port);
            } else if (choice == 2) {
                // Send a message
                std::string message;
                std::cout << "Enter message to send: ";
                std::getline(std::cin, message);

                if (!message.empty()) {
                    client.broadcast_message(message);
                } else {
                    std::cout << "Message cannot be empty.\n";
                }
            } else if (choice == 3) {
                // Exit the program
                std::cout << "Shutting down...\n";
                client.stop();
                listener_thread.join();
                break;
            } else {
                std::cout << "Invalid option. Please try again.\n";
            }
        }
    } catch (std::exception& e) {
        std::cerr << "Error: " << e.what() << std::endl;
    }

#endif
}