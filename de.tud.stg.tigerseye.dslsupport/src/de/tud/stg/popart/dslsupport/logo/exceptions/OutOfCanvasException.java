package de.tud.stg.popart.dslsupport.logo.exceptions;

/**
 * This exception is thrown if a turtle paints out side the canvas.
 * @author dinkelaker
 */
public class OutOfCanvasException extends RuntimeException {

	public OutOfCanvasException() {
		super();
	}

	public OutOfCanvasException(String arg0) {
		super(arg0);
	}

	public OutOfCanvasException(Throwable arg0) {
		super(arg0);
	}

	public OutOfCanvasException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
