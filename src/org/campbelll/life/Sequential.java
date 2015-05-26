/*
 * Name:		Sequential.java
 * Description:	Sequential implementation of game of life.
 * Author:		Campbell Lockley		StudentID: 1178618
 * Date:		26/05/15
 */
package org.campbelll.life;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Sequential implementation of 
 * {@link org.campbelll.life.Life org.campbelll.life.Life}.
 * 
 * @author Campbell Lockley
 */
public class Sequential implements Life {
	
	private int boardDim;		// Dimension size of the board
	private boolean[] board;	// Representation of the Game of Life board
	
	/**
	 * Constructor.
	 * 
	 * @param boardDim Size of board dimension.
	 */
	public Sequential(int boardDim) {
		this.boardDim = boardDim;
		this.board = new boolean[boardDim * boardDim];
	}

	@Override
	public void age() {
		return;
	}

	@Override
	public void loadPattern(InputStream in) 
			throws IOException, FileFormatException {
		/* Clear current board values */
		Arrays.fill(board, false);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		/* Get x-y start pos from 1st line */
		String[] tokens = br.readLine().split(" ");
		int xStart = Integer.parseInt(tokens[0]);
		int yStart = Integer.parseInt(tokens[1]);
		
		final int space = new Character(' ').charValue();
		final int newline = new Character('\n').charValue();
		final int creturn = new Character('\r').charValue();
		int c;
		
		/* Populate board from input stream */
		int x = xStart, y = yStart;
		try {
			while ((c = br.read()) != -1) {
				if (c == newline) {			// Next input line
					y++;
					x = xStart;
					continue;
				} else if (c == creturn) {	// Ignore carriage returns (\r)
					continue;
				} else if (c != space) {	// This is a "living" cell
					board[y * boardDim + x] = true;
				}
				x++;
			}
		} catch (IndexOutOfBoundsException e) {
			Arrays.fill(board, false);	// Clear current board values
			throw new FileFormatException(
					"Input pattern is larger than board size");
		} finally {
			br.close();
		}
	}

	@Override
	public void printBoard(int width, int height) 
			throws IllegalArgumentException {
		/* Bounds check the parameters */
		if ((width > boardDim) || (height > boardDim)) {
			throw new IllegalArgumentException(
					"Width and heigth cannot be > board dimension size");
		}
		
		/* Print board to stdout */
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				System.out.print((board[y * boardDim + x] ? ALIVE : DEAD));
			}
			System.out.println();
		}
		System.out.flush();;
	}
	
	/**
	 * Returns the dimension size of the Game of Life board.
	 * 
	 * @return Size of board dimension.
	 */
	public int getDimSize() {
		return this.boardDim;
	}

}
