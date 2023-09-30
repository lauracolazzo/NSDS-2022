#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

/**
* Group number: 12
*
* Group members
* Laura Colazzo
* Alessandro Meroni
* Filippo Giovanni Del Nero
*
**/

// Set DEBUG 1 if you want car movement to be deterministic
#define DEBUG 0

const int num_segments = 256;

const int num_iterations = 1000;
const int count_every = 10;

const double alpha = 0.5;
const int max_in_per_sec = 10;

// Returns the number of car that enter the first segment at a given iteration.
int create_random_input() {
	#if DEBUG
		return 1;
	#else
		return rand() % max_in_per_sec;
	#endif
}

// Returns 1 if a car needs to move to the next segment at a given iteration, 0 otherwise.
int move_next_segment() {
	#if DEBUG
		return 1;
	#else
		return ((double)rand() / RAND_MAX) < alpha ? 1 : 0;
	#endif
}

// Given the number of cars in a segment, it computes the overall number of cars to move to the next one
int count_cars_to_move(int size){
	int count = 0;

	for(int i = 0; i < size; i++){
		if (move_next_segment())
			count++;
	}

	return count;
}

int main(int argc, char** argv) {
	MPI_Init(NULL, NULL);

	int rank;
	int num_procs;
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	MPI_Comm_size(MPI_COMM_WORLD, &num_procs);
	srand(time(NULL) + rank);

	int array_size = num_segments/num_procs;
	int *segments = (int *) malloc(sizeof(int) * array_size);

	// Simulate for num_iterations iterations
	for (int it = 1; it <= num_iterations; ++it) {
		
		int cars_moved = 0;
		// Move cars across segments
		for(int i = 0; i < array_size - 1; i++){
			cars_moved = count_cars_to_move(segments[i] - cars_moved);
			segments[i] -= cars_moved;
			segments[i+1] += cars_moved;
		}

		// Cars may exit from the last segment
		cars_moved = count_cars_to_move(segments[array_size-1] - cars_moved);
		segments[array_size-1] -= cars_moved;
		
		// All processes except for the last one send the number of cars moved from the last segment to the next process
		if (rank != (num_procs - 1)){
			MPI_Send(&cars_moved, 1, MPI_INT, (rank + 1), 0, MPI_COMM_WORLD);
		}
		
		// All processes except for process 0 receive from the previous process the number of cars to move in their first segment
		if (rank != 0){
			int cars_to_move;
			MPI_Recv(&cars_to_move, 1, MPI_INT, (rank - 1), MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
			segments[0] += cars_to_move;
		}

		// New cars may enter in the first segment
		if (rank == 0){
			segments[0] += create_random_input();
		}

		// When needed, compute the overall sum
		if (it%count_every == 0) {
			int global_sum = 0;
			int local_sum = 0;
			
			// Each process computes the local sum
			for(int i = 0; i < array_size; i++){
				local_sum += segments[i];
			}
			
			// Process 0 collects local sums and computes the global sum
			MPI_Reduce(&local_sum, &global_sum, 1, MPI_INT, MPI_SUM, 0, MPI_COMM_WORLD);

			if (rank == 0) {
				printf("Iteration: %d, sum: %d\n", it, global_sum);
			}
		}
	}
	
	// Deallocate dynamic variables
	free(segments);

	MPI_Barrier(MPI_COMM_WORLD);
	MPI_Finalize();
}
