package de.tud.stg.popart.dslsupport.sql;

/**
 * This exception indicates that the SQL DSL was used in an unsupported way
 */
public class SQLDSLException extends Exception {
	private static final long serialVersionUID = 1L;

	public SQLDSLException() {}

	public SQLDSLException(String message) {
		super(message);
	}

	public SQLDSLException(Throwable cause) {
		super(cause);
	}

	public SQLDSLException(String message, Throwable cause) {
		super(message, cause);
	}

}
