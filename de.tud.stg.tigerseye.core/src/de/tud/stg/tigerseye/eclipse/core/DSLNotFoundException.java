package de.tud.stg.tigerseye.eclipse.core;

public class DSLNotFoundException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String extension;

    public DSLNotFoundException(String message, Throwable cause) {
	super(message, cause);
    }

    public DSLNotFoundException(String message) {
	super(message);
    }

    public DSLNotFoundException() {
	super();
    }

    public DSLNotFoundException setDSL(String extension) {
	this.extension = extension;
	return this;
    }

    public String getDSLExtension() {
	if (extension == null) {
	    return "_no_dsl_extension_information_provided_";
	}
	return extension;
    }

    public String noDSLMsg() {
	return "DSL with extension '" + getDSLExtension() + "' not found.";
    }

}
