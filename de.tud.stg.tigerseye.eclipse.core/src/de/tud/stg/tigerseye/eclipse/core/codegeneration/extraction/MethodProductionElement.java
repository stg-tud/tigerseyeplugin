package de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction;

import javax.annotation.Nullable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Represents a parsed part of production.
 * 
 * @author Leo_Roos
 * 
 */
public abstract class MethodProductionElement implements MethodProductionConstants {

    protected MethodProductionElement() {
	//
    }

    @Override
    public String toString() {
	return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("capturedString",
		doGetCapturedString()).toString();
    }

    public abstract ProductionElement getProductionElementType();

    /**
     * @return a captured string.
     * @throws IllegalStateException
     *             if called although no string has yet been set.
     */
    public final String getCapturedString() {
	String doGetCapturedString = doGetCapturedString();
	if (doGetCapturedString == null)
	    throw new IllegalStateException("captured string has not yet been set");
	else
	    return doGetCapturedString;
    }

    protected boolean isNotNull(Object... shouldNotBeNull) {
	for (Object object : shouldNotBeNull) {
	    if (object == null)
		return false;
	}
	return true;
    }

    protected void validateInitialized() {
	if (!isInitialized())
	    throw new IllegalStateException("This object has not been initialized");
    }

    protected abstract @Nullable
    String doGetCapturedString();

    /**
     * Initialized means that every mandatory information has been passed to the
     * object. The exact meaning is defined by implementing classes.
     * 
     * @return <code>true</code> if this element has been initialized,
     *         <code>false</code> otherwise
     */
    protected abstract boolean isInitialized();

}
