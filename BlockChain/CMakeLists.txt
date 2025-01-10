cmake_minimum_required(VERSION 3.24)
project(BlockChain)

set(CMAKE_CXX_STANDARD 23)

# Set paths to external libraries
# Asio headers are located in /asio/include
set(ASIO_INCLUDE_DIR "${CMAKE_SOURCE_DIR}/libs/asio-1.30.2/include")

# Find OpenSSL package
find_package(OpenSSL REQUIRED)

# Include directories
include_directories(${ASIO_INCLUDE_DIR} ${OPENSSL_INCLUDE_DIR})

# Define your executable
add_executable(BlockChain main.cpp Block.h BlockChain.h P2PClient.h P2PClient.cpp MiningPool.h MPIMiner.cpp mpihello.cpp BlockChain.cpp Block.cpp)

# Link OpenSSL libraries
target_link_libraries(BlockChain
        OpenSSL::SSL
        OpenSSL::Crypto
        )

# If you're using multi-threading with Asio, link to pthread (for Linux/macOS)
if(UNIX)
    target_link_libraries(BlockChain pthread)
endif()

# Link Winsock library (for Windows)
if(WIN32)
    target_link_libraries(BlockChain ws2_32)
    add_definitions(-D_WIN32_WINNT=0x0601)  # for Windows 7 or higher
endif()

# Optionally, specify OpenSSL library path if not automatically detected
link_directories(${OPENSSL_LIB_DIR})
