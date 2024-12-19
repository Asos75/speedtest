cmake_minimum_required(VERSION 3.24)
project(BlockChain)

set(CMAKE_CXX_STANDARD 23)

# Find OpenSSL package
find_package(OpenSSL REQUIRED)

# Define your executable
add_executable(BlockChain main.cpp Block.h)

# Link OpenSSL libraries
target_link_libraries(BlockChain
        OpenSSL::SSL
        OpenSSL::Crypto
        )
