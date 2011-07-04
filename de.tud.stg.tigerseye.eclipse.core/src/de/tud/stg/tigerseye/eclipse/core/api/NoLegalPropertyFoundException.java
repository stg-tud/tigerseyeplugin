package de.tud.stg.tigerseye.eclipse.core.api;

import javax.annotation.Nonnull;

/**
 * Indicates that the property store does not contain the attribute searched
 * for.
 * 
 * @author Leo Roos
 * 
 */
@Nonnull
public class NoLegalPropertyFoundException extends Exception {

    /**
     * to suppress warnings
     */
    private static final long serialVersionUID = 1L;
    private static final DSLKey<?> NullKey = DSLKey.NULL_KEY;
    private transient DSLKey<?> key;

    public NoLegalPropertyFoundException() {
	super();
    }

    public NoLegalPropertyFoundException(String message) {
	super(message);
    }

    public NoLegalPropertyFoundException setKey(DSLKey<?> key) {
	this.key = key;
	return this;
    }

    public DSLKey<?> getKey() {
	if (key == null) {
	    key = NullKey;
	}
	return key;
    }

    @Override
    public String toString() {
	return this.getClass().getSimpleName() + ":Key:" + getKey().toString()
		+ (getMessage() == null ? "" : ":Msg:" + getMessage());
    }

}
