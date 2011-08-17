package de.tud.stg.popart.builder.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars.HostLanguageGrammar;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars.JavaSpecificGrammar;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.TypeHandler;

/**
 * An Annotation to configure a DSL. Unassigned parameter options have the
 * {@link AnnotationConstants#UNASSIGNED} value
 */
@Inherited
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface DSL {
	String parameterEscape() default AnnotationConstants.UNASSIGNED;

	String whitespaceEscape() default AnnotationConstants.UNASSIGNED;

	String arrayDelimiter() default AnnotationConstants.UNASSIGNED;

	String stringQuotation() default AnnotationConstants.UNASSIGNED;

	Class<? extends TypeHandler>[] typeRules() default {};

	Class<? extends HostLanguageGrammar>[] hostLanguageRules() default { JavaSpecificGrammar.class };

	boolean waterSupported() default true;

}
