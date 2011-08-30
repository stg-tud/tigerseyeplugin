package de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction;

import groovy.lang.GroovyObjectSupport;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import javax.annotation.Nonnull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import de.tud.stg.popart.builder.core.annotations.DSLClass;
import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars.HostLanguageGrammar;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ConfigurationOptions;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.TypeHandler;

public class ExtractedClassInforamtion extends ExtractorBase {
    private boolean annotated;

    @Nonnull
    private DSLClass classAnnotation = ExtractorDefaults.DEFAULT_DSLClass;
    @Nonnull
    private Map<ConfigurationOptions, String> classoptions = ExtractorDefaults.DEFAULT_CONFIGURATIONOPTIONS_MAP;
    @Nonnull
    private final Class<?> clazz;

    @Nonnull
    private final List<ExtractedMethodInformation> methodsInformation = new ArrayList<ExtractedMethodInformation>();

    public ExtractedClassInforamtion(Class<?> clazz) {
	this.clazz = clazz;
    }

    @Override
    public Map<ConfigurationOptions, String> getConfigurationOptions() {
	return classoptions;
    }

    public Set<Class<? extends HostLanguageGrammar>> getHostLanguageRules() {
	Set<Class<? extends HostLanguageGrammar>> hashSet = new HashSet<Class<? extends HostLanguageGrammar>>();
	hashSet.addAll(Arrays.asList(this.classAnnotation.hostLanguageRules()));
	return hashSet;
    }

    public List<ExtractedMethodInformation> getMethodsInformation() {
	return methodsInformation;
    }

    public Set<Class<? extends TypeHandler>> getTypeRules() {
	Set<Class<? extends TypeHandler>> hashSet = new HashSet<Class<? extends TypeHandler>>();
	hashSet.addAll(Arrays.asList(this.classAnnotation.typeRules()));
	return hashSet;
    }

    @Override
    public boolean isAnnotated() {
	return annotated;
    }

    public boolean isWaterSupported() {
	return this.classAnnotation.waterSupported();
    }

    @Override
    public void load(Map<ConfigurationOptions, String> defaultParameterOptions) {
	assertAvoidanceOfDSLAnnotation(clazz, DSLMethod.class);
	DSLClass annotation = clazz.getAnnotation(DSLClass.class);
	if (annotation != null) {
	    this.annotated = true;
	    this.classoptions = getAnnotationParameterOptionsOverInitialMap(
		    annotation,
		    ExtractorDefaults.DEFAULT_CONFIGURATIONOPTIONS_MAP);
	    this.classAnnotation = annotation;
	}

	Set<Method> methods = extractAllRelevantMethods(clazz);

	for (Method method : methods) {
	    ExtractedMethodInformation methodInfo = new ExtractedMethodInformation(
		    method);
	    methodInfo.load(classoptions);
	    this.methodsInformation.add(methodInfo);
	}
    }

    /**
     * get all methods, including inherited ones, filter special ones.
     */
    static Set<Method> extractAllRelevantMethods(Class<?> clazz) {
	Set<Method> methods = new LinkedHashSet<Method>();
	// Extract only the public members
	methods.addAll(Arrays.asList(clazz.getMethods()));
	// Method[] declaredMethods = clazz.getDeclaredMethods();
	// methods.addAll(Arrays.asList(declaredMethods));
	methods.removeAll(Arrays.asList(Object.class.getDeclaredMethods()));
	//
	// The extended Interpreter class also contains GroovyObjectSupport
	// also removes eval()
	// List<Method> interpreterMethodsContainingGroovyObjectSupport = Arrays
	// .asList(Interpreter.class.getMethods());
	// getPublicMethodsComparable(declaredMethods)
	methods = filterMethodsByNameAndParameter(methods,
		groovyObjectSupportMethods);
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
	return methods;
    }

    static Set<Method> filterMethodsByNameAndParameter(
	    Set<Method> methods, Set<ComparableMethod> toremove) {
	Set<ComparableMethod> comparableMethods = getComparableMethods(methods);
	comparableMethods.removeAll(toremove);
	Set<Method> hashSet = comparableToNormalMethod(comparableMethods);
	return hashSet;
    }

    private static Set<Method> comparableToNormalMethod(
	    Collection<ComparableMethod> comparableMethods) {
	HashSet<Method> hashSet = new HashSet<Method>();
	for (ComparableMethod comparableMethod : comparableMethods) {
	    hashSet.add(comparableMethod.getMethod());
	}
	return hashSet;
    }

    static Map<ConfigurationOptions, String> getAnnotationParameterOptionsOverInitialMap(
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

    private static final Set<ComparableMethod> groovyObjectSupportMethods = getGroovyObjectSupportMethods();

    /**
     * provides an equals method for {@link Method} objects without considering
     * the enclosing class of a method
     * 
     * @author Leo_Roos
     * 
     */
    public static class ComparableMethod {

	private final Method method;

	public ComparableMethod(Method m) {
	    this.name = m.getName();
	    ArrayList<Class<?>> partypes = new ArrayList<Class<?>>();
	    Collections.addAll(partypes, m.getParameterTypes());
	    this.parameterTypes = partypes;
	    this.method = m;
	}

	public Method getMethod() {
	    return method;
	}

	final String name;
	final List<Class<?>> parameterTypes;

	@Override
	public boolean equals(Object obj) {
	    if (obj == null)
		return false;
	    if (obj == this)
		return true;
	    if (obj.getClass() != this.getClass()) {
		return false;
	    }
	    ComparableMethod other = (ComparableMethod) obj;
	    return new EqualsBuilder().append(name, other.name)
		    .append(parameterTypes, other.parameterTypes).isEquals();
	}

	@Override
	public int hashCode() {
	    return new HashCodeBuilder(5, 37).append(name).toHashCode();
	}

	@Override
	public String toString() {
	    return new ToStringBuilder(this).append(name)
		    .append(parameterTypes).toString();
	}
    }

    private static Set<ComparableMethod> getGroovyObjectSupportMethods() {
	Method[] declaredMethods = GroovyObjectSupport.class
		.getDeclaredMethods();
	Set<Method> methodsWithModifier = getMethodsWithModifier(
		Arrays.asList(declaredMethods), Modifier.PUBLIC);
	Set<ComparableMethod> comps = getComparableMethods(methodsWithModifier);
	return comps;
    }

    private static Set<Method> getMethodsWithModifier(
	    Collection<Method> methods, int modifier) {
	HashSet<Method> hashSet = new HashSet<Method>();
	for (Method method : methods) {
	    if ((method.getModifiers() & modifier) != 0) {
		hashSet.add(method);
	    }
	}
	return hashSet;
    }

    static Set<ComparableMethod> getComparableMethods(
	    Collection<Method> declaredMethods) {
	HashSet<ComparableMethod> comps = new HashSet<ComparableMethod>();
	for (Method method : declaredMethods) {
	    ComparableMethod cm = new ComparableMethod(method);
	    comps.add(cm);
	}
	return comps;
    }

}