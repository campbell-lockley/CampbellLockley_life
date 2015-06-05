/* ************************************************************************* *
 * Name:		CacheParallelLife.java
 * Description:	Parallel implementation of game of life which ages by splitting 
 * 				the board into blocks which are matched to the cache size and 
 * 				submitting a job for each to a thread pool.
 * Author:		Campbell Lockley		StudentID: 1178618
 * Date:		04/06/15
 * ************************************************************************* */
package org.campbelll.life;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Cache parallel implementation of {@link Life}.
 * <p>
 * Blocks of the board are processed in parallel. The board is split into 
 * blocks, where each block is calculated to take up 1/4 of the L1 cache.
 * <p>
 * CacheParallelLife has very poor performance as the blocks are so small that 
 * threads spend most of their time waiting for new jobs.
 * 
 * @author Campbell Lockley
 */
public class CacheParallelLife extends ParallelLife {
	/* Dimension of block */
	private int blockDim;
	
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
	 * @param width Width of block to compute.
	 * @param height Height of block to compute.
	 * @see java.util.concurrent.Callable
	 */
	public CacheParallelLife(char[] board, char[] nextGen, int[] neighbours, 
			int boardDim, int start, int width, int height) {
		this.board = board;
		this.nextGen = nextGen;
		this.neighbours = neighbours;
		this.boardDim = boardDim;
		this.start = start;
		this.width = width;
		this.height = height;
	}

	/**
	 * Constructor.
	 * <p>
	 * The cache size must be cleanly divisible by 4. Theoretically the L2 or 
	 * L3 cache size (in KB) could also be used to get a similar effect. The L3
	 * cache size should be divided by the number of threads used by the thread 
	 * pool.
	 * 
	 * @param boardDim Size of board dimension.
	 * @param numThreads Number of threads for the thread pool to use.
	 * @param l1CacheSize Size of the L1 cache in KB for the CPU this program 
	 * is being run on.
	 */
	public CacheParallelLife(int boardDim, int numThreads, int l1CacheSize) {
		super(boardDim, numThreads);
		
		/* Calculate block width so a single block line is 1/4 of L1 cache */
		assert (l1CacheSize % 4 == 0);
		this.blockDim = ((l1CacheSize * 1024) / 4) / 2;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation of age() submits (tiny) blocks of the board as jobs 
	 * to a thread pool and waits for them to be processed.
	 * 
	 * @throws TimeoutException if a blocking method call in age() times out, 
	 * causing age() to fail.
	 */
	@Override
	public void age() throws TimeoutException {
		ArrayList<CacheParallelLife> jobs = 
				new ArrayList<CacheParallelLife>(boardDim);
		
		/* Create jobs where each block is 1/4 of the L1 cache wide */
		int y, x, start, rem = boardDim % blockDim;
		for (y = 0; y < (boardDim - 1); y += blockDim) {
			for (x = 0; x < (boardDim - 1); x += blockDim) {
				start = (y*blockDim + 1) * (boardDim + 2) + x*blockDim + 1;
				jobs.add(new CacheParallelLife(board, nextGen, neighbours, 
						boardDim, start, blockDim, blockDim));
			} 
			/* Check x bounds */
			start = (y*blockDim + 1) * (boardDim + 2) + x*blockDim + 1;
			if (rem == 0) {
				jobs.add(new CacheParallelLife(board, nextGen, neighbours, 
						boardDim, start, blockDim, blockDim));
			} else {
				jobs.add(new CacheParallelLife(board, nextGen, neighbours, 
						boardDim, start, rem, blockDim));
			}
		}
		/* Check y bounds */
		for (x = 0; x < (boardDim - 1); x += blockDim) {
			start = (y*blockDim + 1) * (boardDim + 2) + x*blockDim + 1;
			if (rem == 0) {
				jobs.add(new CacheParallelLife(board, nextGen, neighbours, 
						boardDim, start, blockDim, blockDim));
			} else {
				jobs.add(new CacheParallelLife(board, nextGen, neighbours, 
						boardDim, start, blockDim, rem));
			}
		}
		/* Check x-y bounds */
		start = (y*blockDim + 1) * (boardDim + 2) + x*blockDim + 1;
		if (rem == 0) {
			jobs.add(new CacheParallelLife(board, nextGen, neighbours, 
					boardDim, start, blockDim, blockDim));
		} else {
			jobs.add(new CacheParallelLife(board, nextGen, neighbours, 
					boardDim, start, rem, rem));
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
		for (int y = 0; y < height; y++) {
			index = start + y * (boardDim + 2);
			for (int x = 0; x < width; x++) {
				nextGen[index + x] = live(index + x);
			}
		}
		
		return null;	// Nothing to return
	}

}
