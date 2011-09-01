package de.tud.stg.tigerseye.eclipse.core.codegeneration;

import static de.tud.stg.tigerseye.util.Utils.throwIllegalFor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

import javax.annotation.CheckForNull;
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
import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.ClassDSLInformation;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.DSLAnnotationDefaults;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.MethodDSLInformation;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.MethodProductionConstants.ProductionElement;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.MethodProductionElement;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.MethodProductionScanner;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.ParameterElement;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.WhitespaceElement;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars.CategoryNames;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars.HostLanguageGrammar;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ConfigurationOptions;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.utils.WhitespaceCategoryDefinition;
import de.tud.stg.tigerseye.util.ListBuilder;

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

	List<ClassDSLInformation> exInfos = extractClassesInformation(clazzes);

	Grammar grammar = createCombinedGrammar(exInfos);

	return grammar;
    }

    private List<ClassDSLInformation> extractClassesInformation(
	    List<Class<? extends de.tud.stg.popart.dslsupport.DSL>> clazzes) {
	ArrayList<ClassDSLInformation> result = new ArrayList<ClassDSLInformation>(
		clazzes.size());
	for (Class<? extends de.tud.stg.popart.dslsupport.DSL> aClass : clazzes) {
	    result.add(loadClassInformation(aClass));
	}
	return result;
    }

    private ClassDSLInformation loadClassInformation(
	    Class<? extends de.tud.stg.popart.dslsupport.DSL> aClass) {
	ClassDSLInformation classInfo = new ClassDSLInformation(aClass);
	classInfo.load(DSLAnnotationDefaults.DEFAULT_CONFIGURATIONOPTIONS_MAP);
	return classInfo;
    }

    private Grammar createCombinedGrammar(List<ClassDSLInformation> exannos) {
	Grammar grammar = new Grammar();
	this.setupGeneralGrammar(grammar);

	boolean waterSupported = isWaterSupported(exannos);
	this.setWaterEnabled(waterSupported, grammar);

	TypeHandlerDispatcher typeHandler = new TypeHandlerDispatcher(grammar);
	for (ClassDSLInformation classInfo : exannos) {
	    typeHandler.addAdditionalTypeRules(classInfo.getTypeRules());
	}
	for (ClassDSLInformation classInfo : exannos) {
	    this.setupHostLanguageRules(classInfo.getHostLanguageRules(),
		    grammar);
	}

	for (ClassDSLInformation classInfo : exannos) {
	    // possibly additional type rules for each class
	    // Current host language support
	    // The configuration options for this class
	    typeHandler.configurationOptions(classInfo
		    .getConfigurationOptions());
	    // Process Methods
	    for (MethodDSLInformation methodInfo : classInfo
		    .getMethodsInformation()) {
		DslMethodType dslType = methodInfo.getDSLType();
		switch (dslType) {
		case Literal:
		    this.handleLiteral(methodInfo, grammar, typeHandler);
		    break;
		case Operation:
		    this.handleMethod(methodInfo, grammar, typeHandler);
		    break;
		case AbstractionOperator:
		    throw new UnsupportedOperationException(
			    "Functionality not yet implemented for " + dslType);
		default:
		    throwIllegalFor(dslType);
		}
	    }

	}
	return grammar;
    }

    /*
     * Water is supported as long as every involved DSL supports water.
     * Otherwise it is not supported
     */
    private boolean isWaterSupported(List<ClassDSLInformation> exannos) {
	for (ClassDSLInformation annos : exannos) {
	    if (!annos.isWaterSupported()) {
		return false;
	    }
	}
	return true;
    }

    private void setupHostLanguageRules(
	    Set<Class<? extends HostLanguageGrammar>> hostLanguageRules,
	    Grammar grammar) {
	for (Class<? extends HostLanguageGrammar> clazz : hostLanguageRules) {
	    setupHostLanguage(grammar, clazz);
	}
    }

    private void setupHostLanguage(Grammar grammar,
	    Class<? extends HostLanguageGrammar> clazz) {
	Constructor<? extends HostLanguageGrammar> constructor = getNullaryConstructor(clazz);
	if (constructor == null) {
	    logger.error("Ignoring host language grammar for {}", clazz);
	    return;
	}
	try {
	    HostLanguageGrammar newInstance = constructor.newInstance();
	    newInstance.applySpecificGrammar(grammar);
	} catch (IllegalArgumentException e) {
	    logger.error(
		    "Unexpected Problem, thought I have loaded nullary constructor",
		    e);
	} catch (InvocationTargetException e) {
	    logger.error("Underlying constructor threw exception", e);
	} catch (InstantiationException e) {
	    logger.error("Class can not be instantiated", e);
	} catch (IllegalAccessException e) {
	    logger.error("Access to class has been denied", e);
	}
    }

    private @CheckForNull
    Constructor<? extends HostLanguageGrammar> getNullaryConstructor(
	    Class<? extends HostLanguageGrammar> clazz) {
	try {
	    Constructor<? extends HostLanguageGrammar> constructor = clazz
		    .getConstructor();
	    return constructor;
	} catch (SecurityException e) {
	    logger.error("Unexpected Problem. Will not load {}", clazz, e);
	} catch (NoSuchMethodException e) {
	    logger.warn(
		    "Hostlanguage class has no nullary constructor {}. Can not load it",
		    clazz, e);
	}
	return null;
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

    private boolean handleLiteral(MethodDSLInformation extractedMethod,
	    Grammar grammar, TypeHandlerDispatcher typeHandler) {

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

    private void handleMethod(MethodDSLInformation method, Grammar grammar,
	    TypeHandlerDispatcher typeHandler) {
	// XXX(Leo_Roos;Aug 31, 2011) This method could be inlined now
	this.handleNonLiteral(method, grammar, typeHandler);
    }

    private void handleNonLiteral(MethodDSLInformation methodInfo,
	    Grammar grammar, TypeHandlerDispatcher typeHandler) {

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

	MethodProductionScanner mps = new MethodProductionScanner();
	mps.setParameterEscape(parameterEscape);
	mps.setWhitespaceEscape(whitespaceEscape);

	mps.startScan(methodProduction);

	if (!mps.hasNext())
	    return;

	int index = 0;
	List<Integer> parameterIndices = new ArrayList<Integer>(
		parameterTypes.length);

	LinkedList<ICategory<String>> categories = new LinkedList<ICategory<String>>();
	while (mps.hasNext()) {
	    MethodProductionElement next = mps.next();
	    ProductionElement productionElementType = next
		    .getProductionElementType();
	    switch (productionElementType) {
	    case Keyword:
		handleKeyword(categories, next.getCapturedString());
		break;
	    case Parameter:
		ParameterElement pe = (ParameterElement) next;
		handleParameter(grammar, typeHandler, parameters,
			parameterAnnotations, methodProduction, methodOptions,
			index, parameterIndices, categories,
			pe.getParsedParameterNumber());
		break;
	    case Whitespace:
		WhitespaceElement we = (WhitespaceElement) next;
		if (we.isOptional()) {
		    categories.add(WhitespaceCategoryDefinition
			    .getAndSetOptionalWhitespace(grammar));
		} else {
		    categories.add(WhitespaceCategoryDefinition
			    .getAndSetRequiredWhitespace(grammar));
		}
		break;
	    default:
		throwIllegalFor(productionElementType);
	    }
	    index++;
	}


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


    private void handleKeyword(
	    LinkedList<ICategory<String>> categories, String keyword) {
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

	categories.add(new Category(uniChar, true));

	this.keywords.add(uniChar);
    }

    private void handleParameter(Grammar grammar,
	    TypeHandlerDispatcher typeHandler, Type[] parameters,
	    Annotation[][] parameterAnnotations, String methodProduction,
	    Map<ConfigurationOptions, String> methodOptions,
	    int index, List<Integer> parameterIndices,
	    LinkedList<ICategory<String>> categories, int parameterIndex) {
	// int parameterIndex = Integer
	// .parseInt(parameterMatcher.group(1));
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
	// sb.append(param);
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
