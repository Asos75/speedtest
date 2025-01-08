#include "P2PClient.h"
#include <functional>
#include <sstream>
#include <openssl/sha.h>

std::string generate_message_id(const std::string& message) {
    unsigned char hash[SHA256_DIGEST_LENGTH];
    SHA256(reinterpret_cast<const unsigned char*>(message.c_str()), message.size(), hash);
    std::ostringstream oss;
    for (unsigned char i : hash) {
        oss << std::hex << static_cast<int>(i);
    }
    return oss.str();
}

P2PClient::P2PClient(asio::io_context& io_context, short port)
        : io_context_(io_context),
          socket_(io_context),
          acceptor_(io_context, asio::ip::tcp::endpoint(asio::ip::tcp::v4(), port)),
          running_(false) {}

void P2PClient::start() {
    running_ = true;
    std::cout << "Starting peer-to-peer network..." << std::endl;

    accept_thread_ = std::thread(&P2PClient::accept_connection, this);

    io_context_.run();
}

void P2PClient::stop() {
    running_ = false;

    {
        std::lock_guard<std::mutex> lock(socket_mutex_);
        for (auto& socket : peer_sockets_) {
            if (socket->is_open()) {
                socket->close();
            }
        }
    }

    acceptor_.close();
    socket_.close();

    if (accept_thread_.joinable()) {
        accept_thread_.join();
    }
    for (auto& thread : peer_threads_) {
        if (thread.joinable()) {
            thread.join();
        }
    }

    std::cout << "Peer-to-peer network stopped." << std::endl;
}

void P2PClient::broadcast_message(const std::string& message) {
    std::lock_guard<std::mutex> lock(socket_mutex_);
    for (const auto& peer_socket : peer_sockets_) {
        try {
            asio::write(*peer_socket, asio::buffer(message));
        } catch (std::exception& e) {
            std::cerr << "Error broadcasting message: " << e.what() << std::endl;
        }
    }
}

void P2PClient::connect_to_peer(const std::string& host, short port) {
    try {
        auto peer_socket = std::make_shared<asio::ip::tcp::socket>(io_context_);
        asio::ip::tcp::resolver resolver(io_context_);
        asio::connect(*peer_socket, resolver.resolve(host, std::to_string(port)));

        {
            std::lock_guard<std::mutex> lock(socket_mutex_);
            peer_sockets_.push_back(peer_socket);
        }

        peer_threads_.emplace_back(&P2PClient::receive_message, this, peer_socket);

        std::cout << "Connected to peer: " << host << ":" << port << std::endl;
    } catch (std::exception& e) {
        std::cerr << "Error connecting to peer: " << e.what() << std::endl;
    }
}

void P2PClient::accept_connection() {
    try {
        while (running_) {
            auto peer_socket = std::make_shared<asio::ip::tcp::socket>(io_context_);
            acceptor_.accept(*peer_socket);

            {
                std::lock_guard<std::mutex> lock(socket_mutex_);
                peer_sockets_.push_back(peer_socket);
            }

            peer_threads_.emplace_back(&P2PClient::receive_message, this, peer_socket);

            std::cout << "Accepted connection from a new peer." << std::endl;
        }
    } catch (std::exception& e) {
        if (running_) {
            std::cerr << "Error accepting connection: " << e.what() << std::endl;
        }
    }
}

void P2PClient::receive_message(const std::shared_ptr<asio::ip::tcp::socket>& peer_socket) {
    char buffer[1024];
    try {
        while (running_) {
            size_t length = peer_socket->read_some(asio::buffer(buffer));
            std::string message(buffer, length);

            std::string message_id = generate_message_id(message);

            {
                std::lock_guard<std::mutex> lock(message_mutex_);
                if (received_messages_.count(message_id)) {
                    continue;
                }
                received_messages_.insert(message_id);
            }

            std::cout << "Received message: " << message << std::endl;

            forward_message(message, peer_socket);
        }
    } catch (std::exception& e) {
        if (running_) {
            std::cerr << "Error receiving message: " << e.what() << std::endl;
        }
    }
}

void P2PClient::forward_message(const std::string& message, const std::shared_ptr<asio::ip::tcp::socket>& sender_socket) {
    std::lock_guard<std::mutex> lock(socket_mutex_);
    for (const auto& peer_socket : peer_sockets_) {
        if (peer_socket != sender_socket) {
            try {
                asio::write(*peer_socket, asio::buffer(message));
            } catch (std::exception& e) {
                std::cerr << "Error forwarding message: " << e.what() << std::endl;
            }
        }
    }
}
