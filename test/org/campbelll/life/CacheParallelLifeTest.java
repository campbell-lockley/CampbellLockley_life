/* ************************************************************************* *
 * Name:		CacheParallelLifeTest.java
 * Description:	Test cases for CacheParallelLife.java
 * Author:		Campbell Lockley		StudentID: 1178618
 * Date:		05/06/15
 * ************************************************************************* */
package org.campbelll.life;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.Test;

import static org.campbelll.life.Life.ALIVE;
import static org.campbelll.life.Life.DEAD;;

/**
 * Test cases for {@link CacheParallelLife}.
 * <p>
 * It is significantly harder to test this implementation of {@link Life}, so 
 * there are no tests for {@link CacheParallelLife#age() age()} or wrapping.
 * 
 * @author Campbell Lockley
 */
public class CacheParallelLifeTest {

	/**
	 * Tests {@link CacheParallelLife#call()}.
	 * <p>
	 * {@link CacheParallelLife#call() call()} computes the next generation for 
	 * a block of the board.
	 * 
	 * @throws FileFormatException if pattern file is incorrectly formatted.
	 * @throws IOException if there is an I/O error.
	 */
	@Test
	public void testCall() throws IOException, FileFormatException {
		final String msg = "call() didn't compute correctly";
		
		/* Instantiate class under test */
		final int boardDim = 1024;
		final int cacheSize = boardDim;
		final int numThreads = Runtime.getRuntime().availableProcessors();
		CacheParallelLife life = 
				new CacheParallelLife(boardDim, numThreads, cacheSize);

		/*
		 * Load test pattern - blinker.patt:
		 * 	1:1 2
		 * 	2:###
		 */
		InputStream in = CacheParallelLifeTest.class
				.getResourceAsStream("/blinker.patt");
		life.loadPattern(in);
		
		/* Set up args for call() */
		final char[] nextGen = new char[life.board.length];
		Arrays.fill(nextGen, DEAD);
		final char[] expected = new char[life.board.length];
		Arrays.fill(expected, DEAD);
		expected[2055] = ALIVE;
		expected[3081] = ALIVE;
		expected[4107] = ALIVE;
		final int start = boardDim + 2 + 1;
		CacheParallelLife job = new CacheParallelLife(life.board, nextGen, 
				life.neighbours, boardDim, start, cacheSize, cacheSize);
		
		/* Run method under test and test result */
		job.call();
		assertArrayEquals(msg, expected, nextGen);
	}

}
