package de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling;

import de.tud.stg.popart.builder.core.annotations.DSL;

/**
 * Valid parameter options and corresponding default values. These options
 * identify the four parameter options in the {@link DSL} annotation.
 * 
 * @author Leo Roos
 * 
 */
public enum ParameterOptions {

	STRING_QUOTATION(ParameterOptionDefaults.DEFAULT_STRING_QUOTATION), //
	ARRAY_DELIMITER(ParameterOptionDefaults.DEFAULT_ARRAY_DELIMITER), //
	PARAMETER_ESCAPE(ParameterOptionDefaults.DEFAULT_PARAMETER_ESCAPE), //
	WHITESPACE_ESCAPE(ParameterOptionDefaults.DEFAULT_WHITESPACE_ESCAPE), //

	;
	public final String defaultValue;

	private ParameterOptions(String value) {
		this.defaultValue = value;
	}

}
