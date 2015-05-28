package org.campbelll.life;

import java.io.IOException;
import java.io.InputStream;

//import marku_life.SequentialLife;

/**
 * Example main program for running the sequential Game-of-Life.
 *
 * @author Mark.Utting
 *
 */
public class Main {

	final static int GENERATIONS = 4080;
	final static int BOARD_SIZE = 1024;

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileFormatException 
	 */
	public static void main(String[] args) 
			throws IOException, FileFormatException {
		SequentialLife life = new SequentialLife(BOARD_SIZE);
		InputStream input = Main.class.getResourceAsStream("/gosperGliderGun.patt");
		life.loadPattern(input);
		
		System.out.print("Warming up ...");
		life.warmup(1000);
		System.out.println(" Done");
		
		System.out.println("Starting " + GENERATIONS + " generations of "
				+ BOARD_SIZE + "x" + BOARD_SIZE);
		final long startTime = System.nanoTime();
		for (int gen = 0; gen < GENERATIONS; gen++) {
			life.age();
		}
		final long endTime = System.nanoTime();
		System.out.println("Time taken was " + (endTime - startTime) / 1.0e9 + " secs");
//		life.printBoard(40, 40);
	}
}