#ifndef BLOCKCHAIN_P2PCLIENT_H
#define BLOCKCHAIN_P2PCLIENT_H

#include <asio.hpp>
#include <iostream>
#include <vector>
#include <thread>
#include <atomic>
#include <mutex>
#include <unordered_set>

class P2PClient {
private:
    asio::io_context& io_context_;
    asio::ip::tcp::socket socket_;
    asio::ip::tcp::acceptor acceptor_;
    std::atomic<bool> running_;
    std::vector<std::shared_ptr<asio::ip::tcp::socket>> peer_sockets_;
    std::mutex socket_mutex_;
    std::thread accept_thread_;
    std::vector<std::thread> peer_threads_;
    std::unordered_set<std::string> received_messages_;
    std::mutex message_mutex_;
public:
    P2PClient(asio::io_context& io_context, short port);
    void start();
    void stop();
    void broadcast_message(const std::string& message);
    void connect_to_peer(const std::string& host, short port);

private:
    void accept_connection();
    void receive_message(const std::shared_ptr<asio::ip::tcp::socket>& peer_socket);
    void forward_message(const std::string& message, const std::shared_ptr<asio::ip::tcp::socket>& sender_socket);
};

#endif //BLOCKCHAIN_P2PCLIENT_H
