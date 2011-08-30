package de.tud.stg.tigerseye.eclipse.core.codegeneration;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.ExtractedClassInforamtion;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.ExtractedMethodInformation;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.ExtractorDefaults;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars.CategoryNames;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars.HostLanguageGrammar;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ConfigurationOptions;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.TypeHandler;
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
		    .getDSLClassChecked();
	    clazzes.add(loadClass);
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
	TypeHandlerDispatcher typeHandler = new TypeHandlerDispatcher(grammar);
	this.setupGeneralGrammar(grammar);

	boolean waterSupported = isWaterSupported(exannos);
	this.setWaterEnabled(waterSupported, grammar);

	for (ExtractedClassInforamtion classInfo : exannos) {
	    Set<Class<? extends HostLanguageGrammar>> hostLanguageRules = classInfo
		    .getHostLanguageRules();
	    Set<Class<? extends TypeHandler>> typeRules = classInfo
		    .getTypeRules();
	    Map<ConfigurationOptions, String> classOptions = classInfo
		    .getConfigurationOptions();

	    typeHandler.addAdditionalTypeRules(typeRules);
	    this.setupHostLanguageRules(hostLanguageRules, grammar);

	    // XXX(Leo_Roos;Aug 28, 2011) can be deleted now?
	    typeHandler.handleDefaults(classOptions);

	    for (ExtractedMethodInformation methodInfo : classInfo
		    .getMethodsInformation()) {

		switch (methodInfo.getDSLType()) {
		case Literal:
		    this.handleLiteral(methodInfo, grammar,
			    typeHandler);
		    break;
		case Operation:
		    this.handleMethod(methodInfo, grammar,
			    typeHandler);
		    break;
		case AbstractionOperator:
		    throw new UnsupportedOperationException(
			    "Functionality not yet implemented for "
				    + methodInfo.getDSLType());
		default:
		    throwIllegalFor(methodInfo.getDSLType());
		}
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
	    if (!annos.isWaterSupported()) {
		return false;
	    }
	}
	return true;
    }

    private void throwIllegalFor(Object dslType) {
	throw new IllegalArgumentException("Unknown or unhandled value ["
		+ dslType.toString() + "].");
    }

    private ExtractedClassInforamtion extractClassInformation(Class<?> clazz) {
	ExtractedClassInforamtion classInfo = new ExtractedClassInforamtion(
		clazz);
	classInfo.load(ExtractorDefaults.DEFAULT_CONFIGURATIONOPTIONS_MAP);
	return classInfo;
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
	    Set<Class<? extends HostLanguageGrammar>> hostLanguageRules,
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

    private boolean handleLiteral(ExtractedMethodInformation extractedMethod,
	    Grammar grammar,
	    TypeHandlerDispatcher typeHandler) {

	Method method = extractedMethod.getMethod();
	Map<ConfigurationOptions, String> methodOptions = extractedMethod
		.getConfigurationOptions();

	Class<?> returnType = method.getReturnType();

	// Support for not annotated method
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

    private void handleMethod(ExtractedMethodInformation method,
	    Grammar grammar,
	    TypeHandlerDispatcher typeHandler) {
	this.handleNonLiteral(method, grammar,
		typeHandler);
    }

    // TODO: check if caching would speed up this method
    public Pattern[] getPattern(String methodParameterEscape,
	    String methodWhitespaceEscape) {
	return new Pattern[] {
		// methodproduction
		getMethodProductionPattern(methodParameterEscape,
			methodWhitespaceEscape),
		// match methodParameterEscape literally followed by a digit
		getProductionParameterPattern(methodParameterEscape),
		// match methodWhitespaceEscape exactly one or exactly two times
		getProductionWhitespacePattern(methodWhitespaceEscape) };
    }

    private Pattern getProductionWhitespacePattern(String methodWhitespaceEscape) {
	return Pattern.compile("\\Q" + methodWhitespaceEscape + "\\E{1,2}");
    }

    private Pattern getProductionParameterPattern(String methodParameterEscape) {
	return Pattern.compile("\\Q" + methodParameterEscape + "\\E(\\d+)");
    }

    private Pattern getMethodProductionPattern(String methodParameterEscape,
	    String methodWhitespaceEscape) {
	return Pattern.compile("((?:\\Q" + methodWhitespaceEscape
		+ "\\E{1,2})|(?:\\Q" + methodParameterEscape
		+ "\\E\\d+)|(?:(?!(?:\\Q" + methodParameterEscape
		+ "\\E\\d+|\\Q" + methodWhitespaceEscape + "\\E)).)+)");
    }

    private void handleNonLiteral(ExtractedMethodInformation methodInfo,
	    Grammar grammar,
	    TypeHandlerDispatcher typeHandler) {

	Method method = methodInfo.getMethod();
	Type returnType = method.getGenericReturnType();
	Type[] parameters = method.getGenericParameterTypes();
	Annotation[][] parameterAnnotations = method.getParameterAnnotations();
	Class<?>[] parameterTypes = method.getParameterTypes();
	String methodProduction = getMethodProduction(method, method.getName());
	Map<ConfigurationOptions, String> methodOptions = methodInfo
		.getConfigurationOptions();

	String parameterEscape = methodOptions
		.get(ConfigurationOptions.PARAMETER_ESCAPE);
	String whitespaceEscape = methodOptions
		.get(ConfigurationOptions.WHITESPACE_ESCAPE);

	Matcher methodProductionmatcher = getMethodProductionPattern(
		parameterEscape, whitespaceEscape).matcher(methodProduction);

	StringBuilder sb = new StringBuilder();

	if (!methodProductionmatcher.find()) {
	    return;
	}

	int index = 0;
	List<Integer> parameterIndices = new ArrayList<Integer>(
		parameterTypes.length);

	LinkedList<ICategory<String>> categories = new LinkedList<ICategory<String>>();
	do {
	    String keyword = methodProductionmatcher.group(1);

	    Matcher parameterMatcher = getProductionParameterPattern(
		    parameterEscape).matcher(keyword);
	    boolean isParameter = parameterMatcher.find();

	    if (!isParameter) {
		Matcher whitespaceMatcher = getProductionWhitespacePattern(
			whitespaceEscape).matcher(keyword);
		boolean isWhitespace = whitespaceMatcher.find();

		if (isWhitespace) {
		    if (keyword.length() == 1) {
			categories.add(WhitespaceCategoryDefinition
				.getAndSetRequiredWhitespace(grammar));
		    } else {
			categories.add(WhitespaceCategoryDefinition
				.getAndSetOptionalWhitespace(grammar));
		    }
		} else {
		    // neither parameter nor whitespace
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

	boolean isPublic = Modifier.isPublic(method.getModifiers());
	boolean toplevel = methodInfo.isToplevel();
	if (isPublic && toplevel) {
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

    // (Leo Roos; Jun 27, 2011): never used
    /*
     * public Set<String> getKeywords() { return this.keywords; }
     */

    public Map<String, MethodOptions> getMethodOptions() {
	return Collections.unmodifiableMap(methodAliases);
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
