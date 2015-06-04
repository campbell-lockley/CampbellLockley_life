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
 * Blocks of the board are processed in parallel.
 * 
 * @author Campbell Lockley
 */
public class BlockParallelLife extends ParallelLife {
	/* Parameters for call() */
	private int start, width, height;

	/**
	 * Constructor. Use when intending to use as a 
	 * {@link java.util.concurrent.Callable Callable}.
	 * 
	 * @param board Pointer to pre-existing board.
	 * @param nextGen Pointer to accompanying nextGen.
	 * @param neighbours Pointer to pre-computed neighbour indexes
	 * @param boardDim Size of board dimension.
	 * @param start Offset into board[] to start at.
	 * @param width Width of block.
	 * @param height Height of block.
	 * @see java.util.concurrent.Callable
	 */
	public BlockParallelLife(char[] board, char[] nextGen, int[] neighbours, 
			int boardDim, int start, int width, int height) {
		this.start = start;
		this.width = width;
		this.height = height;
	}

	/**
	 * Constructor.
	 * 
	 * @param boardDim Size of board dimension.
	 */
	public BlockParallelLife(int boardDim) {
		super(boardDim);
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
		
		/* Create jobs where each job is  */
		// TODO: Split up jobs

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
	public Object call() throws Exception {
		/* Do a block of cells */
		// TODO: Compute next generation for a block of cells
		
		return null;
	}

}
