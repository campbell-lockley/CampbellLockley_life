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
public class SequentialLife implements Life {
	/* Constants */
	private static final int NEIGH_NUM = 9;	// Number of neighbours (incl. self)
	
	/* Private Globals */
	private int boardDim;		// Dimension size of the board
	/* Java handles chars much faster than booleans and ints */
	private char[] board;		// Representation of the Game of Life board
	private char[] nextGen;		// Next generation of the Game of Life
	private int[] neighbours;	// Indexes of neighbours (incl. self) in board
	
	/**
	 * Constructor.
	 * 
	 * @param boardDim Size of board dimension.
	 */
	public SequentialLife(int boardDim) {
		this.boardDim = boardDim;
		this.board = new char[(boardDim + 2) * (boardDim + 2)];
		this.nextGen = new char[(boardDim + 2) * (boardDim + 2)];
		this.neighbours = new int[NEIGH_NUM];
		
		/* Calculate neighbour index arrays */
		int index = 0;
		for (int y = -1; y <= 1; y++) {
			for (int x = -1; x <= 1; x++) {
				neighbours[index++] = y * (boardDim + 2) + x;
			}
		}
		
	}
	
	/**
	 * Runs the {@link SequentialLife#age() age()} method the specified number 
	 * of times to give the JIT compiler opportunity to run.
	 * 
	 * @param times Number of warmup iterations.
	 */
	public void warmup(int times) {
		char[] tmp = Arrays.copyOf(board, board.length);
		
		for (int i = 0; i < times; i++) age();
		
		board = tmp;
	}

	/**
	 * Calculates whether this cell will live or die in the next generation. 
	 * Rules are:
	 * <ul>
	 * 	<li>A live cell with > 2 living neighbours dies</li>
	 * 	<li>A live cell with 2 or 3 living neighbours lives</li>
	 * 	<li>A live cell with < 3 living neighbours die</li>
	 * 	<li>A dead cell with exactly 3 living neighbours lives</li>
	 * </ul>
	 * These rules can be shortened to:
	 * <ul>
	 * 	<li>If the sum of living neighbours and itself is 3, the cell will 
	 * live</li>
	 * 	<li>If the sum of living neighbours and itself is 4, the cell will 
	 * remain in its previous state</li>
	 * 	<li>Any other sum results in the cell being dead</li>
	 * </ul>
	 * 
	 * @param index Index of this cell in the board.
	 * @param neighbours Array of indices to neighbours in the board.
	 * @return True if this cell will live in the next generation, 
	 * false otherwise.
	 */
	private char live(int index, int[] neighbours) {
		int sum = 0;
		
		/* Sum living neighbours, including itself */
		/* It its just as fast to use a loop as to unroll it */
		for (int i = 0; i < NEIGH_NUM; i++) {
			if (board[index + neighbours[i]] == ALIVE) sum++;
		}
		
		/* Apply shortened rules */
		if (sum == 3) return ALIVE;
		else if (sum == 4) return board[index];
		else return DEAD;
	}
	
	@Override
	public void age() {
		/* Do every cell */
		int index;
		for (int y = 1; y < (boardDim + 1); y++) {
			for (int x = 1; x < (boardDim + 1); x++) {
				index = y * (boardDim + 2) + x;
				nextGen[index] = live(index, neighbours);
			}
		}
		
		/* Copy edges to handle wrapping */
		copyEdges(nextGen);
		
		/* Swap boards over */
		char[] tmp = board;
		board = nextGen;
		nextGen = tmp;
	}
	
	/**
	 * Copies edges of board to facilitate wrapping.
	 * <p>
	 * Top edge (i.e. the 2nd row) is copied to the bottom of the board, bottom 
	 * edge (i.e. the (n-1) row) is copied to the top of the board, likewise 
	 * with the left and right edges. The corners are handled automatically.
	 * 
	 * @param board Board to perform edge copies on.
	 * @return Pointer to edited board.
	 */
	private char[] copyEdges(char[] board) {
		/* Copy last row to top and first row to bottom */
		for (int x = 0; x < (boardDim + 2); x++) {
			board[x] = board[boardDim*(boardDim+2)+x];
			board[(boardDim+1)*(boardDim+2)+x] = board[(boardDim+2)+x];
		}
		/* Copy right most column to left and left most column to right */
		for (int y = 0; y < (boardDim + 2); y++) {
			board[y*(boardDim+2)] = board[y*(boardDim+2)+boardDim];
			board[y*(boardDim+2)+boardDim+1] = board[y*(boardDim+2)+1];
		}
		
		return board;
	}

	@Override
	public void loadPattern(InputStream in) 
			throws IOException, FileFormatException {
		/* Clear current boards */
		Arrays.fill(board, DEAD);
		Arrays.fill(nextGen, DEAD);
		
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
		int x = xStart + 1, y = yStart + 1;
		try {
			while ((c = br.read()) != -1) {
				if (c == newline) {			// Next input line
					y++;
					x = xStart + 1;
					continue;
				} else if (c == creturn) {	// Ignore carriage returns (\r)
					continue;
				} else if (c != space) {	// This is a "living" cell
					board[y * (boardDim + 2) + x] = ALIVE;
				}
				x++;
			}

			/* Copy edges to handle wrapping */
			copyEdges(board);
		} catch (IndexOutOfBoundsException e) {
			Arrays.fill(board, DEAD);		// Clear board values
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
		for (int y = 1; y < (height + 1); y++) {
			for (int x = 1; x < (width + 1); x++) {
				System.out.print(board[y * (boardDim + 2) + x]);
			}
			System.out.println();
		}
		System.out.flush();;
	}
	
//	/**
//	 * Returns the dimension size of the Game of Life board.
//	 * 
//	 * @return Size of board dimension.
//	 */
//	public int getDimSize() {
//		return this.boardDim;
//	}

}
