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
	private static final int NO_WRAP = 0;	// Indexes to neighbour arrays 
	private static final int WRAP_LEFT = 1;
	private static final int WRAP_RIGHT = 2;
	private static final int WRAP_UP = 3;
	private static final int WRAP_DOWN = 4;
	private static final int WRAP_UP_LEFT = 5;
	private static final int WRAP_UP_RIGHT = 6;
	private static final int WRAP_DOWN_LEFT = 7;
	private static final int WRAP_DOWN_RIGHT = 8;
	
	/* Private Globals */
	private int boardDim;		// Dimension size of the board
//	private boolean[] board;	// Representation of the Game of Life board
//	private boolean[] nextGen;	// Next generation of the Game of Life
	private char[] board, nextGen;
	private int[][] neighbours;	// Indexes of neighbours (incl. self) in board
	
	/**
	 * Constructor.
	 * 
	 * @param boardDim Size of board dimension.
	 */
	public SequentialLife(int boardDim) {
		this.boardDim = boardDim;
//		this.board = new boolean[boardDim * boardDim];
//		this.nextGen = new boolean[boardDim * boardDim];
		this.board = new char[boardDim * boardDim];
		this.nextGen = new char[boardDim * boardDim];
		this.neighbours = new int[NEIGH_NUM][NEIGH_NUM];
		
		/* Calculate neighbour index arrays */
		int index = 0;
		for (int y = -1; y <= 1; y++) {
			for (int x = -1; x <= 1; x++) {
				neighbours[NO_WRAP][index++] = y * boardDim + x;
			}
		}
		neighbours[WRAP_LEFT] = Arrays.copyOf(neighbours[NO_WRAP], NEIGH_NUM);
		for (int i = 0; i < 9; i += 3) neighbours[WRAP_LEFT][i] += boardDim;
		neighbours[WRAP_RIGHT] = Arrays.copyOf(neighbours[NO_WRAP], NEIGH_NUM);
		for (int i = 2; i < 9; i += 3) neighbours[WRAP_RIGHT][i] -= boardDim;
		int boardSqr = boardDim * boardDim;
		neighbours[WRAP_UP] = Arrays.copyOf(neighbours[NO_WRAP], NEIGH_NUM);
		for (int i = 0; i < 3; i++) neighbours[WRAP_UP][i] += boardSqr;
		neighbours[WRAP_DOWN] = Arrays.copyOf(neighbours[NO_WRAP], NEIGH_NUM);
		for (int i = 6; i < 9; i++) neighbours[WRAP_DOWN][i] -= boardSqr;
		neighbours[WRAP_UP_LEFT] = 
				Arrays.copyOf(neighbours[WRAP_LEFT], NEIGH_NUM);
		for (int i = 0; i < 3; i++) {
			neighbours[WRAP_UP_LEFT][i] += neighbours[WRAP_UP][i];
		}
		neighbours[WRAP_UP_RIGHT] = 
				Arrays.copyOf(neighbours[WRAP_RIGHT], NEIGH_NUM);
		for (int i = 0; i < 3; i++) {
			neighbours[WRAP_UP_RIGHT][i] += neighbours[WRAP_UP][i];
		}
		neighbours[WRAP_DOWN_LEFT] = 
				Arrays.copyOf(neighbours[WRAP_LEFT], NEIGH_NUM);
		for (int i = 6; i < 9; i++) {
			neighbours[WRAP_DOWN_LEFT][i] += neighbours[WRAP_DOWN][i];
		}
		neighbours[WRAP_DOWN_RIGHT] = 
				Arrays.copyOf(neighbours[WRAP_RIGHT], NEIGH_NUM);
		for (int i = 6; i < 9; i++) {
			neighbours[WRAP_DOWN_RIGHT][i] += neighbours[WRAP_DOWN][i];
		}
		
	}
	
	/**
	 * Runs the {@link SequentialLife#age() age()} method the specified number of 
	 * times to give the JIT compiler opportunity to run.
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
		/* Iterate over board */
		int index;
		
		/* Do Top Left cell */
		nextGen[0] = live(0, neighbours[WRAP_UP_LEFT]);
		
		/* Do Top cells */
		for (int x = 1; x < (boardDim - 1); x++) {
			nextGen[x] = live(x, neighbours[WRAP_UP]);
		}
		
		/* Do Top Right cell */
		index = boardDim - 1;
		nextGen[index] = live(index, neighbours[WRAP_UP_RIGHT]);
		
		/* Do middle cells */
		for (int y = 1; y < (boardDim - 1); y++) {
			/* Do left most cell */
			index = y * boardDim;
			nextGen[index] = live(index, neighbours[WRAP_LEFT]);
			
			/* Do middle cells */
			for (int x = 1; x < (boardDim - 1); x++) {
				index = y * boardDim + x;
				nextGen[index] = live(index, neighbours[NO_WRAP]);
			}
			
			/* Do right most cell */
			index = (y + 1) * boardDim - 1;
			nextGen[index] = live(index, neighbours[WRAP_RIGHT]);
		}
		
		/* Do Bottom Left cell */
		index = (boardDim - 1) * boardDim;
		nextGen[index] = live(index, neighbours[WRAP_DOWN_LEFT]);
		
		/* Do bottom cells */
		for (int x = (boardDim - 1) * boardDim + 1; 
				x < ((boardDim * boardDim) - 2); x++) {
			nextGen[x] = live(x, neighbours[WRAP_DOWN]);
		}

		/* Do Bottom Right cell */
		index = (boardDim * boardDim) - 1;
		nextGen[index] = live(index, neighbours[WRAP_DOWN_RIGHT]);
		
		/* Swap boards over */
		char[] tmp = board;
		board = nextGen;
		nextGen = tmp;
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
					board[y * boardDim + x] = ALIVE;
				}
				x++;
			}
		} catch (IndexOutOfBoundsException e) {
			Arrays.fill(board, DEAD);	// Clear board values
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
//				System.out.print((board[y * boardDim + x] ? ALIVE : DEAD));
				System.out.print(board[y * boardDim + x]);
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
