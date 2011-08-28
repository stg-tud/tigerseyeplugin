package de.tud.stg.tigerseye.eclipse.core.codegeneration;

import groovy.lang.GroovyObjectSupport;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.core.runtime.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.parlex.core.Category;
import de.tud.stg.parlex.core.Grammar;
import de.tud.stg.parlex.core.ICategory;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.core.Rule;
import de.tud.stg.parlex.core.groupcategories.WaterCategory;
import de.tud.stg.popart.builder.core.annotations.AnnotationConstants;
import de.tud.stg.popart.builder.core.annotations.DSL;
import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.builder.core.annotations.DSLMethod.DslMethodType;
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars.CategoryNames;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars.HostLanguageGrammar;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ConfigurationOptions;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.utils.WhitespaceCategoryDefinition;
import de.tud.stg.tigerseye.eclipse.core.internal.WorkspaceProjectClassLoaderStrategy;
import de.tud.stg.tigerseye.util.ListBuilder;
import de.tud.stg.tigerseye.util.ListMap;
import de.tud.stg.tigerseye.util.Transformer;

/**
 * {@link GrammarBuilder} builds the grammar for given classes implementing the
 * DSL interface.
 * 
 * @author Kamil Erhard
 * @author Leo Roos
 * 
 */
public class GrammarBuilder {
    private static final Logger logger = LoggerFactory
	    .getLogger(GrammarBuilder.class);

    // private static final String DEFAULT_STRING_QUOTATION =
    // "([\\w_]+|(\".*?\"))";

    private final AtomicInteger parameterCounter = new AtomicInteger();

    /*
     * Only used as left hand side rule or grammar category
     */
    private ICategory<String> statement;
    private ICategory<String> statements;

    private final UnicodeLookupTable unicodeLookupTable;

    public final HashMap<String, MethodOptions> methodAliases = new HashMap<String, MethodOptions>();
    // (Leo Roos; Jun 27, 2011):Never used; still necessary to save the
    // resulting set of keywords?
    private final Set<String> keywords = new LinkedHashSet<String>();

    public GrammarBuilder(UnicodeLookupTable ult) {
	this.unicodeLookupTable = ult;
    }

    private void setWaterEnabled(boolean enabled, Grammar grammar) {
	if (enabled) {
	    ICategory<String> anything = new WaterCategory();
	    grammar.addCategory(anything);

	    Rule rAnyStatement = new Rule(this.statement, anything);
	    grammar.addRule(rAnyStatement);

	    grammar.addWaterRule(rAnyStatement);
	}
    }

    private void setupGeneralGrammar(Grammar grammar) {
	Category program = new Category(CategoryNames.PROGRAM_CATEGORY, false);

	this.statement = new Category(CategoryNames.STATEMENT_CATEGORY, false);
	this.statements = new Category(CategoryNames.STATEMENTS_CATEGORY, false);

	List<ICategory<String>> single = ListBuilder.single(this.statements);
	Rule startRule = new Rule(program, single);

	Rule rStatements = new Rule(this.statements, ListBuilder
		.newList(this.statement)
		.add(WhitespaceCategoryDefinition
			.getAndSetOptionalWhitespace(grammar))
		.add(this.statements).toList());

	Rule rStatement = new Rule(this.statements,
		ListBuilder.single(this.statement));

	grammar.addCategory(program);
	grammar.addCategory(this.statement);
	grammar.addCategory(this.statements);

	grammar.setStartRule(startRule);
	grammar.addRule(rStatement);
	grammar.addRule(rStatements);
    }

    // XXX(Leo Roos;Aug 16, 2011) Untested
    public IGrammar<String> buildGrammarFromDefinitions(List<DSLDefinition> dsls) {
	ArrayList<Class<? extends de.tud.stg.popart.dslsupport.DSL>> clazzes = new ArrayList<Class<? extends de.tud.stg.popart.dslsupport.DSL>>(
		dsls.size());
	for (DSLDefinition dsl : dsls) {
	    Class<? extends de.tud.stg.popart.dslsupport.DSL> loadClass = dsl
		    .getDSLClass();
	    if (loadClass != null)
		clazzes.add(loadClass);
	    else
		logger.error(
			"Failed to load dsl {}, can not perform any transformations for that class",
			dsl);
	}
	return buildGrammar(clazzes);
    }

    /**
     * @param clazzes
     * @return the grammar for DSL classes
     * @deprecated use type safe version {@link #buildGrammar(List)} instead
     */
    @Deprecated
    public IGrammar<String> buildGrammar(
	    Class<? extends de.tud.stg.popart.dslsupport.DSL>... clazzes) {
	List<Class<? extends de.tud.stg.popart.dslsupport.DSL>> clazzess = Arrays
		.asList(clazzes);
	return buildGrammar(clazzess);
    }

    static class ExtractedClassInforamtion {
	public ExtractedClassInforamtion(Class<?> clazz) {
	    this.clazz = clazz;
	}

	@Nonnull
	public Map<ConfigurationOptions, String> classoptions = Collections
		.emptyMap();
	@Nonnull
	public DSL classDslAnnotation = DefaultedAnnotation.of(DSL.class);
	@Nonnull
	public List<ExtractedMethodsInformation> methodsInformation = new ArrayList<ExtractedMethodsInformation>();
	@Nonnull
	public final Class<?> clazz;
    }

    static class DefaultedAnnotation implements InvocationHandler {
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

    static class ExtractedMethodsInformation {
	public ExtractedMethodsInformation(Method method) {
	    this.method = method;
	}

	@Nonnull
	public Map<ConfigurationOptions, String> methodOptions = Collections
		.emptyMap();
	// @Nullable
	// public PopartType popartType;
	@Nonnull
	public final Method method;
	@Nullable
	public DSLMethod dslMethodAnnotation;

    }

    public IGrammar<String> buildGrammar(
	    List<Class<? extends de.tud.stg.popart.dslsupport.DSL>> clazzes) {

	List<ExtractedClassInforamtion> exInfos = extractClassesInformation(clazzes);

	Grammar grammar = createCombinedGrammar(exInfos);

	return grammar;
    }

    private List<ExtractedClassInforamtion> extractClassesInformation(
	    List<Class<? extends de.tud.stg.popart.dslsupport.DSL>> clazzes) {
	List<ExtractedClassInforamtion> exannos = ListMap
		.map(clazzes,
			new Transformer<Class<? extends de.tud.stg.popart.dslsupport.DSL>, ExtractedClassInforamtion>() {

			    @Override
			    public ExtractedClassInforamtion transform(
				    Class<? extends de.tud.stg.popart.dslsupport.DSL> input) {
				return extractClassInformation(input);
			    }
			});
	return exannos;
    }

    private Grammar createCombinedGrammar(
	    List<ExtractedClassInforamtion> exannos) {
	Grammar grammar = new Grammar();
	HandlingDispatcher typeHandler = new HandlingDispatcher(grammar);
	this.setupGeneralGrammar(grammar);

	boolean waterSupported = isWaterSupported(exannos);
	this.setWaterEnabled(waterSupported, grammar);

	for (ExtractedClassInforamtion classInfo : exannos) {
	    DSL classAnnotation = classInfo.classDslAnnotation;
	    Class<? extends HostLanguageGrammar>[] hostLanguageRules = classInfo.classDslAnnotation
		    .hostLanguageRules();
	    Map<ConfigurationOptions, String> classOptions = classInfo.classoptions;

	    if (classAnnotation != null) {
		// check for additionals rules

		// TODO check cyclic Dependency between Grammar and
		// HandlingDispatcher
		// xxx
		typeHandler.addAdditionalTypeRules(classInfo.classDslAnnotation
			.typeRules());
		this.setupHostLanguageRules(hostLanguageRules, grammar);
	    }

	    typeHandler.handleDefaults(classOptions);

	    for (ExtractedMethodsInformation methodInfo : classInfo.methodsInformation) {

		// PopartType p = methodInfo.popartType;
		Map<ConfigurationOptions, String> methodOptions = methodInfo.methodOptions;
		Method method = methodInfo.method;

		DSLMethod dslMethodAnnotation = methodInfo.dslMethodAnnotation;
		// Assuming default
		DslMethodType dslType;
		if (dslMethodAnnotation == null) {
		    dslType = DefaultedAnnotation.of(DSLMethod.class).type();
		} else {
		    dslType = dslMethodAnnotation.type();
		}

		switch (dslType) {
		case Literal:
		    this.handleLiteral(method, methodOptions, grammar,
			    typeHandler);
		    break;
		case Operation:
		    this.handleMethod(method, methodOptions, grammar,
			    typeHandler);
		    break;
		case AbstractionOperator:
		    throw new UnsupportedOperationException(
			    "Functionality not yet implemented for " + dslType);
		default:
		    throwIllegalFor(dslType);
		}
		// if (p != null) {
		// if (p.clazz() == PopartOperationKeyword.class) {
		//
		// } else if (p.clazz() == PopartLiteralKeyword.class) {
		//
		// }
		// }
	    }

	}
	return grammar;
    }

    /*
     * Water is supported as long as every involved DSL supports water.
     * Otherwise it is not supported
     */
    private boolean isWaterSupported(List<ExtractedClassInforamtion> exannos) {
	for (ExtractedClassInforamtion annos : exannos) {
	    if (!annos.classDslAnnotation.waterSupported()) {
		return false;
	    }
	}
	return true;
    }

    private void throwIllegalFor(Object dslType) {
	throw new IllegalArgumentException("Unknown or unhandled value "
		+ dslType.toString() + ".");
    }

    private ExtractedClassInforamtion extractClassInformation(Class<?> clazz) {
	ExtractedClassInforamtion classInfo = new ExtractedClassInforamtion(
		clazz);
	DSL classAnnotation = clazz.getAnnotation(DSL.class);
	if (classAnnotation == null) {
	    classInfo.classoptions = getDefaultOptions();
	} else {
	    classInfo.classoptions = getAnnotationParameterOptionsOverInitialMap(
		    classAnnotation, getDefaultOptions());
	    classInfo.classDslAnnotation = classAnnotation;
	}

	// get all methods, including inherited ones
	Set<Method> methods = extractAllRelevantMethods(clazz);

	final Map<ConfigurationOptions, String> classoptions = classInfo.classoptions;
	List<ExtractedMethodsInformation> methodInfos = ListMap.map(methods,
		new Transformer<Method, ExtractedMethodsInformation>() {

		    @Override
		    public ExtractedMethodsInformation transform(Method input) {
			return extractMethodInformation(input, classoptions);
		    }
		});
	classInfo.methodsInformation = methodInfos;
	return classInfo;
    }

    private Set<Method> extractAllRelevantMethods(Class<?> clazz) {
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

    private ExtractedMethodsInformation extractMethodInformation(Method method,
	    Map<ConfigurationOptions, String> defaultParameterOptions) {
	validateClassIsLoadedInSameClassloader(method);
	ExtractedMethodsInformation extMethAnnos = new ExtractedMethodsInformation(
		method);
	DSL methodDSLAnnotations = method.getAnnotation(DSL.class);
	Map<ConfigurationOptions, String> methodOptions;
	if (methodDSLAnnotations == null) {
	    methodOptions = defaultParameterOptions;
	} else {
	    // XXX(Leo Roos;Aug 17, 2011) this branch is currently not entered
	    // but might be useful to overwrite class-wide preferences.
	    methodOptions = getAnnotationParameterOptionsOverInitialMap(
		    methodDSLAnnotations, defaultParameterOptions);
	}
	extMethAnnos.methodOptions = methodOptions;
	// extMethAnnos.popartType = method.getAnnotation(PopartType.class);
	extMethAnnos.dslMethodAnnotation = method
		.getAnnotation(DSLMethod.class);
	return extMethAnnos;
    }

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

    private void setupHostLanguageRules(
	    Class<? extends HostLanguageGrammar>[] hostLanguageRules,
	    Grammar grammar) {
	for (Class<? extends HostLanguageGrammar> clazz : hostLanguageRules) {
	    try {
		HostLanguageGrammar newInstance = clazz.newInstance();
		newInstance.applySpecificGrammar(grammar);
	    } catch (InstantiationException e) {
		logger.warn("Generated log statement", e);
	    } catch (IllegalAccessException e) {
		logger.warn("Generated log statement", e);
	    }
	}
    }

    // FIXME(Leo Roos;Aug 25, 2011) belongs to extraction part
    public String getMethodProduction(AnnotatedElement element,
	    String defaultName) {
	DSLMethod dslAnnotation = element.getAnnotation(DSLMethod.class);

	String methodProduction = null;

	if (dslAnnotation != null) {
	    methodProduction = dslAnnotation.production();
	}

	return assignFirstStringOrDefault(methodProduction, defaultName);
    }

    /**
     * Extracts the options of the passed {@link DSL} annotation. The method
     * will create a copy of the passed map and overwrite existing values if
     * different values for the same keys are found in the DSL annotation.
     * 
     * @param dslAnnotation
     * @param initialMap
     *            the initialMap will be copied and values found in the
     *            annotation that differ from the defaults will overwrite the
     *            values of the passed map
     * @return
     */
    public Map<ConfigurationOptions, String> getAnnotationParameterOptionsOverInitialMap(
	    @Nullable DSL dslAnnotation,
	    Map<ConfigurationOptions, String> initialMap) {
	Map<ConfigurationOptions, String> resultMap = new HashMap<ConfigurationOptions, String>(
		initialMap);
	if (dslAnnotation != null) {
	    assignIfValid(resultMap, ConfigurationOptions.PARAMETER_ESCAPE,
		    dslAnnotation.parameterEscape());
	    assignIfValid(resultMap, ConfigurationOptions.WHITESPACE_ESCAPE,
		    dslAnnotation.whitespaceEscape());
	    assignIfValid(resultMap, ConfigurationOptions.ARRAY_DELIMITER,
		    dslAnnotation.arrayDelimiter());
	    assignIfValid(resultMap, ConfigurationOptions.STRING_QUOTATION,
		    dslAnnotation.stringQuotation());
	}
	return resultMap;
    }

    /*
     * assigns value with key to resultMap if the value is neither null nor
     * equals the UNASSIGNED constant
     */
    private Map<ConfigurationOptions, String> assignIfValid(
	    Map<ConfigurationOptions, String> resultMap,
	    ConfigurationOptions stringQuotation, String value) {
	Assert.isNotNull(value);
	if (value.equals(AnnotationConstants.UNASSIGNED))
	    return resultMap;
	else {
	    resultMap.put(stringQuotation, value);
	    return resultMap;
	}
    }

    private String assignFirstStringOrDefault(String stringToCheck,
	    String defaultString) {

	String string;
	if (stringToCheck == null
		|| stringToCheck.equals(AnnotationConstants.UNASSIGNED))
	    string = defaultString;
	else
	    string = stringToCheck;
	return string;
    }

    private final static Pattern literalPattern = Pattern.compile("^get(\\S+)");

    private boolean handleLiteral(Method method,
	    Map<ConfigurationOptions, String> methodOptions, Grammar grammar,
	    HandlingDispatcher typeHandler) {
	Class<?> returnType = method.getReturnType();

	if (returnType != void.class && returnType != Void.class) {
	    Matcher matcher = literalPattern.matcher(method.getName());

	    if (matcher.find()) {
		String literal = matcher.group(1);
		literal = literal.substring(0, 1).toLowerCase()
			+ literal.substring(1);

		ICategory<String> literalCategory = new Category(literal, true);
		grammar.addCategory(literalCategory);

		Rule literalRule = new Rule(this.statement, literalCategory);
		grammar.addRule(literalRule);

		ICategory<String> returnTypeCategory = typeHandler.handle(
			returnType, methodOptions);
		grammar.addCategory(returnTypeCategory);

		Rule returnTypeRule = new Rule(returnTypeCategory,
			literalCategory);
		grammar.addRule(returnTypeRule);

		this.methodAliases.put(
			literal,
			new MethodOptions(method.getName(),
				new LinkedList<Integer>(), method
					.getDeclaringClass()));

		return true;
	    }
	}

	return false;
    }

    public static class MethodOptions {

	private final List<Integer> parameterIndices;
	private final String methodCallName;
	private final Class<?> parentClass;

	public MethodOptions(String methodCallName,
		List<Integer> parameterIndices, Class<?> clazz) {
	    this.methodCallName = methodCallName;
	    this.parameterIndices = parameterIndices;
	    this.parentClass = clazz;
	}

	public List<Integer> getParamaterIndices() {
	    return this.parameterIndices;
	}

	public String getMethodCallName() {
	    return this.methodCallName;
	}

	public Class<?> getParentClass() {
	    return this.parentClass;
	}
    }

    // private void handleConstructor(Constructor<?> constructor, String
    // methodParameterEscape,
    // String methodWhitespaceEscape) {
    // String methodProduction = this.getMethodProduction(constructor,
    // constructor.getName());
    //
    // Pattern[] pattern = this.getPattern(methodParameterEscape,
    // methodWhitespaceEscape);
    //
    // this.methodAliases.put(methodProduction, new Pair<String,
    // Pattern[]>(constructor.getName(), pattern));
    //
    // Class<?>[] parameters = constructor.getParameterTypes();
    //
    // this.handleNonLiteral(methodProduction, constructor.getClass(),
    // parameters, pattern);
    // }

    private void handleMethod(Method method,
	    Map<ConfigurationOptions, String> methodOptions, Grammar grammar,
	    HandlingDispatcher typeHandler) {
	String methodProduction = getMethodProduction(method, method.getName());

	Pattern[] pattern = getPattern(
		methodOptions.get(ConfigurationOptions.PARAMETER_ESCAPE),
		methodOptions.get(ConfigurationOptions.WHITESPACE_ESCAPE));

	Type[] parameters = method.getGenericParameterTypes();

	this.handleNonLiteral(method, methodProduction,
		method.getGenericReturnType(), parameters, pattern,
		method.getParameterAnnotations(), methodOptions, grammar,
		typeHandler);
    }

    // TODO: check if caching would speed up this method
    public Pattern[] getPattern(String methodParameterEscape,
	    String methodWhitespaceEscape) {
	return new Pattern[] {
		// 0: methodproduction
		Pattern.compile("((?:\\Q" + methodWhitespaceEscape
			+ "\\E{1,2})|(?:\\Q" + methodParameterEscape
			+ "\\E\\d+)|(?:(?!(?:\\Q" + methodParameterEscape
			+ "\\E\\d+|\\Q" + methodWhitespaceEscape + "\\E)).)+)"),

		Pattern.compile("\\Q" + methodParameterEscape + "\\E(\\d+)"),
		Pattern.compile("\\Q" + methodWhitespaceEscape + "\\E{1,2}") };
    }

    private void handleNonLiteral(Method method, String methodProduction,
	    Type returnType, Type[] parameters, Pattern[] pattern,
	    Annotation[][] parameterAnnotations,
	    Map<ConfigurationOptions, String> methodOptions, Grammar grammar,
	    HandlingDispatcher typeHandler) {
	Matcher methodProductionmatcher = pattern[0].matcher(methodProduction);

	StringBuilder sb = new StringBuilder();

	if (!methodProductionmatcher.find()) {
	    return;
	}

	int index = 0;
	List<Integer> parameterIndices = new ArrayList<Integer>(
		method.getParameterTypes().length);

	LinkedList<ICategory<String>> categories = new LinkedList<ICategory<String>>();
	do {
	    String keyword = methodProductionmatcher.group(1);

	    Matcher parameterMatcher = pattern[1].matcher(keyword);
	    boolean isParameter = parameterMatcher.find();

	    if (!isParameter) {
		Matcher whitespaceMatcher = pattern[2].matcher(keyword);
		boolean isWhitespace = whitespaceMatcher.find();

		if (isWhitespace) {
		    if (keyword.length() == 1) {
			categories.add(WhitespaceCategoryDefinition
				.getAndSetOptionalWhitespace(grammar));
		    } else {
			categories.add(WhitespaceCategoryDefinition
				.getAndSetRequiredWhitespace(grammar));
		    }
		} else {

		    String uniChar = unicodeLookupTable.nameToUnicode(keyword);

		    if (uniChar == null) {
			logger.trace(
				"No unicode representation for [{}] found. Assuming this is a literal keyword.",
				keyword);
			uniChar = keyword;
		    } else {
			logger.trace(
				"found unicode representation [{}] for [{}]",
				uniChar, keyword);
		    }

		    sb.append(uniChar);

		    categories.add(new Category(uniChar, true));

		    this.keywords.add(uniChar);
		}
	    } else {
		int parameterIndex = Integer
			.parseInt(parameterMatcher.group(1));
		parameterIndices.add(index);
		Type parameterType = null;
		Annotation[] pAnnotations = null;

		try {
		    parameterType = parameters[parameterIndex];
		    pAnnotations = parameterAnnotations[parameterIndex];
		} catch (IndexOutOfBoundsException e) {
		    throw new IndexOutOfBoundsException("Grammar for method \""
			    + methodProduction
			    + "\" could not be built. Parameter reference $p"
			    + parameterIndex + " can not be resolved.");
		}

		String param = "P" + parameterIndex + "{"
			+ this.parameterCounter.getAndIncrement() + "}";
		ICategory<String> parameterCategory = new Category(param, false);
		categories.add(parameterCategory);

		DSL parameterDSLAnnotation = null;
		for (Annotation a : pAnnotations) {
		    if (a instanceof DSL) {
			parameterDSLAnnotation = (DSL) a;
			break;
		    }
		}

		Map<ConfigurationOptions, String> parameterOptions = getAnnotationParameterOptionsOverInitialMap(
			parameterDSLAnnotation, methodOptions);

		ICategory<String> parameterMapping = typeHandler.handle(
			parameterType, parameterOptions);
		Rule rule = new Rule(parameterCategory, parameterMapping);

		ICategory<String> typeCategory = new Category(
			CategoryNames.PTYPE_CATEGORY, false);
		Rule typeRule = new Rule(parameterMapping, typeCategory);

		grammar.addRule(rule);
		grammar.addRule(typeRule);
		grammar.addCategory(parameterMapping);

		// sb.append(parameterMapping.toString().toUpperCase());
		sb.append(param);
	    }

	    sb.append('_');
	    index++;
	} while ((methodProductionmatcher.find()));

	sb.deleteCharAt(sb.length() - 1);

	int methodCounter = this.parameterCounter.getAndIncrement();

	String indexedMethodProduction = "M" + methodCounter + "("
		+ methodProduction + ")";
	MethodOptions value = new MethodOptions(method.getName(),
		parameterIndices, method.getDeclaringClass());
	this.methodAliases.put(indexedMethodProduction, value);

	ICategory<String> methodCategory = new Category(
		indexedMethodProduction, false);
	grammar.addCategory(methodCategory);

	DSLMethod annotation = method.getAnnotation(DSLMethod.class);
	boolean isPublic = Modifier.isPublic(method.getModifiers());

	boolean isTopLevel = isTopLevel(annotation);
	if (isPublic && isTopLevel) {
	    Rule methodRule = new Rule(this.statement, methodCategory);
	    grammar.addRule(methodRule);
	}

	Rule rule = new Rule(methodCategory, categories);
	grammar.addRule(rule);

	for (ICategory<String> c : categories) {
	    grammar.addCategory(c);
	}

	if (returnType != void.class && returnType != Void.class) {
	    ICategory<String> returnTypeCategory = typeHandler.handle(
		    returnType, methodOptions);
	    grammar.addCategory(returnTypeCategory);

	    ICategory<String> typeCategory = new Category(
		    CategoryNames.RTYPE_CATEGORY, false);
	    Rule typeToMethod = new Rule(typeCategory, methodCategory);
	    grammar.addRule(typeToMethod);

	    Rule returnTypeToMethod = new Rule(returnTypeCategory,
		    methodCategory);
	    grammar.addRule(returnTypeToMethod);
	}
    }

    private boolean isTopLevel(@Nullable DSLMethod annotation) {
	if (annotation == null)
	    return DefaultedAnnotation.of(DSLMethod.class).topLevel();
	else
	    return annotation.topLevel();
    }

    public Map<String, MethodOptions> getMethodOptions() {
	return Collections.unmodifiableMap(this.methodAliases);
    }

    // (Leo Roos; Jun 27, 2011): never used
    /*
     * public Set<String> getKeywords() { return this.keywords; }
     */

    private Map<ConfigurationOptions, String> getDefaultOptions() {
	Map<ConfigurationOptions, String> defaultOptions = new HashMap<ConfigurationOptions, String>();
	for (ConfigurationOptions parop : ConfigurationOptions.values()) {
	    defaultOptions.put(parop, parop.defaultValue);
	}
	return defaultOptions;
    }

    // public static void main(String[] args) {
    //
    // logger.info(GrammarBuilder.getOptions(StateMachineDSL.class.getAnnotation(DSL.class),
    // GrammarBuilder
    // .getDefaultOptions()));
    // }
}
