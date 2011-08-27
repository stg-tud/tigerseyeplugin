package de.tud.stg.popart.builder.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Support subset of {@link DSLMethod} annotations. <br>
 * Default values are {@link AnnotationConstants#UNASSIGNED}. If the elements
 * have been defined on method or class level via {@link DSLMethod} or
 * {@link DSLClass} they will be overwritten for the annotated parameter.
 * 
 * @see DSLMethod
 * @see DSLClass
 * @author Leo_Roos
 * 
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface DSLParameter {

	String arrayDelimiter() default AnnotationConstants.UNASSIGNED;

	String stringQuotation() default AnnotationConstants.UNASSIGNED;

}
