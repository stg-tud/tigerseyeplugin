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
 * An Annotation to configure a DSL. Elements that have {@code [unassigned]} as
 * their default values are defined in
 * {@link de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarBuilder}
 */
@Inherited
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface DSL {
	String parameterEscape() default "[unassigned]";

	String whitespaceEscape() default "[unassigned]";

	String arrayDelimiter() default "[unassigned]";

	String stringQuotation() default "[unassigned]";

	Class<? extends TypeHandler>[] typeRules() default {};

	Class<? extends HostLanguageGrammar>[] hostLanguageRules() default { JavaSpecificGrammar.class };

	boolean waterSupported() default true;
}
