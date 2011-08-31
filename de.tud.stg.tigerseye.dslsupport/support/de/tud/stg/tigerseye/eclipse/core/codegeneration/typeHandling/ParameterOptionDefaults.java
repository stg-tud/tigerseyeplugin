package de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling;

public interface ParameterOptionDefaults {

	public static final String DEFAULT_ARRAY_DELIMITER = ",";
	public static final String DEFAULT_PARAMETER_ESCAPE = "p";
	public static final String DEFAULT_WHITESPACE_ESCAPE = "_";
	/*
	 * FIXME(Leo_Roos;Aug 31, 2011) This implementation does not support escape
	 * characters quick solution could be "\".*?(?<!\\)\")" negative
	 * lookbehind for backslash character before closing quotation mark.
	 */
	public static final String DEFAULT_STRING_QUOTATION = "(\".*?\")";

}
