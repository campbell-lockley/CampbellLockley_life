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
//	final static int GENERATIONS = 4080;	// My parameters
//	final static int BOARD_SIZE = 1024;
//	final static int NUM_THREADS = 4;
//	final static int CACHE_SIZE = 32;
	final static int GENERATIONS = 20;		// Amazon parameters
	final static int BOARD_SIZE = 32768;
	final static int NUM_THREADS = 32;
	final static int CACHE_SIZE = 32;

	/**
	 * Entry point when running the jar.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args)  {
		Life life;

		/* Run all LineParallelLife runs */
		System.out.println("LineParallelLife: Starting " + GENERATIONS + 
				" generations of " + BOARD_SIZE + "x" + BOARD_SIZE);
		for (int i = 1; i <= NUM_THREADS; i++) {
			System.out.print(i + " Thread: ");
			life = new LineParallelLife(BOARD_SIZE, i);
			try {
				runTest(life);
			} catch (IOException | FileFormatException e) {
				System.err.println("LineParallelLife run "+i+" failed.");
			}
		}
		
		/* Run all BlockParallelLife runs */
		System.out.println("BlockParallelLife: Starting " + GENERATIONS + 
				" generations of " + BOARD_SIZE + "x" + BOARD_SIZE);
		for (int i = 1; i <= NUM_THREADS; i++) {
			System.out.print(i + " Thread: ");
			life = new BlockParallelLife(BOARD_SIZE, i);
			try {
				runTest(life);
			} catch (IOException | FileFormatException e) {
				System.err.println("BlockParallelLife run "+i+" failed.");
			}
		}

		/* Run SequentialLife run */
		System.out.println("SequentialLife: Starting " + GENERATIONS + 
				" generations of " + BOARD_SIZE + "x" + BOARD_SIZE);
		life = new SequentialLife(BOARD_SIZE);
		try {
			runTest(life);
		} catch (IOException | FileFormatException e) {
			System.err.println("SequentialLife run failed.");
		}
		
		/* Run all CacheParallelLife runs */
		System.out.println("CacheParallelLife: Starting " + GENERATIONS + 
				" generations of " + BOARD_SIZE + "x" + BOARD_SIZE);
		for (int i = 1; i <= NUM_THREADS; i++) {
			System.out.print(i + " Thread: ");
			life = new CacheParallelLife(BOARD_SIZE, i, CACHE_SIZE);
			try {
				runTest(life);
			} catch (IOException | FileFormatException e) {
				System.err.println("CacheParallelLife run "+i+" failed.");
			}
		}
	}
	
	/**
	 * Sets up and runs a given implementation of {@link Life}.
	 * 
	 * @param life The Game of Life implementation to run.
	 * @throws IOException if there is an I/O error.
	 * @throws FileFormatException if the pattern file is formated incorrectly.
	 */
	public static void runTest(Life life) 
			throws IOException, FileFormatException {
		InputStream input = Main.class
				.getResourceAsStream("/gosperGliderGun.patt");
		life.loadPattern(input);
		
		try {
			life.warmup(100);
		} catch (TimeoutException e) {
			System.err.println("warmup() timed out");
			e.printStackTrace();
		}
		
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
		life.cleanUp();
	}
	
}
