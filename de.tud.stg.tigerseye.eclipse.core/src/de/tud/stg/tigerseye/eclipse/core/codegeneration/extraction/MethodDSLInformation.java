package de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction;

import static de.tud.stg.tigerseye.util.Utils.illegalForArg;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
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
import org.eclipse.core.runtime.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.popart.builder.core.annotations.AnnotationConstants;
import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.builder.core.annotations.DSLMethod.Associativity;
import de.tud.stg.popart.builder.core.annotations.DSLMethod.DslMethodType;
import de.tud.stg.popart.builder.core.annotations.DSLMethod.PreferencePriority;
import de.tud.stg.popart.builder.core.annotations.DSLParameter;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.MethodProductionConstants.ProductionElement;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ConfigurationOptions;

/**
 * Extracts DSLMethod annotation informations. If none have been set the methods
 * will return default values.
 * 
 * @author Leo_Roos
 * 
 */
public class MethodDSLInformation extends DSLInformation {

    private static final Logger logger = LoggerFactory.getLogger(MethodDSLInformation.class);
    private static final String LITERAL_IDENTIFER_ON_METHOD_NAME = "get";

    @Nullable
    private DSLMethod dslMethodAnnotation = DSLInformationDefaults.DEFAULT_DSLMethod;

    private boolean hasDSLMethodAnnotation = false;
    @Nonnull
    final Method method;
    private List<ParameterDSLInformation> parameterInfos;

    public MethodDSLInformation(Method method) {
	this.method = method;
    }

    public DslMethodType getDSLType() {
	if (isAnnotated()) {
	    // Annotation has Priority
	    return this.dslMethodAnnotation.type();
	} else if (methodHasLiteralForm()) {
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
    private boolean methodHasLiteralForm() {
	if (getMethod().getParameterTypes().length > 0)
	    return false;
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
	DSLMethod annotation = method.getAnnotation(DSLMethod.class);
	if (annotation == null) {
	    hasDSLMethodAnnotation = false;
	} else {
	    hasDSLMethodAnnotation = true;
	    this.dslMethodAnnotation = annotation;
	    setConfigurationOptions(getAnnotationParameterOptionsOverInitialMap(this.dslMethodAnnotation,
		    defaultParameterOptions));
	}
	String definedWS = getConfigurationOption(ConfigurationOptions.WHITESPACE_ESCAPE);
	if (definedWS.isEmpty()) {
	    setDefaultWSE();
	} else if (!hasProductionInAnnotationdDefined()) {
	    // Not annotated uses method name as production have to narrow
	    // possibly inherited whitespace escapes to valid java identifier
	    if (!Character.isJavaIdentifierPart(definedWS.charAt(0))) {
		setDefaultWSE();
	    }
	}
	Type[] typeParameters = getMethod().getGenericParameterTypes();
	Annotation[][] parameterAnnotations = getMethod().getParameterAnnotations();
	this.parameterInfos = createParameterInfosList(typeParameters, parameterAnnotations);
    }

    private List<ParameterDSLInformation> createParameterInfosList(Type[] typeParameters,
	    Annotation[][] parameterAnnotations) {
	Assert.isTrue(typeParameters.length == parameterAnnotations.length);

	List<ParameterDSLInformation> result = new ArrayList<ParameterDSLInformation>();
	for (int i = 0; i < typeParameters.length; i++) {
	    Type nextType = typeParameters[i];
	    DSLParameter maybeDSLParameter = findDSLParameterOrNull(parameterAnnotations[i]);

	    ParameterDSLInformation newPI = new ParameterDSLInformation(nextType, maybeDSLParameter, i);
	    newPI.load(getConfigurationOptions());

	    result.add(newPI);
	}

	return result;
    }

    private DSLParameter findDSLParameterOrNull(Annotation[] nextAnnotations) {
	for (Annotation annotation : nextAnnotations) {
	    if (annotation instanceof DSLParameter) {
		return (DSLParameter) annotation;
	    }
	}
	return null;
    }

    private boolean hasProductionInAnnotationdDefined() {
	if (AnnotationConstants.UNASSIGNED.equals(this.dslMethodAnnotation.production())) {
	    return false;
	} else {
	    return true;
	}
    }

    private void setDefaultWSE() {
	String defaultWSE = ConfigurationOptions.WHITESPACE_ESCAPE.defaultValue;
	getConfigurationOptions().put(ConfigurationOptions.WHITESPACE_ESCAPE, defaultWSE);
    }

    static Map<ConfigurationOptions, String> getAnnotationParameterOptionsOverInitialMap(DSLMethod dslAnnotation,
	    Map<ConfigurationOptions, String> initialMap) {
	Map<ConfigurationOptions, String> resultMap = new HashMap<ConfigurationOptions, String>(initialMap);
	if (dslAnnotation != null) {
	    putIfValid(resultMap, ConfigurationOptions.PARAMETER_ESCAPE, dslAnnotation.parameterEscape());
	    putIfValid(resultMap, ConfigurationOptions.WHITESPACE_ESCAPE, dslAnnotation.whitespaceEscape());
	    putIfValid(resultMap, ConfigurationOptions.ARRAY_DELIMITER, dslAnnotation.arrayDelimiter());
	    putIfValid(resultMap, ConfigurationOptions.STRING_QUOTATION, dslAnnotation.stringQuotation());
	}
	return resultMap;
    }

    @Override
    public String toString() {
	ToStringBuilder append = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
		.append("production", getProduction()).append("uniqueIdentifier", getUniqueIdentifier())
		.append("annotation", dslMethodAnnotation.getClass()).append("isannotated", hasDSLMethodAnnotation)
		.append("configurationoptions", getConfigurationOptions()).append("underlyingmethod", method)
		.append("parameterInfos", this.parameterInfos);
	return append.toString();
    }

    /**
     * If production element in the annotation has been used that value will be
     * returned. If the annotations production element has not been used the
     * method name will be returned for dsls of type
     * {@link DslMethodType#Operation}. For {@link DslMethodType#Literal} get
     * prefix will be subtracted.
     * 
     * @return the computed production.
     */
    public String getProduction() {
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
	String fromMethodName;
	DslMethodType dslType = getDSLType();
	switch (dslType) {
	case Literal:
	    String tosubtractString = LITERAL_IDENTIFER_ON_METHOD_NAME;
	    if (methodNameRaw.length() <= tosubtractString.length())
		fromMethodName = methodNameRaw;
	    else {
		String removePrecedingString = removePrecedingString(methodNameRaw, tosubtractString);
		fromMethodName = firstCharToLowercase(removePrecedingString);
	    }
	    break;
	case AbstractionOperator:
	    //$FALL-THROUGH$
	case Operation:
	    fromMethodName = methodNameRaw;
	    break;
	default:
	    throw illegalForArg(dslType);
	}
	return fromMethodName;
    }

    /**
     * @param methodNameRaw
     * @return methodNameRaw without a possible get. returns the passesd string
     *         if it's lenght is smaller or equal to the string that would be
     *         subtracted from it. if the name is just {@code get}. the first
     *         character of the resulting string will be made to lowercase.
     */
    private String removePrecedingString(String methodNameRaw, String tosubtractString) {
	if (methodNameRaw.startsWith(tosubtractString)) {
	    return methodNameRaw.substring(tosubtractString.length());
	} else
	    return methodNameRaw;
    }

    private String firstCharToLowercase(String substring) {
	if (substring.isEmpty())
	    return substring;
	String firstChar = substring.substring(0, 1).toLowerCase(Locale.ENGLISH);
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
	String parameters = buildParametersString(getMethod().getParameterTypes());
	uniqueIdentifier = getMethod().getDeclaringClass().getName() + getMethod().getName() + parameters;
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

    /**
     * TODO(Leo_Roos;Sep 1, 2011) still looking for more assertion to be made.
     * currently only whether literal has return type
     * 
     * @return whether this information and annotated method are actually valid
     *         in their current form.
     */
    public boolean isValid() {
	switch (getDSLType()) {
	case Literal:
	    Class<?> returnType = getMethod().getReturnType();
	    if (isVoid(returnType))
		return false;
	    break;

	default:
	    break;
	}
	return true;
    }

    private boolean isVoid(Class<?> returnType) {
	return returnType == void.class || returnType == Void.class;
    }

    // TODO(Leo_Roos;Sep 1, 2011) Untested
    public List<String> getKeywordList() {

	ArrayList<String> result = new ArrayList<String>();
	switch (getDSLType()) {
	case Literal:
	    result.add(getProduction());
	    break;
	case Operation:
	    MethodProductionScanner mps = new MethodProductionScanner(
		    getConfigurationOption(ConfigurationOptions.WHITESPACE_ESCAPE),
		    getConfigurationOption(ConfigurationOptions.PARAMETER_ESCAPE));
	    mps.startScan(getProduction());
	    for (MethodProductionElement mpe : mps) {
		if (mpe.getProductionElementType().equals(ProductionElement.Keyword)) {
		    result.add(mpe.getCapturedString());
		}
	    }
	    break;
	default:
	    logger.info("unknown handling for type {}", getDSLType());
	    break;
	}
	return result;
    }

    public List<ParameterDSLInformation> getParameterInfos() {
	return this.parameterInfos;
    }

    /**
     * Get the parameter info of the {@code i}th parameter of the wrapped
     * method. Will return <code>null</code> if i is out of bounds.
     * 
     * @param i
     * @return
     */
    public @CheckForNull
    ParameterDSLInformation getParameterInfo(int i) {
	if (this.parameterInfos.size() > i && i >= 0) {
	    return this.parameterInfos.get(i);
	} else {
	    logger.warn("tried to access parameter out of range. {}", i);
	    return null;
	}
    }

    public boolean hasReturnValue() {
	return !isVoid(getMethod().getReturnType());
    }
}