package de.tud.stg.tigerseye.eclipse.core.api;

/**
 * Can be used in situations that are not recoverable.
 * 
 * @author Leo Roos
 * 
 */
public class TigerseyeRuntimeException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public TigerseyeRuntimeException() {
    }

    public TigerseyeRuntimeException(String msg) {
	super(msg);
    }

    public TigerseyeRuntimeException(Throwable t) {
	super(t);
    }

    public TigerseyeRuntimeException(String msg, Throwable t) {
	super(msg, t);
    }

}
