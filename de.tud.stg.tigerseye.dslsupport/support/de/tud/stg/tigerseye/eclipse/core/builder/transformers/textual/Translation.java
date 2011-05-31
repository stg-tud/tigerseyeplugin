package de.tud.stg.tigerseye.eclipse.core.builder.transformers.textual;

public @interface Translation {
	/**
	 * A Link to a properties file containing the translations.
	 * 
	 * @return
	 */
	String file() default "[unassigned]";
}
