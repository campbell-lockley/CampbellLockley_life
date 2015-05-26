/*
 * Name:		Life.java
 * Description:	Interface for game of life implementations.
 * Author:		Campbell Lockley		StudentID: 1178618
 * DateL		26/05/15
 */
package org.campbelll.life;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for Conway's Game of Life implementations in 
 * {@link org.campbelll.life org.campbelll.life}.
 * <p>
 * Rules for Conway's Game of life can be found 
 * <a href="http://en.wikipedia.org/wiki/Conway%27s_Game_of_Life">here</a>.
 * @author Campbell Lockley
 */
public interface Life {
	/** Char representation of a "living" cell. */
	public static final char ALIVE = '#';
	/** Char representation of a "dead" cell. */
	public static final char DEAD = ' ';
	
	/**
	 * Calculates the next generation in the game of life.
	 */
	public void age();
	
	/**
	 * Loads a starting pattern for the game of life from an input stream.
	 * <p>
	 * First line of input stream must have: "[x] [y]"
	 * <ul>
	 * 	<li>x - x position in board where pattern starts</li>
	 *  <li>y - y position in board where pattern starts</li>
	 * </ul>
	 * <p>
	 * Following that is any number of lines representing the pattern. Space 
	 * denotes "dead" cells, and any character which is not a space denotes a 
	 * "live" cell.
	 * 
	 * @param in Input stream to load starting state from.
	 */
	public void loadPattern(InputStream in) 
		throws IOException, FileFormatException;
	
	/**
	 * Prints part of the current state of the board to stdout.
	 * <p>
	 * printBoard(40, 40) will print the top left 40x40 section of the board.
	 * 
	 * @param x Width to print.
	 * @param y Height to print.
	 */
	public void printBoard(int width, int height) 
		throws IllegalArgumentException;
}
