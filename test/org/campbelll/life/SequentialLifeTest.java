/* ************************************************************************* *
 * Name:		SequentialLifeTest.java
 * Description:	Test cases for SequentialLife.java
 * Author:		Campbell Lockley		StudentID: 1178618
 * Date:		03/06/15
 * ************************************************************************* */
package org.campbelll.life;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import static org.campbelll.life.Life.ALIVE;
import static org.campbelll.life.Life.DEAD;;

/**
 * Test cases for {@link SequentialLife}.
 * 
 * @author Campbell Lockley
 */
public class SequentialLifeTest {

	/**
	 * Tests {@link SequentialLife#loadPattern(InputStream)}.
	 * <p>
	 * {@link SequentialLife#loadPattern(InputStream) loadPattern} is tested 
	 * using the small pattern file squareTest.patt.
	 * 
	 * @throws FileFormatException if pattern file is incorrectly formatted.
	 * @throws IOException if there is an I/O error.
	 */
	@Test
	public void testLoadPattern() throws IOException, FileFormatException {
		final String msg = "loadPattern() did not load correctly";
		final char[] board = 
			{
				DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,
				DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,
				DEAD,	DEAD,	ALIVE,	ALIVE,	ALIVE,	DEAD,	DEAD,
				DEAD,	DEAD,	ALIVE,	DEAD,	ALIVE,	DEAD,	DEAD,
				DEAD,	DEAD,	ALIVE,	ALIVE,	ALIVE,	DEAD,	DEAD,
				DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,
				DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,
			};
		
		/* Instantiate class under test */
		final int boardSize = 5;
		SequentialLife life = new SequentialLife(boardSize);
		
		/*
		 * Load test pattern - squareTest.patt:
		 * 	1:1 1
		 * 	2:###
		 * 	3:# #
		 * 	4:###
		 */
		InputStream in = SequentialLifeTest.class
				.getResourceAsStream("/squareTest.patt");
		
		/* Run method under test */
		life.loadPattern(in);
		
		/* Test state of board including wrapped edges */
		for (int i = 0; i < 49; i++) {
			assertArrayEquals(msg, board, life.board);
		}
	}

	/**
	 * Tests {@link SequentialLife#age()}.
	 * <p>
	 * {@link SequentialLife#age() age} is tested using the small pattern file 
	 * blinker.patt which is a period 2 oscillator.
	 * 
	 * @throws FileFormatException if pattern file is incorrectly formatted.
	 * @throws IOException if there is an I/O error.
	 */
	@Test
	public void testAge() throws IOException, FileFormatException {
		final String msg = "age() didn't compute next generation correctly";
		final char[] nextGen1 = 
			{
				DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,
				DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,
				DEAD,	DEAD,	DEAD,	ALIVE,	DEAD,	DEAD,	DEAD,
				DEAD,	DEAD,	DEAD,	ALIVE,	DEAD,	DEAD,	DEAD,
				DEAD,	DEAD,	DEAD,	ALIVE,	DEAD,	DEAD,	DEAD,
				DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,
				DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	
			};
		final char[] nextGen2 = 
			{
				DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,
				DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,
				DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,
				DEAD,	DEAD,	ALIVE,	ALIVE,	ALIVE,	DEAD,	DEAD,
				DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,
				DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,
				DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	DEAD,
			};
		
		/* Instantiate class under test */
		int boardSize = 5;
		SequentialLife life = new SequentialLife(boardSize);
		
		/*
		 * Load test pattern - blinker.patt:
		 * 	1:1 2
		 * 	2:###
		 */
		InputStream in = SequentialLifeTest.class
				.getResourceAsStream("/blinker.patt");
		life.loadPattern(in);
		
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

	/**
	 * Tests the copy-edge wrapping of {@link SequentialLife}.
	 * <p>
	 * Wrapping is tested with the small pattern file toadWrap.patt which 
	 * behaves as a period 2 oscillator on a 5x5 board.
	 * 
	 * @throws FileFormatException if pattern file is incorrectly formatted.
	 * @throws IOException if there is an I/O error.
	 */
	@Test
	public void testWrap() throws IOException, FileFormatException {
		final String msg = "board is not wrapping correctly";
		final char[] nextGen = 
			{
				0,		0,		0,		0,		0,		0,		0,
				0,		ALIVE,	DEAD,	DEAD,	ALIVE,	DEAD,	0,
				0,		DEAD,	ALIVE,	DEAD,	DEAD,	DEAD,	0,
				0,		DEAD,	DEAD,	DEAD,	DEAD,	DEAD,	0,
				0,		DEAD,	DEAD,	ALIVE,	DEAD,	DEAD,	0,
				0,		ALIVE,	DEAD,	DEAD,	ALIVE,	DEAD,	0,
				0,		0,		0,		0,		0,		0,		0
			};
		
		/* Instantiate class under test */
		final int boardDim = 5;
		SequentialLife life = new SequentialLife(boardDim);
		
		/*
		 * Load test pattern - toadWrap.patt:
		 * 	1:0 0
		 * 	2:###
		 * 	3:
		 * 	4:
		 * 	5:
		 * 	6: ###
		 */
		InputStream in = SequentialLifeTest.class
				.getResourceAsStream("/toadWrap.patt");
		life.loadPattern(in);
		
		/* Test that edges were copied */
		for (int i = 0; i < 7; i++) {		// Top edge
			assertTrue(msg, life.board[i] == life.board[35 + i]);
		}
		for (int i = 42; i < 49; i++) {		// Bottom edge
			assertTrue(msg, life.board[i] == life.board[i - 35]);
		}
		for (int i = 0; i <= 42; i += 7) {	// Left edge
			assertTrue(msg, life.board[i] == life.board[i + 5]);
		}
		for (int i = 6; i <= 48; i += 7) {	// Right edge
			assertTrue(msg, life.board[i] == life.board[i - 5]);
		}
		
		/* Generate next generation */
		life.age();

		/* Test contents of board only */
		for (int i = 8; i < 13; i++) {					// 1st line
			assertTrue(msg, life.board[i] == nextGen[i]);
		}
		for (int i = 15; i < 20; i++) {					// 2nd line
			assertTrue(msg, life.board[i] == nextGen[i]);
		}
		for (int i = 22; i < 27; i++) {					// 3rd line
			assertTrue(msg, life.board[i] == nextGen[i]);
		}
		for (int i = 29; i < 34; i++) {					// 4th line
			assertTrue(msg, life.board[i] == nextGen[i]);
		}
		for (int i = 36; i < 41; i++) {					// 5th line
			assertTrue(msg, life.board[i] == nextGen[i]);
		}
	}
	
}
