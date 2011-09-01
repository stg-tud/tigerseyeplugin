package de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling;

import de.tud.stg.popart.builder.core.annotations.DSLClass;
import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.builder.core.annotations.DSLParameter;

/**
 * Valid parameter options and corresponding default values. These options
 * identify the four parameter options in the {@link DSLClass},
 * {@link DSLMethod} and {@link DSLParameter} annotations.
 * 
 * @author Leo Roos
 * 
 */
public enum ConfigurationOptions {

	STRING_QUOTATION(ConfigurationOptionDefaults.DEFAULT_STRING_QUOTATION), //
	ARRAY_DELIMITER(ConfigurationOptionDefaults.DEFAULT_ARRAY_DELIMITER), //
	PARAMETER_ESCAPE(ConfigurationOptionDefaults.DEFAULT_PARAMETER_ESCAPE), //
	WHITESPACE_ESCAPE(ConfigurationOptionDefaults.DEFAULT_WHITESPACE_ESCAPE), //

	;
	public final String defaultValue;

	private ConfigurationOptions(String value) {
		this.defaultValue = value;
	}

}
