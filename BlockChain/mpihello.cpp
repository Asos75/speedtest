#include <mpi.h>
#include <iostream>

int main(int argc, char** argv) {
    // Initialize the MPI environment
    MPI_Init(&argc, &argv);

    // Get the rank (ID) of the process
    int worldRank;
    MPI_Comm_rank(MPI_COMM_WORLD, &worldRank);

    // Get the total number of processes
    int worldSize;
    MPI_Comm_size(MPI_COMM_WORLD, &worldSize);

    // Print a message from each process
    std::cout << "Hello, World! I am process " << worldRank << " of " << worldSize << std::endl;

    // Finalize the MPI environment
    MPI_Finalize();

    return 0;
}
