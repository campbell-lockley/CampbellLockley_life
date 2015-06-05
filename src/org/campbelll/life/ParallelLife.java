/*
 * Name:		ParallelLife.java
 * Description:	Interface for parallel Game of Life implementations.
 * Author:		Campbell Lockley		studentID: 1178618
 * Date:		04/06/15
 */
package org.campbelll.life;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Abstract class representing parallel Conway's Game of Life implementations.
 * <p>
 * All parallel {@link Life Lifes} have a thread pool and submit instances of 
 * themselves as jobs to the thread pool.
 * <p>
 * Once a ParallelLife has finished being used {@link #cleanUp()} must be 
 * called to shutdown the thread pool.
 * 
 * @author Campbell Lockley
 */
public abstract class ParallelLife extends Life implements Callable<Object> {
	/** Timeout for thread pool in milliseconds */
	public final static long timeout = 10000;
	
	/* Thread pool for parallel execution */
	protected ExecutorService pool;
	
	/** Default Constructor. */
	protected ParallelLife() {
	}

	/**
	 * Constructor.
	 * 
	 * @param boardDim Size of board dimension.
	 * @param numThreads Number of threads for the thread pool to use.
	 */
	public ParallelLife(int boardDim, int numThreads) {
		super(boardDim);
		
		/* Use number of CPUs as the number of threads for the thread pool */
		this.pool = Executors.newFixedThreadPool(numThreads);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Shuts down thread pool and terminates threads. 
	 */
	@Override
	public void cleanUp() {
		pool.shutdownNow();
	}

}
