/*
 * Name:		Life.java
 * Description:	Interface for game of life implementations.
 * Author:		Campbell Lockley		StudentID: 1178618
 * Date:		26/05/15
 */
package org.campbelll.life;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

/**
 * Abstract class representing Conway's Game of Life implementations in 
 * {@link org.campbelll.life}.
 * <p>
 * Rules for Conway's Game of life can be found 
 * <a href="http://en.wikipedia.org/wiki/Conway%27s_Game_of_Life">here</a>.
 * 
 * @author Campbell Lockley
 */
public abstract class Life {
	/* Number of neighbours (incl. self) */
	protected static final int NEIGH_NUM = 9;
	/** Char representation of a "living" cell. */
	public static final char ALIVE = '#';
	/** Char representation of a "dead" cell. */
	public static final char DEAD = ' ';
	
	/* Java handles chars much faster than booleans and ints */
	protected char[] board;				// Game of Life board
	protected char[] nextGen;			// Next generation of the Game of Life
	protected int boardDim;				// Dimension size of the board
	protected int[] neighbours;			// Indexes of neighbours (incl. self)
	
	/** Default constructor. */
	protected Life() {
	}
	
	/**
	 * Constructor.
	 * <p>
	 * The game of life board must be a size which is a power of 2.
	 * 
	 * @param boardDim Size of board dimension.
	 */
	public Life(int boardDim) {
		/* Size of board must be a power of 2 */
		assert (boardDim % 2 == 0);
		
		this.boardDim = boardDim;
		/* Edge of board is copied to opposite side of board for wrapping */
		this.board = new char[(boardDim + 2) * (boardDim + 2)];
		this.nextGen = new char[(boardDim + 2) * (boardDim + 2)];
		Arrays.fill(board, DEAD);
		Arrays.fill(nextGen, DEAD);
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
	 * Calculates the next generation in the Game of Life.
	 * 
	 * @throws TimeoutException if a blocking method call in age() times out, 
	 * causing age() to fail.
	 */
	public abstract void age() throws TimeoutException;
	
	/**
	 * Cleans up after itself.
	 */
	public abstract void cleanUp();
	
	/**
	 * Runs the {@link SequentialLife#age() age()} method the specified number 
	 * of times to give the JIT compiler opportunity to do some optimisation.
	 * 
	 * @param times Number of warmup iterations. 100 times works well.
	 * @throws TimeoutException if age() times out.
	 */
	public void warmup(int times) throws TimeoutException {
		char[] tmp = Arrays.copyOf(board, board.length);
		
		for (int i = 0; i < times; i++) age();
		
		board = tmp;
	}
	
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
	 * Multiple patterns can be loaded onto the same board using this method.
	 * 
	 * @param in Input stream to load starting state from.
	 * @throws IOException if there is an I/O error.
	 * @throws FileFormatException if format of pattern in the InputStream is
	 * incorrect.
	 */
	public void loadPattern(InputStream in) 
			throws IOException, FileFormatException {
		/* Backup old board */
		char[] tmp = board;
		board = nextGen;
		nextGen = tmp;
		
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
			/* On error restore old board */
			tmp = board;
			board = nextGen;
			nextGen = tmp;
			throw new FileFormatException(
					"Input pattern is larger than board size");
		} finally {
			br.close();
		}
	}
	
	/**
	 * Prints part of the current state of the board to stdout.
	 * <p>
	 * printBoard(40, 40) will print the top left 40x40 section of the board.
	 * 
	 * @param width Width to print.
	 * @param height Height to print.
	 */
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
				System.out.print(
						(board[y * (boardDim + 2) + x] == ALIVE) ? "#" : " ");
			}
			System.out.println();
		}
		System.out.flush();;
	}

	/**
	 * Clears the Game of Life board.
	 * <p>
	 * Call before {@link org.campbelll.life.Life#loadPattern loadPattern()} to
	 * start the Game of Life with a board containing just the loaded pattern.
	 */
	public void clearBoard() {
		Arrays.fill(board, DEAD);
		Arrays.fill(nextGen, DEAD);
	}

	/**
	 * Calculates whether this cell will live or die in the next generation. 
	 * Rules are:
	 * <ul>
	 * 	<li>A live cell with < 2 living neighbours dies</li>
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
	protected char live(int index) {
		int sum = 0;
		
		/* It its just as fast to use a loop as to unroll it */
		/* It is faster to test-branch-increment than to += the chars */
		
		/* Sum living neighbours, including itself */
		for (int i = 0; i < NEIGH_NUM; i++) {
			if (board[index + neighbours[i]] == ALIVE) sum++;
		}
		
		/* Apply shortened rules */
		if (sum == 3) return ALIVE;
		else if (sum == 4) return board[index];
		else return DEAD;
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
	protected char[] copyEdges(char[] board) {
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
	
}
