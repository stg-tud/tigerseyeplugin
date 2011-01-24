package de.tud.stg.popart.builder.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.tud.stg.popart.builder.core.GrammarBuilder;
import de.tud.stg.popart.builder.core.HostLanguageGrammar;
import de.tud.stg.popart.builder.core.JavaSpecificGrammar;
import de.tud.stg.popart.builder.core.typeHandling.TypeHandler;

/**
 * An Annotation to configure a DSL. Elements that have {@code [unassigned]} as their default values are defined in {@link
 * GrammarBuilder}
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
