package de.tud.stg.popart.builder.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DSLMethod {
	String prettyName() default "[unassigned]";

	/**
	 * Defines if this method is a top level statement or can only be referenced
	 * by other rules. This assures that rules like p0 = p1 can be referenced in
	 * another rule like [p0] without global scope for the first rule.
	 * 
	 * @return <code>true</code> if this is a top level statement,
	 *         <code>false</code> otherwise
	 */
	boolean topLevel() default true;
}
