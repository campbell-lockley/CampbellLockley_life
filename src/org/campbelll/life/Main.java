package org.campbelll.life;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeoutException;

/**
 * Main program for running the Game-of-Life jar.
 * <p>
 * Copied from life.Main by Mark Utting
 *
 * @author Campbell Lockley
 */
public class Main {
	/* Run parameters */
	final static int GENERATIONS = 4080;
//	final static int GENERATIONS = 40800;
	final static int BOARD_SIZE = 1024;
//	final static int BOARD_SIZE = 4096;

	/**
	 * Entry point when running the jar.
	 * 
	 * @param args Command line arguments.
	 * @throws IOException if there is an I/O error.
	 * @throws FileFormatException if the pattern file is formated incorrectly.
	 */
	public static void main(String[] args) 
			throws IOException, FileFormatException {
		final int numThreads = Runtime.getRuntime().availableProcessors();
//		marku_life.SequentialLife life = new marku_life.SequentialLife(BOARD_SIZE);
//		Life life = new SequentialLife(BOARD_SIZE);
//		Life life = new LineParallelLife(BOARD_SIZE, numThreads);
		Life life = new BlockParallelLife(BOARD_SIZE, numThreads);
		
		InputStream input = Main.class
				.getResourceAsStream("/gosperGliderGun.patt");
		life.loadPattern(input);
		
		System.out.print("Warming up ...");
		try {
			life.warmup(100);
		} catch (TimeoutException e) {
			System.err.println("warmup() timed out");
			e.printStackTrace();
		}
		System.out.println(" Done");
		
		System.out.println("Starting " + GENERATIONS + " generations of "
				+ BOARD_SIZE + "x" + BOARD_SIZE);
		final long startTime = System.nanoTime();
		for (int gen = 0; gen < GENERATIONS; gen++) {
			try {
				life.age();
			} catch (TimeoutException e) {
				System.err.println("age() timed out");
				e.printStackTrace();
			}
		}
		final long endTime = System.nanoTime();
		System.out.println("Time taken was " + (endTime - startTime) / 1.0e9 + 
				" secs");
		life.printBoard(40, 40);
		life.cleanUp();
	}
	
}