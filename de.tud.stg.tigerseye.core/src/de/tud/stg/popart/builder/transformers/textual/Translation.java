package de.tud.stg.popart.builder.transformers.textual;

public @interface Translation {
	/**
	 * A Link to a properties file containing the translations.
	 * 
	 * @return
	 */
	String file() default "[unassigned]";
}
