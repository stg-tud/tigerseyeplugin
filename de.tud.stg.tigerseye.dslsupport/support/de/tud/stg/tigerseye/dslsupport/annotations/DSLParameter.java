package de.tud.stg.tigerseye.dslsupport.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
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
@Inherited
public @interface DSLParameter {

	String arrayDelimiter() default AnnotationConstants.UNASSIGNED;

	/**
	 * TODO(Leo_Roos;Sep 1, 2011) how can this element on a parameter have
	 * impact on the transformation? Delete?
	 */
	String stringQuotation() default AnnotationConstants.UNASSIGNED;

}
