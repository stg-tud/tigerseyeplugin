package de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction;

import groovy.lang.GroovyObjectSupport;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import javax.annotation.Nonnull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.dslsupport.annotations.DSLClass;
import de.tud.stg.tigerseye.dslsupport.annotations.DSLMethod;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars.HostLanguageGrammar;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ConfigurationOptions;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.TypeHandler;
import de.tud.stg.tigerseye.eclipse.core.internal.WorkspaceProjectClassLoaderStrategy;

/**
 * A parsing wrapper around a DSL class. Extracts all DSL specific information.
 * Methods can be accessed via {@link #getMethodsInformation()}. To avoid
 * Exceptions during creation the class has to be loaded before the information
 * are extracted:
 * 
 * <pre>
 * cdi = new ClassDSLInformation(SomeClass.class)
 * cdi.load();
 * </pre>
 * 
 * @author Leo_Roos
 * 
 */
public class ClassDSLInformation extends DSLInformation {

    private static final Logger logger = LoggerFactory.getLogger(ClassDSLInformation.class);

    private boolean annotated;

    @Nonnull
    private DSLClass classAnnotation = DSLInformationDefaults.DEFAULT_DSLClass;
    @Nonnull
    private final Class<?> clazz;

    @Nonnull
    private final List<MethodDSLInformation> methodsInformation = new ArrayList<MethodDSLInformation>();

    /**
     * Create a new Instance. Don't forget to call {@link #load()} or
     * {@link #load(Map)}.
     * 
     * @param clazz
     */
    public ClassDSLInformation(Class<?> clazz) {
	this.clazz = clazz;
    }

    public Set<Class<? extends HostLanguageGrammar>> getHostLanguageRules() {
	Set<Class<? extends HostLanguageGrammar>> hashSet = new HashSet<Class<? extends HostLanguageGrammar>>();
	hashSet.addAll(Arrays.asList(this.classAnnotation.hostLanguageRules()));
	return hashSet;
    }

    public List<MethodDSLInformation> getMethodsInformation() {
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
	DSLClass annotation = clazz.getAnnotation(DSLClass.class);
	if (annotation != null) {
	    this.annotated = true;
	    setConfigurationOptions(getAnnotationParameterOptionsOverInitialMap(annotation,
		    DSLInformationDefaults.DEFAULT_CONFIGURATIONOPTIONS_MAP));
	    this.classAnnotation = annotation;
	}

	Set<Method> methods = Collections.emptySet();
	try {
	    methods = extractAllRelevantMethods(clazz);
	} catch (NoClassDefFoundError e) {
	    logger.warn(
		    "Failed to extract methods from class {}.\nProbably the class uses libraries that can not be loaded by the Tigerseye Plug-in.",
		    clazz, e);
	}
	// FIXME(Leo_Roos;Aug 31, 2011) load annotations from inherited methods
	for (Method method : methods) {
	    MethodDSLInformation methodInfo = new MethodDSLInformation(method);
	    methodInfo.load(getConfigurationOptions());
	    this.methodsInformation.add(methodInfo);
	}
    }

    /**
     * get all methods, including inherited ones, filter special ones.
     */
    static Set<Method> extractAllRelevantMethods(Class<?> clazz) {
	Set<Method> methods = new LinkedHashSet<Method>();
	Collections.addAll(methods, clazz.getMethods());
	methods.removeAll(Arrays.asList(Object.class.getDeclaredMethods()));
	methods = filterSpecialGeneratedMethods(methods);
	/*
	 * There is no possibility to check for whether it is groovy class other
	 * than asking for the methods which could be implemented by the user.
	 * Instance does not work since Groovy seems to weave the methods
	 * directly to the class instead of changing its hierarchy.
	 */
	methods = filterMethodsByNameAndParameter(methods, groovyObjectSupportMethods);
	// TODO(Leo_Roos;Aug 30, 2011) consider to also remove the method from
	// the Interpreter class
	return methods;
    }

    private static Set<Method> filterSpecialGeneratedMethods(Set<Method> methods) {
	/*
	 * Quick and Dirty approach to filter some of groovy generated methods
	 * which contains multiple $ signs. Bad luck for the user who actually
	 * wants to define a method containing a $ sign as well
	 */
	Iterator<Method> iterator = methods.iterator();
	while (iterator.hasNext()) {
	    Method next = iterator.next();
	    boolean specialMethod = next.getName().contains(DSLInformationDefaults.SUBSTRING_DEFINING_FILTERED_METHODS);
	    if (specialMethod)
		iterator.remove();
	}
	return methods;
    }

    static Set<Method> filterMethodsByNameAndParameter(Set<Method> methods, Set<ComparableMethod> toremove) {
	Set<ComparableMethod> comparableMethods = getComparableMethods(methods);
	comparableMethods.removeAll(toremove);
	Set<Method> hashSet = comparableToNormalMethod(comparableMethods);
	return hashSet;
    }

    private static Set<Method> comparableToNormalMethod(Collection<ComparableMethod> comparableMethods) {
	HashSet<Method> hashSet = new HashSet<Method>();
	for (ComparableMethod comparableMethod : comparableMethods) {
	    hashSet.add(comparableMethod.getMethod());
	}
	return hashSet;
    }

    static Map<ConfigurationOptions, String> getAnnotationParameterOptionsOverInitialMap(DSLClass dslAnnotation,
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
	    return new EqualsBuilder().append(name, other.name).append(parameterTypes, other.parameterTypes).isEquals();
	}

	@Override
	public int hashCode() {
	    return new HashCodeBuilder(5, 37).append(name).toHashCode();
	}

	@Override
	public String toString() {
	    return new ToStringBuilder(this).append(name).append(parameterTypes).toString();
	}
    }

    private static Set<ComparableMethod> getGroovyObjectSupportMethods() {
	Method[] declaredMethods = GroovyObjectSupport.class.getDeclaredMethods();
	Set<Method> methodsWithModifier = getMethodsWithModifier(Arrays.asList(declaredMethods), Modifier.PUBLIC);
	Set<ComparableMethod> comps = getComparableMethods(methodsWithModifier);
	return comps;
    }

    private static Set<Method> getMethodsWithModifier(Collection<Method> methods, int modifier) {
	HashSet<Method> hashSet = new HashSet<Method>();
	for (Method method : methods) {
	    if ((method.getModifiers() & modifier) != 0) {
		hashSet.add(method);
	    }
	}
	return hashSet;
    }

    static Set<ComparableMethod> getComparableMethods(Collection<Method> declaredMethods) {
	HashSet<ComparableMethod> comps = new HashSet<ComparableMethod>();
	for (Method method : declaredMethods) {
	    ComparableMethod cm = new ComparableMethod(method);
	    comps.add(cm);
	}
	return comps;
    }

    // TODO(Leo_Roos;Sep 1, 2011) perhaps I should at least build in one such
    // check here
    /**
     * A similar thing now happens also in the
     * {@link WorkspaceProjectClassLoaderStrategy} could extend it to be used
     * during an DSLinitialization.
     * 
     */
    private void validateClassIsLoadedInSameClassloader(Method method) {
	Annotation[] annotations = method.getAnnotations();
	for (Annotation anno : annotations) {
	    Class<? extends Annotation> annotationType = anno.annotationType();
	    if (annotationType.getName().equals(DSLMethod.class.getName())) {
		boolean isPopartType = anno instanceof DSLMethod;
		if (!isPopartType)
		    throw new IllegalStateException(
			    "Loaded class "
				    + method.getClass()
				    + " has as expected a dsl method annotation "
				    + DSLMethod.class
				    + ". But the class is not considered equal to it. The problem is probably caused by a faulty class loader configuration where the class is loaded from a different context in which it is here processed.");
	    }
	}
    }

}