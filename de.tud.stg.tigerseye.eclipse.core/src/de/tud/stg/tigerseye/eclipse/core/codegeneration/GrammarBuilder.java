package de.tud.stg.tigerseye.eclipse.core.codegeneration;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.parlex.core.Category;
import de.tud.stg.parlex.core.Grammar;
import de.tud.stg.parlex.core.ICategory;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.core.Rule;
import de.tud.stg.parlex.core.groupcategories.WaterCategory;
import de.tud.stg.popart.builder.core.annotations.DSL;
import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartLiteralKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars.CategoryNames;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars.HostLanguageGrammar;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ParameterOptions;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.TypeHandler;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.utils.GrammarBuilderHelper;

/**
 * {@link GrammarBuilder} builds the grammar for given DSL interfaces
 * 
 * @author Kamil Erhard
 * 
 */
public class GrammarBuilder {
    private static final Logger logger = LoggerFactory.getLogger(GrammarBuilder.class);

    public static final String DEFAULT_ARRAY_DELIMITER = ",";
    public static final String DEFAULT_PARAMETER_ESCAPE = "p";
    public static final String DEFAULT_WHITESPACE_ESCAPE = "_";
    // private static final String DEFAULT_STRING_QUOTATION =
    // "([\\w_]+|(\".*?\"))";
    private static final String DEFAULT_STRING_QUOTATION = "(\".*?\")";

    private final Grammar grammar;

	private final AtomicInteger parameterCounter = new AtomicInteger();

	private Category statement;
	private Category statements;
	private final HandlingDispatcher typeHandler;

    // private Rule startRule;

    private final UnicodeLookupTable unicodeLookupTable;

	public final HashMap<String, MethodOptions> methodAliases = new HashMap<String, MethodOptions>();
    // (Leo Roos; Jun 27, 2011):Never used; still necessary to save the
    // resulting set of keywords?
	private final Set<String> keywords = new LinkedHashSet<String>();


    public GrammarBuilder(UnicodeLookupTable ult) {
	this.unicodeLookupTable = ult;
		this.grammar = new Grammar();
		this.typeHandler = new HandlingDispatcher(this.grammar);
	}

	private void setWaterEnabled(boolean enabled) {
		if (enabled) {
			Category anything = new WaterCategory();
			this.grammar.addCategory(anything);

			Rule rAnyStatement = new Rule(this.statement, anything);
			this.grammar.addRule(rAnyStatement);

	    this.grammar.addWaterRule(rAnyStatement);
		}
	}

	private void setupGeneralGrammar() {
		Category program = new Category(CategoryNames.PROGRAM_CATEGORY, false);

	this.statement = new Category(CategoryNames.STATEMENT_CATEGORY, false);
	this.statements = new Category(CategoryNames.STATEMENTS_CATEGORY, false);

	Rule startRule = new Rule(program, this.statements);

		Rule rStatements = new Rule(this.statements, this.statement, GrammarBuilderHelper.getWhitespaceCategory(this.grammar, true),
				this.statements);

		Rule rStatement = new Rule(this.statements, this.statement);

		this.grammar.addCategory(program);
		this.grammar.addCategory(this.statement);
		this.grammar.addCategory(this.statements);

	this.grammar.setStartRule(startRule);
		this.grammar.addRule(rStatement);
		this.grammar.addRule(rStatements);
	}

    public IGrammar<String> buildGrammar(List<DSLDefinition> dsls) {
	ArrayList<Class<?>> clazzes = new ArrayList<Class<?>>(dsls.size());
	for (DSLDefinition dsl : dsls) {
	    Class<? extends de.tud.stg.popart.dslsupport.DSL> loadClass = dsl
		    .loadClass();
	    if (loadClass != null)
		clazzes.add(loadClass);
	    else
		logger.error(
			"Failed to load dsl {}, can not perform any transformations for that class",
			dsl);
	}
	return buildGrammar(clazzes.toArray(new Class<?>[0]));
    }

	public IGrammar<String> buildGrammar(Class<?>... clazzes) {
	this.setupGeneralGrammar();// TODO moved from constructor check if valid

		boolean waterSupported = true;

		for (Class<?> clazz : clazzes) {
	    // TODO Should normally not use any additional methods calls in a
	    // log statement unless they already have been called in the program
	    logger.debug("class " + clazz.getCanonicalName()
		    + " has annotations: "
					+ Arrays.toString(clazz.getAnnotations()));

			// check for configuration options
			Map<String, String> classOptions = getOptions(clazz.getAnnotation(DSL.class), getDefaultOptions());

			// logger.info("clazz: " + clazz + ", " + classOptions + " : " + getDefaultOptions() + ", ");

			DSL annotation = clazz.getAnnotation(DSL.class);
			if (annotation != null) {
				// check for additionals rules
				Class<? extends TypeHandler>[] typeRules = annotation.typeRules();

		// TODO check cyclic Dependency between Grammar and
		// HandlingDispatcher
				this.typeHandler.addAdditionalTypeRules(typeRules);

				// check for host language rules
				Class<? extends HostLanguageGrammar>[] hostLanguageRules = annotation.hostLanguageRules();
				this.setupHostLanguageRules(hostLanguageRules);

				// check water support
				waterSupported &= annotation.waterSupported();
			}

	    this.setWaterEnabled(waterSupported);

			// get all methods, including inherited ones
			Set<Method> methods = new LinkedHashSet<Method>();
			methods.addAll(Arrays.asList(clazz.getMethods()));
			methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
			methods.removeAll(Arrays.asList(Object.class.getDeclaredMethods()));

			this.typeHandler.handleDefaults(classOptions);

			for (Method method : methods) {
				Map<String, String> methodOptions = this.getOptions(method.getAnnotation(DSL.class), classOptions);

				PopartType p = method.getAnnotation(PopartType.class);

				if (p != null) {
					if (p.clazz() == PopartOperationKeyword.class) {
						this.handleMethod(method, methodOptions);
					} else if (p.clazz() == PopartLiteralKeyword.class) {
						this.handleLiteral(method, methodOptions);
					}
				}
			}

			// for (Constructor<?> constructor : clazz.getConstructors()) {
			// PopartType p = constructor.getAnnotation(PopartType.class);
			//
			// if (p != null) {
			// Pair<String, String> methodEscape = this.getEscaping(constructor,
			// classEscape.getX(), classEscape
			// .getY());
			//
			// this.handleConstructor(constructor, methodEscape.getX(),
			// methodEscape.getY());
			// }
	    // }
		}



		return this.grammar;
	}

	private void setupHostLanguageRules(Class<? extends HostLanguageGrammar>[] hostLanguageRules) {
		for (Class<? extends HostLanguageGrammar> clazz : hostLanguageRules) {
			try {
				HostLanguageGrammar newInstance = clazz.newInstance();
				newInstance.applySpecificGrammar(this.getGrammar());
			} catch (InstantiationException e) {
				logger.warn("Generated log statement",e);
			} catch (IllegalAccessException e) {
				logger.warn("Generated log statement",e);
			}
		}
	}

	public String getMethodProduction(AnnotatedElement element, String defaultName) {
		DSLMethod dslAnnotation = element.getAnnotation(DSLMethod.class);

		String methodProduction = null;

		if (dslAnnotation != null) {
			methodProduction = dslAnnotation.prettyName();
		}

		return assignFirstStringOrDefault(methodProduction, defaultName);
	}

	public Map<String, String> getOptions(DSL dslAnnotation, Map<String, String> currentOptions) {
		Map<String, String> resultMap = new HashMap<String, String>();

		if (dslAnnotation != null) {
			resultMap.put(ParameterOptions.PARAMETER_ESCAPE, dslAnnotation.parameterEscape());
			resultMap.put(ParameterOptions.WHITESPACE_ESCAPE, dslAnnotation.whitespaceEscape());
	    resultMap.put(ParameterOptions.ARRAY_DELIMITER, dslAnnotation.arrayDelimiter());
	    resultMap.put(ParameterOptions.STRING_QUOTATION, dslAnnotation.stringQuotation());
		}

		for (Entry<String, String> e : currentOptions.entrySet()) {
			String currentOption = currentOptions.get(e.getKey());
			String newOption = resultMap.get(e.getKey());
			resultMap.put(e.getKey(), assignFirstStringOrDefault(newOption, currentOption));
		}

		return resultMap;
	}

	private  String assignFirstStringOrDefault(String stringToCheck, String defaultString) {
		final String UNASSIGNED = "[unassigned]";

	String string;
	if (stringToCheck == null || stringToCheck.equals(UNASSIGNED))
	    string = defaultString;
	else
	    string = stringToCheck;
		return string;
	}

	private final static Pattern literalPattern = Pattern.compile("^get(\\S+)");

	private boolean handleLiteral(Method method, Map<String, String> methodOptions) {
		Class<?> returnType = method.getReturnType();

		if (returnType != void.class && returnType != Void.class) {
			Matcher matcher = literalPattern.matcher(method.getName());

			if (matcher.find()) {
				String literal = matcher.group(1);
				literal = literal.substring(0, 1).toLowerCase() + literal.substring(1);

				Category literalCategory = new Category(literal, true);
				this.grammar.addCategory(literalCategory);

				Rule literalRule = new Rule(this.statement, literalCategory);
				this.grammar.addRule(literalRule);

				ICategory<String> returnTypeCategory = this.typeHandler.handle(returnType, methodOptions);
				this.grammar.addCategory(returnTypeCategory);

				Rule returnTypeRule = new Rule(returnTypeCategory, literalCategory);
				this.grammar.addRule(returnTypeRule);

				this.methodAliases.put(literal, new MethodOptions(method.getName(), new LinkedList<Integer>(), method
						.getDeclaringClass()));

				return true;
			}
		}

		return false;
	}

	public static  class MethodOptions {

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

	private void handleMethod(Method method, Map<String, String> methodOptions) {
		String methodProduction = getMethodProduction(method, method.getName());

		Pattern[] pattern = 
				getPattern(methodOptions.get(ParameterOptions.PARAMETER_ESCAPE), methodOptions.get(ParameterOptions.WHITESPACE_ESCAPE));

		Type[] parameters = method.getGenericParameterTypes();

		this.handleNonLiteral(method, methodProduction, method.getGenericReturnType(), parameters, Modifier
				.isPublic(method.getModifiers()), pattern, method.getParameterAnnotations(), methodOptions);
	}

	// TODO: check if caching would speed up this method
	public  Pattern[] getPattern(String methodParameterEscape, String methodWhitespaceEscape) {
		return new Pattern[] {
				Pattern.compile("((?:\\Q" + methodWhitespaceEscape + "\\E{1,2})|(?:\\Q" + methodParameterEscape
						+ "\\E\\d+)|(?:(?!(?:\\Q" + methodParameterEscape + "\\E\\d+|\\Q" + methodWhitespaceEscape
						+ "\\E)).)+)"),

				Pattern.compile("\\Q" + methodParameterEscape + "\\E(\\d+)"),
				Pattern.compile("\\Q" + methodWhitespaceEscape + "\\E{1,2}") };
	}

    private void handleNonLiteral(Method method, String methodProduction, Type returnType, Type[] parameters,
			boolean isPublic, Pattern[] pattern, Annotation[][] parameterAnnotations, Map<String, String> methodOptions) {
		Matcher matcher = pattern[0].matcher(methodProduction);

		LinkedList<ICategory<String>> categories = new LinkedList<ICategory<String>>();

		StringBuilder sb = new StringBuilder();

		if (!matcher.find()) {
			return;
		}

		int index = 0;
		List<Integer> parameterIndices = new ArrayList<Integer>(method.getParameterTypes().length);

		do {
			String keyword = matcher.group(1);

			Matcher parameterMatcher = pattern[1].matcher(keyword);
			boolean isParameter = parameterMatcher.find();

			if (!isParameter) {
				Matcher whitespaceMatcher = pattern[2].matcher(keyword);
				boolean isWhitespace = whitespaceMatcher.find();

				if (isWhitespace) {
					if (keyword.length() == 1) {
						categories.add(GrammarBuilderHelper.getWhitespaceCategory(this.grammar, true));
					} else {
						categories.add(GrammarBuilderHelper.getWhitespaceCategory(this.grammar, false));
					}
				} else {

		    String uniChar = unicodeLookupTable.nameToUnicode(keyword);

		    if (uniChar == null) {
			logger.debug(
				"No unicode representation for [{}] found. Assuming this is a literal keyword.",
				keyword);
			uniChar = keyword;
		    } else {
			logger.debug(
				"found unicode representation [{}] for [{}]",
				uniChar, keyword);
		    }

		    sb.append(uniChar);

					categories.add(new Category(uniChar, true));

					this.keywords.add(uniChar);
				}
			} else {
				int parameterIndex = Integer.parseInt(parameterMatcher.group(1));
				parameterIndices.add(index);
				Type parameterType = null;
				Annotation[] pAnnotations = null;

				try {
					parameterType = parameters[parameterIndex];
					pAnnotations = parameterAnnotations[parameterIndex];
				} catch (IndexOutOfBoundsException e) {
					throw new IndexOutOfBoundsException("Grammar for method \"" + methodProduction
							+ "\" could not be built. Parameter reference $p" + parameterIndex
							+ " can not be resolved.");
				}

				String param = "P" + parameterIndex + "{" + this.parameterCounter.getAndIncrement() + "}";
				ICategory<String> parameterCategory = new Category(param, false);
				categories.add(parameterCategory);

				DSL parameterDSLAnnotation = null;
				for (Annotation a : pAnnotations) {
					if (a instanceof DSL) {
						parameterDSLAnnotation = (DSL) a;
						break;
					}
				}

				Map<String, String> parameterOptions = getOptions(parameterDSLAnnotation, methodOptions);

				ICategory<String> parameterMapping = this.typeHandler.handle(parameterType, parameterOptions);
				Rule rule = new Rule(parameterCategory, parameterMapping);

		ICategory<String> typeCategory = new Category(
			CategoryNames.PTYPE_CATEGORY, false);
				Rule typeRule = new Rule(parameterMapping, typeCategory);

				this.grammar.addRule(rule);
				this.grammar.addRule(typeRule);
				this.grammar.addCategory(parameterMapping);

				// sb.append(parameterMapping.toString().toUpperCase());
				sb.append(param);
			}

			sb.append('_');
			index++;
		} while ((matcher.find()));

		sb.deleteCharAt(sb.length() - 1);

		int methodCounter = this.parameterCounter.getAndIncrement();

		String indexedMethodProduction = "M" + methodCounter + "(" + methodProduction + ")";
		MethodOptions value = new MethodOptions(method.getName(), parameterIndices, method
				.getDeclaringClass());
		this.methodAliases.put(indexedMethodProduction, value);

		Category methodCategory = new Category(indexedMethodProduction, false);
		this.grammar.addCategory(methodCategory);

		DSLMethod annotation = method.getAnnotation(DSLMethod.class);

		if (isPublic && (annotation == null || annotation.topLevel())) {
			Rule methodRule = new Rule(this.statement, methodCategory);
			this.grammar.addRule(methodRule);
		}

		Rule rule = new Rule(methodCategory, categories);
		this.grammar.addRule(rule);

		for (ICategory<String> c : categories) {
			this.grammar.addCategory(c);
		}

		if (returnType != void.class && returnType != Void.class) {
			ICategory<String> returnTypeCategory = this.typeHandler.handle(returnType, methodOptions);
			this.grammar.addCategory(returnTypeCategory);

	    ICategory<String> typeCategory = new Category(
		    CategoryNames.RTYPE_CATEGORY, false);
			Rule typeToMethod = new Rule(typeCategory, methodCategory);
			this.grammar.addRule(typeToMethod);

			Rule returnTypeToMethod = new Rule(returnTypeCategory, methodCategory);
			this.grammar.addRule(returnTypeToMethod);
		}
	}

    // (Leo Roos; Jun 27, 2011): Never used; plus this information is also
    // available via the generated IGrammar
    // public IRule<String> getStartRule() {
    // return this.startRule;
    // }

    private IGrammar<String> getGrammar() {
		return this.grammar;
	}

    public Map<String, MethodOptions> getMethodOptions() {
	return Collections.unmodifiableMap(this.methodAliases);
	}

    // (Leo Roos; Jun 27, 2011): never used
    /*
     * public Set<String> getKeywords() { return this.keywords; }
     */

	public Map<String, String> getDefaultOptions() {
		Map<String, String> defaultOptions = new HashMap<String, String>();
		defaultOptions.put(ParameterOptions.PARAMETER_ESCAPE, DEFAULT_PARAMETER_ESCAPE);
		defaultOptions.put(ParameterOptions.WHITESPACE_ESCAPE, DEFAULT_WHITESPACE_ESCAPE);
	defaultOptions.put(ParameterOptions.ARRAY_DELIMITER, DEFAULT_ARRAY_DELIMITER);
	defaultOptions.put(ParameterOptions.STRING_QUOTATION, DEFAULT_STRING_QUOTATION);
		return defaultOptions;
	}

	// public static void main(String[] args) {
	//
	// logger.info(GrammarBuilder.getOptions(StateMachineDSL.class.getAnnotation(DSL.class),
	// GrammarBuilder
	// .getDefaultOptions()));
	// }
}
