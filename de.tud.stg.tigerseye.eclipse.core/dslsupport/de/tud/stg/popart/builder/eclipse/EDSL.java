package de.tud.stg.popart.builder.eclipse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
public @interface EDSL {
	String[] value();

	int x() default 4;
}
