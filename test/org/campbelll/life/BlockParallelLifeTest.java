/*
 * Name:		BlockParallelLifeTest.java
 * Description:	Test cases for BlockParallelLife.java
 * Author:		Campbell Lockley		StudentID: 1178618
 * Date:		04/06/15
 */
package org.campbelll.life;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import static org.campbelll.life.Life.ALIVE;
import static org.campbelll.life.Life.DEAD;;

/**
 * Test cases for {@link BlockParallelLife}.
 * 
 * @author Campbell Lockley
 */
public class BlockParallelLifeTest {

	/**
	 * Tests {@link BlockParallelLife#call()}.
	 * <p>
	 * {@link BlockParallelLife#call() call()} computes the next generation for 
	 * a block of the board.
	 * 
	 * @throws FileFormatException if pattern file is incorrectly formatted.
	 * @throws IOException if there is an I/O error.
	 */
	@Test
	public void testCall() throws IOException, FileFormatException {
		final String msg = "call() didn't compute correctly";
		
		/* Instantiate class under test */
		final int boardDim = 32;
		BlockParallelLife life = new BlockParallelLife(boardDim);

		/*
		 * Load test pattern - blinker.patt:
		 * 	1:1 2
		 * 	2:###
		 */
		InputStream in = BlockParallelLifeTest.class
				.getResourceAsStream("/blinker.patt");
		life.loadPattern(in);
		
		/* Setup args for call() */
		final char[] nextGen = new char[life.board.length];
		final char[] expected = new char[(32 + 2) * (32 + 2)];
		final char[] v = { DEAD, DEAD, ALIVE, DEAD, DEAD };
		final int blockDim = boardDim / BlockParallelLife.numDivisions;
		final int start = 103;
		
		/* Run a vector of tests over call() */
		int index = 0;
		for (int i = start; i < (start + v.length); i++) {
			expected[i] = v[index++];
			BlockParallelLife job = new BlockParallelLife(life.board, nextGen, 
					life.neighbours, boardDim, blockDim, i);
			job.call();
			assertArrayEquals(msg, expected, nextGen);	
		}
	}

	/**
	 * Tests {@link BlockParallelLife#age()}.
	 * <p>
	 * {@link BlockParallelLife#age() age()} is tested using the small pattern 
	 * file blinker.patt which is a period 2 oscillator.
	 * 
	 * @throws TimeoutException if {@link BlockParallelLife#age() age()} times 
	 * out.
	 * @throws FileFormatException if pattern file is incorrectly formatted.
	 * @throws IOException if there is an I/O error.
	 */
	@Test
	public void testAge() 
			throws IOException, FileFormatException, TimeoutException {
		final String msg = "age() didn't compute next generation correctly";
		
		/* Instantiate class under test */
		int boardDim = 64;
		BlockParallelLife life = new BlockParallelLife(boardDim);
		
		/*
		 * Load test pattern - blinker.patt:
		 * 	1:1 2
		 * 	2:###
		 */
		InputStream in = BlockParallelLifeTest.class
				.getResourceAsStream("/blinker.patt");
		life.loadPattern(in);
		
		/* Setup expected boards */
		final char[] nextGen1 = new char[life.board.length];
		Arrays.fill(nextGen1, DEAD);
		nextGen1[135] = ALIVE;
		nextGen1[201] = ALIVE;
		nextGen1[267] = ALIVE;
		final char[] nextGen2 = Arrays.copyOf(life.board, life.board.length);
		
		/* Run method under test and test 1st generation */
		life.age();
		assertArrayEquals(msg, nextGen1, life.board);

		/* Run method under test and test 2nd generation */
		life.age();
		assertArrayEquals(msg, nextGen2, life.board);

		/* Run method under test and test 3rd generation */
		life.age();
		assertArrayEquals(msg, nextGen1, life.board);

		/* Run method under test and test 4th generation */
		life.age();
		assertArrayEquals(msg, nextGen2, life.board);
	}

}
