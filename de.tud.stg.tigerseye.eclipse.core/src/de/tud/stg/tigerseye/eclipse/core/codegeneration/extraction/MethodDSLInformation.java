package de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.tud.stg.popart.builder.core.annotations.AnnotationConstants;
import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.builder.core.annotations.DSLMethod.Associativity;
import de.tud.stg.popart.builder.core.annotations.DSLMethod.DslMethodType;
import de.tud.stg.popart.builder.core.annotations.DSLMethod.PreferencePriority;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ConfigurationOptions;

/**
 * Extracts DSLMethod annotation informations. If none have been set the methods
 * will return default values.
 * 
 * @author Leo_Roos
 * 
 */
public class MethodDSLInformation extends DSLInformation {
    private static final String LITERAL_IDENTIFER_ON_METHOD_NAME = "get";

    @Nullable
    private DSLMethod dslMethodAnnotation = DSLAnnotationDefaults.DEFAULT_DSLMethod;

    private boolean hasDSLMethodAnnotation = false;
    @Nonnull
    final Method method;
    @Nonnull
    Map<ConfigurationOptions, String> methodOptions = DSLAnnotationDefaults.DEFAULT_CONFIGURATIONOPTIONS_MAP;

    public MethodDSLInformation(Method method) {
	this.method = method;
    }

    @Override
    public Map<ConfigurationOptions, String> getConfigurationOptions() {
	return this.methodOptions;
    }

    public DslMethodType getDSLType() {
	if (isAnnotated()) {
	    // Annotation has Priority
	    return this.dslMethodAnnotation.type();
	} else if (methodNameHasLiteralForm()) {
	    return DslMethodType.Literal;
	} else {
	    // Otherwise return default
	    return this.dslMethodAnnotation.type();
	}
    }

    /**
     * @return <code>true</code> if method name begins with literal identifying
     *         string and is longer as that prefix.
     */
    private boolean methodNameHasLiteralForm() {
	String name = getMethod().getName();
	if (name.length() > LITERAL_IDENTIFER_ON_METHOD_NAME.length()) {
	    return name.startsWith(LITERAL_IDENTIFER_ON_METHOD_NAME);
	}
	return false;
    }

    public Method getMethod() {
	return method;
    }

    @Override
    public boolean isAnnotated() {
	return hasDSLMethodAnnotation;
    }

    public boolean isToplevel() {
	return this.dslMethodAnnotation.topLevel();
    }

    /**
     * Actually extract the method information
     * 
     * @param defaultParameterOptions
     */
    @Override
    public void load(Map<ConfigurationOptions, String> defaultParameterOptions) {
	assertAvoidanceOfDSLAnnotation(method, DSLMethod.class);
	DSLMethod annotation = method.getAnnotation(DSLMethod.class);
	if (annotation == null) {
	    hasDSLMethodAnnotation = false;
	} else {
	    hasDSLMethodAnnotation = true;
	    this.dslMethodAnnotation = annotation;
	    this.methodOptions = getAnnotationParameterOptionsOverInitialMap(
		    this.dslMethodAnnotation, defaultParameterOptions);
	}
	String definedWS = getConfiguratinoOption(ConfigurationOptions.WHITESPACE_ESCAPE);
	if (definedWS.isEmpty()) {
	    setDefaultWSE();
	} else if (!hasProductionInAnnotationdDefined()) {
	    // Not annotated uses method name as production have to narrow
	    // possibly inherited whitespace escapes to valid java identifier
	    if (!Character.isJavaIdentifierPart(definedWS.charAt(0))) {
		setDefaultWSE();
	    }
	}
    }

    private boolean hasProductionInAnnotationdDefined() {
	if (AnnotationConstants.UNASSIGNED.equals(this.dslMethodAnnotation
		.production())) {
	    return false;
	} else {
	    return true;
	}
    }

    private void setDefaultWSE() {
	String defaultWSE = ConfigurationOptions.WHITESPACE_ESCAPE.defaultValue;
	this.methodOptions.put(ConfigurationOptions.WHITESPACE_ESCAPE,
		defaultWSE);
    }

    static Map<ConfigurationOptions, String> getAnnotationParameterOptionsOverInitialMap(
	    DSLMethod dslAnnotation,
	    Map<ConfigurationOptions, String> initialMap) {
	Map<ConfigurationOptions, String> resultMap = new HashMap<ConfigurationOptions, String>(
		initialMap);
	if (dslAnnotation != null) {
	    putIfValid(resultMap, ConfigurationOptions.PARAMETER_ESCAPE,
		    dslAnnotation.parameterEscape());
	    putIfValid(resultMap, ConfigurationOptions.WHITESPACE_ESCAPE,
		    dslAnnotation.whitespaceEscape());
	    putIfValid(resultMap, ConfigurationOptions.ARRAY_DELIMITER,
		    dslAnnotation.arrayDelimiter());
	    putIfValid(resultMap, ConfigurationOptions.STRING_QUOTATION,
		    dslAnnotation.stringQuotation());
	}
	return resultMap;
    }

    @Override
    public String toString() {
	ToStringBuilder append = new ToStringBuilder(this,
		ToStringStyle.SHORT_PREFIX_STYLE)
		.append("annotation", dslMethodAnnotation.getClass())
		.append("isannotated", hasDSLMethodAnnotation)
		.append("configurationoptions", methodOptions)
		.append("underlyingmethod", method)
		.append("production", getProductionRaw());
	return append.toString();
    }

    /**
     * @return the {@link DSLMethod#production()} element. If it is the default
     *         value, i.e. either the method has not been annotated or
     *         production has not been used in annotation <code>null</code> will
     *         be returned.
     */
    public @CheckForNull
    String getProductionRaw() {
	String production;
	if (hasProductionInAnnotationdDefined())
	    production = this.dslMethodAnnotation.production();
	else {
	    production = getProductionFromMethodName();
	}
	return production;
    }

    private String getProductionFromMethodName() {
	String methodNameRaw = getMethod().getName();
	String methodName;
	DslMethodType dslType = getDSLType();
	switch (dslType) {
	case Literal:
	    String tosubtractString = LITERAL_IDENTIFER_ON_METHOD_NAME;
	    if (methodNameRaw.length() <= tosubtractString.length())
		methodName = methodNameRaw;
	    else {
		String removePrecedingString = removePrecedingString(
			methodNameRaw, tosubtractString);
		methodName = firstCharToLowercase(removePrecedingString);
	    }
	    break;
	case AbstractionOperator:
	    //$FALL-THROUGH$
	case Operation:
	    methodName = methodNameRaw;
	    break;
	default:
	    throw new IllegalArgumentException("unhandled type: " + dslType);
	}
	return methodName;
    }

    /**
     * @param methodNameRaw
     * @return methodNameRaw without a possible get. returns the passesd string
     *         if it's lenght is smaller or equal to the string that would be
     *         subtracted from it. if the name is just {@code get}. the first
     *         character of the resulting string will be made to lowercase.
     */
    private String removePrecedingString(String methodNameRaw,
	    String tosubtractString) {
	if (methodNameRaw.startsWith(tosubtractString)) {
	    return methodNameRaw.substring(tosubtractString.length());
	} else
	    return methodNameRaw;
    }

    private String firstCharToLowercase(String substring) {
	if (substring.isEmpty())
	    return substring;
	String firstChar = substring.substring(0, 1)
		.toLowerCase(Locale.ENGLISH);
	return firstChar + substring.substring(1);
    }

    public int getAbsolutePriority() {
	return this.dslMethodAnnotation.absolutePriority();
    }

    public Associativity getAssociativity() {
	return this.dslMethodAnnotation.associativity();
    }

    public PreferencePriority getPreferencePriority() {
	return this.dslMethodAnnotation.preferencePriority();
    }

    public String getPriorityHigherThan() {
	return this.dslMethodAnnotation.priorityHigherThan();
    }

    public String getPriorityLowerThan() {
	return this.dslMethodAnnotation.priorityLowerThan();
    }

    public String getUniqueIdentifier() {
	String uniqueIdentifier = this.dslMethodAnnotation.uniqueIdentifier();
	if (AnnotationConstants.UNASSIGNED.equals(uniqueIdentifier)) {
	    uniqueIdentifier = crateUniqueIdentifierFromMethod();
	}
	return uniqueIdentifier;
    }

    String crateUniqueIdentifierFromMethod() {
	String uniqueIdentifier;
	String parameters = buildParametersString(getMethod()
		.getParameterTypes());
	uniqueIdentifier = getMethod().getDeclaringClass().getName()
		+ getMethod().getName() + parameters;
	return uniqueIdentifier;
    }

    private String buildParametersString(Class<?>[] parameterTypes) {
	StringBuilder pts = new StringBuilder("(");
	List<Class<?>> asList = Arrays.asList(parameterTypes);
	Iterator<Class<?>> arrayIterator = asList.iterator();
	while (arrayIterator.hasNext()) {
	    Class<?> class1 = arrayIterator.next();
	    pts.append(class1.getName());
	    if (arrayIterator.hasNext()) {
		pts.append(",");
	    }
	}
	pts.append(")");
	return pts.toString();
    }
}