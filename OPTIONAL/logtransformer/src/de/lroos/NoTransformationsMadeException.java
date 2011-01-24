package de.lroos;

public class NoTransformationsMadeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoTransformationsMadeException() {
		super();
	}

	public NoTransformationsMadeException(String message) {
		super(message);
	}

	public NoTransformationsMadeException(String message, Exception cause) {
		super(message, cause);
	}

	public NoTransformationsMadeException(Exception cause) {
		super(cause);
	}

}
