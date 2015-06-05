/*
 * Name:		BlocklParallelLife.java
 * Description:	Parallel implementation of game of life which ages by splitting 
 * 				the board into blocks and submitting a job for each to a thread 
 * 				pool.
 * Author:		Campbell Lockley		StudentID: 1178618
 * Date:		04/06/15
 */
package org.campbelll.life;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Block parallel implementation of {@link Life}.
 * <p>
 * Blocks of the board are processed in parallel. Blocks are generated by 
 * splitting the board 32 ways along its x and y axis.
 * 
 * @author Campbell Lockley
 */
public class BlockParallelLife extends ParallelLife {
	/* Board is split into 32 x 32 blocks */
	protected final static int numDivisions = 32;
	
	/* Parameters for call() */
	private int start, blockDim;

	/**
	 * Constructor. Use when intending to use as a 
	 * {@link java.util.concurrent.Callable Callable}.
	 * 
	 * @param board Pointer to pre-existing board.
	 * @param nextGen Pointer to accompanying nextGen.
	 * @param neighbours Pointer to pre-computed neighbour indexes
	 * @param boardDim Size of board dimension.
	 * @param blockDim Size of block dimensions.
	 * @param start Offset into board[] to start at.
	 * @see java.util.concurrent.Callable
	 */
	public BlockParallelLife(char[] board, char[] nextGen, int[] neighbours, 
			int boardDim, int blockDim, int start) {
		this.board = board;
		this.nextGen = nextGen;
		this.neighbours = neighbours;
		this.boardDim = boardDim;
		this.blockDim = blockDim;
		this.start = start;
	}

	/**
	 * Constructor.
	 * <p>
	 * For BlockParallelLife the dimensions of the game of life board must be 
	 * at least 32.
	 * 
	 * @param boardDim Size of board dimension.
	 * @param numThreads Number of threads for the thread pool to use.
	 */
	public BlockParallelLife(int boardDim, int numThreads) {
		super(boardDim, numThreads);
		
		/* For BlocklParallelLife the board dimensions must be at least 32 */
		assert (boardDim >= numDivisions);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation of age() submits blocks of the board as jobs to a 
	 * thread pool and waits for them to be processed. The thread pool queries 
	 * the Java runtime and uses a 1:1 ratio of threads to CPU's.
	 * 
	 * @throws TimeoutException if a blocking method call in age() times out, 
	 * causing age() to fail.
	 */
	@Override
	public void age() throws TimeoutException {
		ArrayList<BlockParallelLife> jobs = 
				new ArrayList<BlockParallelLife>(boardDim);
		
		/* Create jobs where each job is a 32^2 way split of the board */
		int blockDim = boardDim / numDivisions;
		for (int y = 0; y < numDivisions; y++) {
			for (int x = 0; x < numDivisions; x++) {
				jobs.add(new BlockParallelLife(board, nextGen, neighbours, 
						boardDim, blockDim,
						(y*blockDim + 1) * (boardDim + 2) + x*blockDim + 1));
			}
		}

		/* Submit the jobs to executor and wait for completion */
		try {
			pool.invokeAll(jobs, timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			/* Shutdown thread pool and pass on interrupt */
			pool.shutdownNow();
			Thread.currentThread().interrupt();
		}
		
		/* Copy edges to handle wrapping */
		copyEdges(nextGen);
		
		/* Swap boards over */
		char[] tmp = board;
		board = nextGen;
		nextGen = tmp;
	}
	
	/**
	 * Computes next generation for a block of the board.
	 * <p> 
	 * Implementation of call() method in 
	 * {@link java.util.concurrent.Callable Callable} interface. Should be used
	 * only when instantiated specifically as a Callable.
	 * 
	 * @return null always.
	 */
	@Override
	public Object call() {
		/* Do a block of cells */
		int index;
		for (int y = 0; y < blockDim; y++) {
			index = start + y * (boardDim + 2);
			for (int x = 0; x < blockDim; x++) {
				nextGen[index + x] = live(index + x);
			}
		}
		
		return null;	// Nothing to return
	}

}