package de.tud.stg.tigerseye.eclipse.core.codegeneration;

import static de.tud.stg.tigerseye.util.Utils.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.CheckForNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.parlex.core.Category;
import de.tud.stg.parlex.core.Grammar;
import de.tud.stg.parlex.core.ICategory;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.core.Rule;
import de.tud.stg.parlex.core.groupcategories.WaterCategory;
import de.tud.stg.popart.builder.core.annotations.DSLMethod.DslMethodType;
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.ClassDSLInformation;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.DSLInformationDefaults;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.MethodDSLInformation;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.MethodProductionConstants.ProductionElement;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.MethodProductionElement;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.MethodProductionScanner;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.ParameterDSLInformation;
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

    private static final Logger logger = LoggerFactory.getLogger(GrammarBuilder.class);

    private final AtomicInteger parameterCounter = new AtomicInteger();

    /*
     * Only used as left hand side rule or grammar category
     */
    private ICategory<String> statement;
    private ICategory<String> statements;

    private final UnicodeLookupTable unicodeLookupTable;

    public final HashMap<String, MethodOptions> methodAliases = new HashMap<String, MethodOptions>();

    public GrammarBuilder(UnicodeLookupTable ult) {
	this.unicodeLookupTable = ult;
    }

    private void setWaterEnabled(boolean enabled, Grammar grammar) {
	ICategory<String> anything = new WaterCategory();
	grammar.addCategory(anything);

	Rule rAnyStatement = new Rule(this.statement, anything);
	grammar.addRule(rAnyStatement);

	grammar.addWaterRule(rAnyStatement);
    }

    private void setupGeneralGrammar(Grammar grammar) {
	Category program = new Category(CategoryNames.PROGRAM_CATEGORY, false);

	this.statement = new Category(CategoryNames.STATEMENT_CATEGORY, false);
	this.statements = new Category(CategoryNames.STATEMENTS_CATEGORY, false);

	List<ICategory<String>> single = ListBuilder.single(this.statements);
	Rule startRule = new Rule(program, single);

	Rule rStatements = new Rule(this.statements, ListBuilder.newList(this.statement)
		.add(WhitespaceCategoryDefinition.getAndSetOptionalWhitespace(grammar)).add(this.statements).toList());

	Rule rStatement = new Rule(this.statements, ListBuilder.single(this.statement));

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
	    Class<? extends de.tud.stg.popart.dslsupport.DSL> loadClass = dsl.getDSLClassChecked();
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
    public IGrammar<String> buildGrammar(Class<? extends de.tud.stg.popart.dslsupport.DSL>... clazzes) {
	List<Class<? extends de.tud.stg.popart.dslsupport.DSL>> clazzess = Arrays.asList(clazzes);
	return buildGrammar(clazzess);
    }

    public IGrammar<String> buildGrammar(List<Class<? extends de.tud.stg.popart.dslsupport.DSL>> clazzes) {

	List<ClassDSLInformation> exInfos = extractClassesInformation(clazzes);

	Grammar grammar = createCombinedGrammar(exInfos);

	return grammar;
    }

    private List<ClassDSLInformation> extractClassesInformation(
	    List<Class<? extends de.tud.stg.popart.dslsupport.DSL>> clazzes) {
	ArrayList<ClassDSLInformation> result = new ArrayList<ClassDSLInformation>(clazzes.size());
	for (Class<? extends de.tud.stg.popart.dslsupport.DSL> aClass : clazzes) {
	    result.add(loadClassInformation(aClass));
	}
	return result;
    }

    private ClassDSLInformation loadClassInformation(Class<? extends de.tud.stg.popart.dslsupport.DSL> aClass) {
	ClassDSLInformation classInfo = new ClassDSLInformation(aClass);
	classInfo.load(DSLInformationDefaults.DEFAULT_CONFIGURATIONOPTIONS_MAP);
	return classInfo;
    }

    private Grammar createCombinedGrammar(List<ClassDSLInformation> exannos) {
	Grammar grammar = new Grammar();
	this.setupGeneralGrammar(grammar);

	boolean waterSupported = isWaterSupported(exannos);
	if (waterSupported) {
	    this.setWaterEnabled(waterSupported, grammar);
	}

	TypeHandlerDispatcher typeHandler = new TypeHandlerDispatcher(grammar);
	for (ClassDSLInformation classInfo : exannos) {
	    typeHandler.addAdditionalTypeRules(classInfo.getTypeRules());
	}

	for (ClassDSLInformation classInfo : exannos) {
	    this.setupHostLanguageRules(classInfo.getHostLanguageRules(), grammar);
	}
	for (ClassDSLInformation classDSLInformation : exannos) {
	    typeHandler.configurationOptions(classDSLInformation.getConfigurationOptions());
	}

	for (ClassDSLInformation classInfo : exannos) {

	    List<MethodDSLInformation> validMinfs = new ArrayList<MethodDSLInformation>();
	    List<MethodDSLInformation> invalidMinfs = new ArrayList<MethodDSLInformation>();
	    for (MethodDSLInformation minf : classInfo.getMethodsInformation()) {
		// TODO(Leo_Roos;Sep 1, 2011) should be a functionality of
		// ClassDSLInformation to provide only the valid ones if queried
		if (minf.isValid())
		    validMinfs.add(minf);
		else
		    invalidMinfs.add(minf);
	    }

	    if (invalidMinfs.size() > 0) {
		logger.info("Ignoring following invalid method configurations: {}", invalidMinfs);
	    }

	    for (MethodDSLInformation methodInfo : validMinfs) {
		DslMethodType dslType = methodInfo.getDSLType();
		switch (dslType) {
		case Literal:
		    this.handleLiteral(methodInfo, grammar, typeHandler);
		    break;
		case Operation:
		    this.handleMethod(methodInfo, grammar, typeHandler);
		    break;
		case AbstractionOperator:
		    throw new UnsupportedOperationException("Functionality not yet implemented for " + dslType);
		default:
		    throw illegalForArg(dslType);
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

    private void setupHostLanguageRules(Set<Class<? extends HostLanguageGrammar>> hostLanguageRules, Grammar grammar) {
	for (Class<? extends HostLanguageGrammar> clazz : hostLanguageRules) {
	    setupHostLanguage(grammar, clazz);
	}
    }

    private void setupHostLanguage(Grammar grammar, Class<? extends HostLanguageGrammar> clazz) {
	Constructor<? extends HostLanguageGrammar> constructor = getNullaryConstructor(clazz);
	if (constructor == null) {
	    logger.error("Ignoring host language grammar for {}", clazz);
	    return;
	}
	try {
	    HostLanguageGrammar newInstance = constructor.newInstance();
	    newInstance.applySpecificGrammar(grammar);
	} catch (IllegalArgumentException e) {
	    logger.error("Unexpected Problem, thought I have loaded nullary constructor", e);
	} catch (InvocationTargetException e) {
	    logger.error("Underlying constructor threw exception", e);
	} catch (InstantiationException e) {
	    logger.error("Class can not be instantiated", e);
	} catch (IllegalAccessException e) {
	    logger.error("Access to class has been denied", e);
	}
    }

    private @CheckForNull
    Constructor<? extends HostLanguageGrammar> getNullaryConstructor(Class<? extends HostLanguageGrammar> clazz) {
	try {
	    Constructor<? extends HostLanguageGrammar> constructor = clazz.getConstructor();
	    return constructor;
	} catch (SecurityException e) {
	    logger.error("Unexpected Problem. Will not load {}", clazz, e);
	} catch (NoSuchMethodException e) {
	    logger.warn("Hostlanguage class has no nullary constructor {}. Can not load it", clazz, e);
	}
	return null;
    }

    private static class GrammarBox {

	List<ICategory<String>> category = new LinkedList<ICategory<String>>();
	List<Rule> rules = new LinkedList<Rule>();

    }

    private void handleLiteral(MethodDSLInformation extractedMethod, Grammar grammar, TypeHandlerDispatcher typeHandler) {

	Method method = extractedMethod.getMethod();
	Map<ConfigurationOptions, String> methodOptions = extractedMethod.getConfigurationOptions();

	String literal = extractedMethod.getProduction();

	ICategory<String> literalCategory = new Category(literal, true);
	grammar.addCategory(literalCategory);

	Rule literalRule = new Rule(this.statement, literalCategory);
	grammar.addRule(literalRule);

	Class<?> returnType = method.getReturnType();
	ICategory<String> returnTypeCategory = typeHandler.handle(returnType, methodOptions);
	grammar.addCategory(returnTypeCategory);

	Rule returnTypeRule = new Rule(returnTypeCategory, literalCategory);
	grammar.addRule(returnTypeRule);

	this.methodAliases.put(literal,
		new MethodOptions(method.getName(), new LinkedList<Integer>(), method.getDeclaringClass()));
    }

    public static class MethodOptions {

	private final List<Integer> parameterIndices;
	private final String methodCallName;
	private final Class<?> parentClass;

	public MethodOptions(String methodCallName, List<Integer> parameterIndices, Class<?> clazz) {
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

    private void handleMethod(MethodDSLInformation method, Grammar grammar, TypeHandlerDispatcher typeHandler) {
	// XXX(Leo_Roos;Aug 31, 2011) This method could be inlined now
	this.handleNonLiteral(method, grammar, typeHandler);
    }

    private void handleNonLiteral(MethodDSLInformation methodInfo, Grammar grammar, TypeHandlerDispatcher typeHandler) {

	Method method = methodInfo.getMethod();

	String methodProduction = methodInfo.getProduction();// getMethodProduction(method,
							     // method.getName());

	Map<ConfigurationOptions, String> methodOptions = methodInfo.getConfigurationOptions();

	String parameterEscape = methodOptions.get(ConfigurationOptions.PARAMETER_ESCAPE);
	String whitespaceEscape = methodOptions.get(ConfigurationOptions.WHITESPACE_ESCAPE);

	MethodProductionScanner mps = new MethodProductionScanner();
	mps.setParameterEscape(parameterEscape);
	mps.setWhitespaceEscape(whitespaceEscape);

	mps.startScan(methodProduction);

	if (!mps.hasNext())
	    return;

	int index = 0;
	List<Integer> parameterIndices = new ArrayList<Integer>();

	LinkedList<ICategory<String>> categories = new LinkedList<ICategory<String>>();
	while (mps.hasNext()) {
	    MethodProductionElement next = mps.next();
	    ProductionElement productionElementType = next.getProductionElementType();
	    switch (productionElementType) {
	    case Keyword:
		handleProductionElementKeyword(categories, next);
		break;
	    case Parameter:
		ParameterElement pe = (ParameterElement) next;
		ParameterDSLInformation parameterInfo = methodInfo.getParameterInfo(pe.getParsedParameterNumber());
		if (parameterInfo == null) {
		    logger.error("Could not determine parameter of index {}. Will ignore it and build grammar without it"
			    + index);
		    // TODO(Leo_Roos;Sep 1, 2011) perhaps better throw
		    // exception?
		    break;
		}
		handleProductionElementParameter(grammar, typeHandler, categories, parameterInfo);
		parameterIndices.add(index);
		break;
	    case Whitespace:
		handleProductionElementWhitespace(grammar, categories, (WhitespaceElement) next);
		break;
	    default:
		throwIllegalArgFor(productionElementType);
	    }
	    index++;
	}

	int methodCounter = this.parameterCounter.getAndIncrement();

	String indexedMethodProduction = "M" + methodCounter + "(" + methodProduction + ")";
	MethodOptions value = new MethodOptions(method.getName(), parameterIndices, method.getDeclaringClass());
	this.methodAliases.put(indexedMethodProduction, value);

	ICategory<String> methodCategory = new Category(indexedMethodProduction, false);
	grammar.addCategory(methodCategory);

	boolean toplevel = methodInfo.isToplevel();
	if (toplevel) {
	    Rule methodRule = new Rule(this.statement, methodCategory);
	    grammar.addRule(methodRule);
	}

	Rule rule = new Rule(methodCategory, categories);
	grammar.addRule(rule);

	for (ICategory<String> c : categories) {
	    grammar.addCategory(c);
	}

	if (methodInfo.hasReturnValue()) {
	    ICategory<String> returnTypeCategory = typeHandler.handle(method.getReturnType(), methodOptions);
	    grammar.addCategory(returnTypeCategory);

	    ICategory<String> typeCategory = new Category(CategoryNames.RETURNTYPE_CATEGORY, false);
	    Rule typeToMethod = new Rule(typeCategory, methodCategory);
	    grammar.addRule(typeToMethod);

	    Rule returnTypeToMethod = new Rule(returnTypeCategory, methodCategory);
	    grammar.addRule(returnTypeToMethod);
	}

    }

    private void handleProductionElementKeyword(LinkedList<ICategory<String>> categories, MethodProductionElement next) {
	String keyword = next.getCapturedString();
	keyword = getUnicodeRepresentationOrKeyword(keyword);
	categories.add(new Category(keyword, true));
	// this.keywords.add(keyword);
    }

    private void handleProductionElementWhitespace(Grammar grammar, LinkedList<ICategory<String>> categories,
	    WhitespaceElement we) {
	if (we.isOptional()) {
	    categories.add(WhitespaceCategoryDefinition.getAndSetOptionalWhitespace(grammar));
	} else {
	    categories.add(WhitespaceCategoryDefinition.getAndSetRequiredWhitespace(grammar));
	}
    }

    private boolean isReturnTypeNotVoid(Class<?> returnType) {
	return returnType != void.class && returnType != Void.class;
    }

    private String getUnicodeRepresentationOrKeyword(String keyword) {
	String uniChar = unicodeLookupTable.nameToUnicode(keyword);
	if (uniChar == null) {
	    return keyword;
	} else {
	    logger.trace("found unicode representation [{}] for [{}]", uniChar, keyword);
	    return uniChar;
	}
    }

    private void handleProductionElementParameter(Grammar grammar, TypeHandlerDispatcher typeHandler,
	    LinkedList<ICategory<String>> categories, ParameterDSLInformation parameterInfo) {

	String param = "P" + parameterInfo.getIndex() + "{" + this.parameterCounter.getAndIncrement() + "}";
	ICategory<String> parameterCategory = new Category(param, false);
	categories.add(parameterCategory);

	Map<ConfigurationOptions, String> parameterOptions = parameterInfo.getConfigurationOptions();
	ICategory<String> parameterMapping = typeHandler.handle(parameterInfo.getType(), parameterOptions);
	Rule rule = new Rule(parameterCategory, parameterMapping);

	ICategory<String> typeCategory = new Category(CategoryNames.PARAMETERTYPE_CATEGORY, false);
	Rule typeRule = new Rule(parameterMapping, typeCategory);

	grammar.addRule(rule);
	grammar.addRule(typeRule);
	grammar.addCategory(parameterMapping);

    }

    public Map<String, MethodOptions> getMethodOptions() {
	return Collections.unmodifiableMap(methodAliases);
    }

//@formatter:off
// TODO(Leo_Roos;Sep 1, 2011) delete when sure that no lost treasure is buried somewhere donw there ...
// ======================================================================================================
// 0000000000000000234523453205432452845927869026405927z----Yee Old Gravyard
// ---- Abandon all Hope
/*
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

    // private static final String DEFAULT_STRING_QUOTATION =
    // "([\\w_]+|(\".*?\"))";
     
//From handleProductionElementParameter
 	/*
	 * categories.add(parameterCategory);
	 * 
	 * DSL parameterDSLAnnotation = null; for (Annotation a : pAnnotations)
	 * { if (a instanceof DSL) { parameterDSLAnnotation = (DSL) a; break; }
	 * }
	 * 
	 * Map<ConfigurationOptions, String> parameterOptions =
	 * getAnnotationParameterOptionsOverInitialMap( parameterDSLAnnotation,
	 * methodOptions);
	 * 
	 * ICategory<String> parameterMapping = typeHandler.handle(
	 * parameterType, parameterOptions); Rule rule = new
	 * Rule(parameterCategory, parameterMapping);
	 */
    
//  /*
//  * assigns value with key to resultMap if the value is neither null nor
//  * equal to the UNASSIGNED constant.
//  */
// private static Map<ConfigurationOptions, String> putIfValid(Map<ConfigurationOptions, String> resultMap,
//	    ConfigurationOptions confOption, String value) {
//	Assert.isNotNull(value);
//	if (value.equals(AnnotationConstants.UNASSIGNED))
//	    return resultMap;
//	else {
//	    resultMap.put(confOption, value);
//	    return resultMap;
//	}
// }
 
    // Never used; still necessary to save the
    // resulting set of keywords?
    //private final Set<String> keywords = new LinkedHashSet<String>();

//@formatter:on
    // ===================================

}