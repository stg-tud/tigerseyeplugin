package de.tud.stg.popart.builder.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ParameterOptionDefaults;

/**
 * Annotation to define the concrete syntax of an embedded DSL element. Elements
 * shared with {@link DSLClass} are overwritten in the scope of the the
 * annotated method.
 * <p>
 * To define the right-hand-side of the production the element
 * {@link #production()} is used. <br>
 * This annotation provides elements to configure the way a production is
 * interpreted via {@link #parameterEscape()} and {@link #whitespaceEscape()}.<br>
 * The interpretation of arrays and strings can be adjusted using
 * {@link #arrayDelimiter()} and {@link #stringQuotation()}.<br>
 * The default values are defined in {@link ParameterOptionDefaults}. <br>
 * <p>
 * A definition can be defined {@link #topLevel()} in which case it can not be
 * part of another production.
 * <p>
 * To avoid ambiguities different types of priorities are supported. They may be
 * mixed at will but a combination might be ignored if it doesn't make sense. <br>
 * Priorities can be defined
 * <ul>
 * <li>absolute, see {@link #absolutePriority()},
 * <li>by general preference, see {@link #preferencePriority()},
 * <li>relative, see {@link #priorityHigherThan()} and
 * {@link #priorityLowerThan()}.
 * </ul>
 * <p>
 * 
 * 
 * @see DSLParameter
 * @see DSLClass
 * 
 * @author Leo_Roos
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DSLMethod {

	String parameterEscape() default AnnotationConstants.UNASSIGNED;

	String whitespaceEscape() default AnnotationConstants.UNASSIGNED;

	String arrayDelimiter() default AnnotationConstants.UNASSIGNED;

	/**
	 * This element represents a regular expression as defined by
	 * {@link Pattern}. It captures all elements that are supposed to be
	 * interpreted as strings. For example
	 * 
	 * <pre>
	 * stringQuotation = &quot;\&quot;.*?\&quot;&quot;
	 * </pre>
	 * 
	 * interprets every string in the processed file beginning and ending with a
	 * {@code "} as a string. The example will however not support strings with
	 * escaped quotations. To support escaping string quotations the regex has
	 * to be adjusted to for example something like
	 * 
	 * <pre>
	 * stringQuotation = &quot;\&quot;.*?(?&lt;!\\)\&quot;&quot;
	 * </pre>
	 * 
	 * which describes a negative look behind before the closing quotation
	 * character that tells the matcher only to accept a closing {@code "} if it
	 * is not preceded with a {@code \} character.
	 */
	String stringQuotation() default AnnotationConstants.UNASSIGNED;

	/**
	 * The production name of the describe method. It's default value is
	 * {@link AnnotationConstants#UNASSIGNED}.
	 */
	String production() default AnnotationConstants.UNASSIGNED;

	/**
	 * Defines if this method is a top level statement or can only be referenced
	 * by other rules. This assures that rules like p0 = p1 can be referenced in
	 * another rule like [p0] without global scope for the first rule.
	 * 
	 * @return <code>true</code> if this is a top level statement,
	 *         <code>false</code> otherwise
	 */
	boolean topLevel() default true;

	/**
	 * Describes the type of an annotated method.
	 * 
	 * @author Leo_Roos
	 * 
	 */
	public enum DslMethodType {
		AbstractionOperator, Literal, Operation
	};

	/**
	 * The type of the syntax definition. Default is
	 * {@link DslMethodType#Operation}
	 * 
	 * @return the type of an annotated method.
	 */
	DslMethodType type() default DslMethodType.Operation;

	public enum Associativity {
		LEFT, RIGHT, NONE;
	}

	Associativity associativity() default Associativity.NONE;

	/**
	 * Define priority by absolute value. Default is 0. Every integer value is
	 * valid.
	 */
	int absolutePriority() default 0;

	public enum PreferencePriority {
		Avoid, Reject, Prefer, NONE;
	}

	/**
	 * Define priority by general preference. Default is
	 * {@link PreferencePriority#NONE}.
	 */
	PreferencePriority preferencePriority() default PreferencePriority.NONE;

	/**
	 * defines a priority lower than another syntax definition.
	 * <p>
	 * The other definition can be chosen via its {@link #uniqueIdentifier()}.
	 * This will be either the fully qualified name of the annotated method or a
	 * user defined identifier. The selection is valid using any substring of a
	 * valid identifier. This might be ambiguous in which case the first found
	 * definition will be assumed.
	 * 
	 * @see #uniqueIdentifier()
	 */
	String priorityLowerThan() default AnnotationConstants.UNASSIGNED;

	/**
	 * defines a priority higher than another syntax definition.
	 * <p>
	 * The other definition can be chosen via its {@link #uniqueIdentifier()}.
	 * This will be either the fully qualified name of the annotated method or a
	 * user defined identifier. The selection is valid using any substring of a
	 * valid identifier. This might be ambiguous in which case the first found
	 * definition will be assumed.
	 * 
	 * @see #uniqueIdentifier()
	 */
	String priorityHigherThan() default AnnotationConstants.UNASSIGNED;

	/**
	 * Defines the unique identifier of this definition. If not assigned the
	 * fully qualified name of the annotated method will be used.
	 */
	String uniqueIdentifier() default AnnotationConstants.UNASSIGNED;

}
