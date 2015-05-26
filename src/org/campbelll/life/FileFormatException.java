package org.campbelll.life;

/**
 * Thrown when a file is incorrectly formatted.
 * 
 * @author Campbell Lockley
 */
public class FileFormatException extends Exception {
	/** serialVersionUID for serialising */
	private static final long serialVersionUID = 6269430004046301731L;

	public FileFormatException(String message) {
		super(message);
	}
	
	public FileFormatException() {
		super();
	}
}
