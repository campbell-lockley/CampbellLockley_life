/*
 * Name:		LineParallelLife.java
 * Description:	Parallel implementation of game of life which ages by splitting 
 * 				the board into lines and submitting a job for each to a thread 
 * 				pool.
 * Author:		Campbell Lockley		StudentID: 1178618
 * Date:		03/06/15
 */
package org.campbelll.life;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Line parallel implementation of {@link Life}.
 * <p>
 * Each line of the board is processed in parallel.
 * 
 * @author Campbell Lockley
 */
public class LineParallelLife extends ParallelLife {
	/* Parameter for call() */
	private int line = 0;
	
	/**
	 * Constructor.
	 * 
	 * @param boardDim Size of board dimension.
	 * @param numThreads Number of threads for the thread pool to use.
	 */
	public LineParallelLife(int boardDim, int numThreads) {
		super(boardDim, numThreads);
	}
	
	/**
	 * Constructor. Use when intending to use as a 
	 * {@link java.util.concurrent.Callable Callable}.
	 * 
	 * @param board Pointer to pre-existing board.
	 * @param nextGen Pointer to accompanying nextGen.
	 * @param neighbours Pointer to pre-computed neighbour indexes
	 * @param boardDim Size of board dimension.
	 * @param line Line number of board to compute.
	 * @see java.util.concurrent.Callable
	 */
	protected LineParallelLife(char[] board, char[] nextGen, int[] neighbours,
			int boardDim, int line) {
		this.board = board;
		this.nextGen = nextGen;
		this.neighbours = neighbours;
		this.boardDim = boardDim;
		this.line = line;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation of age() submits each line of the board as a job to 
	 * a thread pool and waits for every line to be computed. The thread pool 
	 * queries the Java runtime and uses a 1:1 ratio of threads to CPU's.
	 * 
	 * @throws TimeoutException if a blocking method call in age() times out, 
	 * causing age() to fail.
	 */
	@Override
	public void age() throws TimeoutException {
		ArrayList<LineParallelLife> jobs = 
				new ArrayList<LineParallelLife>(boardDim);
		
		/* Create jobs where each job is a line of the board */
		for (int y = 0; y < boardDim; y++) {
			jobs.add(new LineParallelLife(board, nextGen, neighbours, 
					boardDim, y));
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
	 * Computes next generation for a line of the board.
	 * <p> 
	 * Implementation of call() method in 
	 * {@link java.util.concurrent.Callable Callable} interface. Should be used
	 * only when instantiated specifically as a Callable.
	 * 
	 * @return null always.
	 */
	public Object call() {
		/* Do a line of cells */
		int index = (line + 1) * (boardDim + 2) + 1;
		for (int i = index; i < (index + boardDim); i++) {
			nextGen[i] = live(i);
		}
		
		return null;	// Nothing to return
	}

}
