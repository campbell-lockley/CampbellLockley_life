/*
 * Name:		Sequential.java
 * Description:	Sequential implementation of game of life.
 * Author:		Campbell Lockley		StudentID: 1178618
 * Date:		26/05/15
 */
package org.campbelll.life;

/**
 * Sequential implementation of 
 * {@link org.campbelll.life.Life Life}.
 * 
 * @author Campbell Lockley
 */
public class SequentialLife extends Life {
	/**
	 * Constructor.
	 * 
	 * @param boardDim Size of board dimension.
	 */
	public SequentialLife(int boardDim) {
		super(boardDim);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation of age() uses a single thread which iterates over 
	 * every cell in the board.
	 */
	@Override
	public void age() {
		/* Do every cell */
		int index;
		for (int y = 1; y < (boardDim + 1); y++) {
			for (int x = 1; x < (boardDim + 1); x++) {
				index = y * (boardDim + 2) + x;
				nextGen[index] = live(index);
			}
		}
		
		/* Copy edges to handle wrapping */
		copyEdges(nextGen);
		
		/* Swap boards over */
		char[] tmp = board;
		board = nextGen;
		nextGen = tmp;
	}
}
