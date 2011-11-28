package de.tud.stg.tigerseye.dslsupport.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars.HostLanguageGrammar;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars.JavaSpecificGrammar;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.TypeHandler;

/**
 * 
 * 
 * Class wide DSL annotations. Specific properties can be adjusted method or
 * parameter wise.
 * 
 * @author Leo_Roos
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DSLClass {

	String parameterEscape() default AnnotationConstants.UNASSIGNED;

	String whitespaceEscape() default AnnotationConstants.UNASSIGNED;

	String arrayDelimiter() default AnnotationConstants.UNASSIGNED;

	String stringQuotation() default AnnotationConstants.UNASSIGNED;
	
	Class<? extends TypeHandler>[] typeRules() default {};

	Class<? extends HostLanguageGrammar>[] hostLanguageRules() default { JavaSpecificGrammar.class };

	boolean waterSupported() default true;

}
