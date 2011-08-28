package de.tud.stg.tigerseye.eclipse.core.codegeneration;

import groovy.lang.GroovyObjectSupport;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.core.runtime.Assert;

import de.tud.stg.popart.builder.core.annotations.AnnotationConstants;
import de.tud.stg.popart.builder.core.annotations.DSL;
import de.tud.stg.popart.builder.core.annotations.DSLClass;
import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.builder.core.annotations.DSLMethod.DslMethodType;
import de.tud.stg.popart.builder.core.annotations.DSLParameter;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars.HostLanguageGrammar;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ConfigurationOptions;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.TypeHandler;

/**
 * Extracts the DSL information
 * 
 * @author Leo_Roos
 * 
 */
public class Extractor {

    public static class DefaultedAnnotation implements InvocationHandler {
	@SuppressWarnings("unchecked")
	public static <A extends Annotation> A of(Class<A> annotation) {
	    return (A) Proxy.newProxyInstance(annotation.getClassLoader(),
		    new Class[] { annotation }, new DefaultedAnnotation());
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable {
	    return method.getDefaultValue();
	}
    }

    public static class ExtractedClassInforamtion {
	private boolean annotated;

	@Nonnull
	private final DSLClass classAnnotation = DEFAULT_DSLClass;
	@Nonnull
	private Map<ConfigurationOptions, String> classoptions = DEFAULT_CONFIGURATIONOPTIONS_VALUES;
	@Nonnull
	private final Class<?> clazz;

	@Nonnull
	private final List<Extractor.ExtractedMethodsInformation> methodsInformation = new ArrayList<Extractor.ExtractedMethodsInformation>();

	public ExtractedClassInforamtion(Class<?> clazz) {
	    this.clazz = clazz;
	}

	public Map<ConfigurationOptions, String> getConfiguratinoOptions() {
	    return classoptions;
	}

	public Class<? extends HostLanguageGrammar>[] getHostLanguageRules() {
	    return this.classAnnotation.hostLanguageRules();
	}

	public List<Extractor.ExtractedMethodsInformation> getMethodsInformation() {
	    return methodsInformation;
	}

	public Class<? extends TypeHandler>[] getTypeRules() {
	    return this.classAnnotation.typeRules();
	}

	public boolean isAnnotated() {
	    return annotated;
	}

	public boolean isWaterSupported() {
	    return this.classAnnotation.waterSupported();
	}

	public void load() {
	    assertAvoidanceOfDSLAnnotation(clazz, DSLMethod.class);
	    DSLClass annotation = clazz.getAnnotation(DSLClass.class);
	    if (annotation != null) {
		this.annotated = true;
		this.classoptions = getAnnotationParameterOptionsOverInitialMap(
			annotation, DEFAULT_CONFIGURATIONOPTIONS_VALUES);
		// this.classAnnotation = annotation;
	    }

	    Set<Method> methods = extractAllRelevantMethods(clazz);

	    for (Method method : methods) {
		ExtractedMethodsInformation methodInfo = new ExtractedMethodsInformation(
			method);
		methodInfo.load(classoptions);
		this.methodsInformation.add(methodInfo);
	    }
	}
    }

    /**
     * Extracts DSLMethod annotation informations. If none have been set the
     * methods will return default values.
     * 
     * @author Leo_Roos
     * 
     */
    public static class ExtractedMethodsInformation {
	@Nullable
	private DSLMethod dslMethodAnnotation = DEFAULT_DSLMethod;

	private boolean hasDSLMethodAnnotation = false;
	@Nonnull
	final Method method;
	@Nonnull
	Map<ConfigurationOptions, String> methodOptions = DEFAULT_CONFIGURATIONOPTIONS_VALUES;

	public ExtractedMethodsInformation(Method method) {
	    this.method = method;
	}

	public Map<ConfigurationOptions, String> getConfigurationOptions() {
	    return this.methodOptions;
	}

	public DslMethodType getDSLType() {
	    return this.dslMethodAnnotation.type();
	}

	public Method getMethod() {
	    return method;
	}

	public boolean hasDSLMethodAnnotation() {
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
	public void load(
		Map<ConfigurationOptions, String> defaultParameterOptions) {
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
	}

    }

    private static final Map<ConfigurationOptions, String> DEFAULT_CONFIGURATIONOPTIONS_VALUES = getDefaultOptions();

    private static final DSLClass DEFAULT_DSLClass = DefaultedAnnotation
	    .of(DSLClass.class);

    private static final DSLMethod DEFAULT_DSLMethod = DefaultedAnnotation
	    .of(DSLMethod.class);

    private static final DSLParameter DEFAULT_DSLParameter = DefaultedAnnotation
	    .of(DSLParameter.class);

    // XXX(LeoRoos;Aug 28, 2011) delete when appropriate
    private static void assertAvoidanceOfDSLAnnotation(AnnotatedElement el,
	    Class<? extends Annotation> useInstead) {
	DSL hasDeprecatedAnnotation = el.getAnnotation(DSL.class);
	if (hasDeprecatedAnnotation != null)
	    throw new IllegalArgumentException(DSL.class
		    + " is no longer supported. It was used on " + el
		    + ". Use " + useInstead + " instead.");
    }

    /**
     * get all methods, including inherited ones, filter special ones.
     */
    private static Set<Method> extractAllRelevantMethods(Class<?> clazz) {
	Set<Method> methods = new LinkedHashSet<Method>();
	methods.addAll(Arrays.asList(clazz.getMethods()));
	methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
	methods.removeAll(Arrays.asList(Object.class.getDeclaredMethods()));
	methods.removeAll(Arrays.asList(GroovyObjectSupport.class
		.getDeclaredMethods()));
	/*
	 * Quick and Dirty approach to filter some of groovy generated methods
	 * which contains multiple $ signs. Bad luck for the user who actually
	 * wants to define a method containing a $ sign as well
	 */
	Iterator<Method> iterator = methods.iterator();
	while (iterator.hasNext()) {
	    Method next = iterator.next();
	    boolean specialMethod = next.getName().contains("$");
	    if (specialMethod)
		iterator.remove();
	}
	// perhaps also remove eval()
	return methods;
    }

    private static Map<ConfigurationOptions, String> getAnnotationParameterOptionsOverInitialMap(
	    DSLClass dslAnnotation, Map<ConfigurationOptions, String> initialMap) {
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

    private static Map<ConfigurationOptions, String> getAnnotationParameterOptionsOverInitialMap(
	    DSLParameter dslAnnotation,
	    Map<ConfigurationOptions, String> initialMap) {
	Map<ConfigurationOptions, String> resultMap = new HashMap<ConfigurationOptions, String>(
		initialMap);
	if (dslAnnotation != null) {
	    putIfValid(resultMap, ConfigurationOptions.ARRAY_DELIMITER,
		    dslAnnotation.arrayDelimiter());
	    putIfValid(resultMap, ConfigurationOptions.STRING_QUOTATION,
		    dslAnnotation.stringQuotation());
	}
	return resultMap;
    }

    private static Map<ConfigurationOptions, String> getDefaultOptions() {
	Map<ConfigurationOptions, String> defaultOptions = new HashMap<ConfigurationOptions, String>();
	for (ConfigurationOptions parop : ConfigurationOptions.values()) {
	    defaultOptions.put(parop, parop.defaultValue);
	}
	return defaultOptions;
    }

    /*
     * assigns value with key to resultMap if the value is neither null nor
     * equal to the UNASSIGNED constant.
     */
    private static Map<ConfigurationOptions, String> putIfValid(
	    Map<ConfigurationOptions, String> resultMap,
	    ConfigurationOptions confOption, String value) {
	Assert.isNotNull(value);
	if (value.equals(AnnotationConstants.UNASSIGNED))
	    return resultMap;
	else {
	    resultMap.put(confOption, value);
	    return resultMap;
	}
    }

}
