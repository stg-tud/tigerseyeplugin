package de.tud.stg.popart.builder.core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import de.tud.stg.parlex.core.Category;
import de.tud.stg.parlex.core.Grammar;
import de.tud.stg.parlex.core.ICategory;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.core.IRule;
import de.tud.stg.parlex.core.Rule;
import de.tud.stg.parlex.core.groupcategories.StringCategory;
import de.tud.stg.parlex.core.groupcategories.WaterCategory;
import de.tud.stg.popart.builder.core.annotations.DSL;
import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.builder.core.typeHandling.HandlingDispatcher;
import de.tud.stg.popart.builder.core.typeHandling.TypeHandler;
import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartLiteralKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;

/**
 * {@link GrammarBuilder} builds the grammar for given DSL interfaces
 * 
 * @author Kamil Erhard
 * 
 */
public class GrammarBuilder {
private static final Logger logger = LoggerFactory.getLogger(GrammarBuilder.class);

	private static final String DEFAULT_ARRAY_DELIMITER = ",";
	public static final String DEFAULT_PARAMETER_ESCAPE = "p";
	public static final String DEFAULT_WHITESPACE_ESCAPE = "_";
	// private static final String DEFAULT_STRING_QUOTATION = "([\\w_]+|(\".*?\"))";
	private static final String DEFAULT_STRING_QUOTATION = "(\".*?\")";

	private final IGrammar<String> grammar;

	private final AtomicInteger parameterCounter = new AtomicInteger();

	private Category statement;
	private Category statements;
	private final HandlingDispatcher typeHandler;

	private Rule startRule;

	private static UnicodeLookupTable unicodeLookupTable = UnicodeLookupTable.getDefaultInstance();

	public final HashMap<String, MethodOptions> methodAliases = new HashMap<String, MethodOptions>();
	private final Set<String> keywords = new LinkedHashSet<String>();

	public GrammarBuilder() {

		this.grammar = new Grammar();
		this.typeHandler = new HandlingDispatcher(this.grammar);

		this.setupGeneralGrammar();
	}

	private void setWaterEnabled(boolean enabled) {
		if (enabled) {
			Category anything = new WaterCategory();
			this.grammar.addCategory(anything);

			Rule rAnyStatement = new Rule(this.statement, anything);
			this.grammar.addRule(rAnyStatement);

			Grammar g = (Grammar) this.grammar;
			g.addWaterRule(rAnyStatement);
		}
	}

	private void setupGeneralGrammar() {
		Category program = new Category("PROGRAM", false);

		this.statement = new Category("STATEMENT", false);
		this.statements = new Category("STATEMENTS", false);

		this.startRule = new Rule(program, this.statements);

		Rule rStatements = new Rule(this.statements, this.statement, getWhitespaceCategory(this.grammar, true),
				this.statements);

		Rule rStatement = new Rule(this.statements, this.statement);

		this.grammar.addCategory(program);
		this.grammar.addCategory(this.statement);
		this.grammar.addCategory(this.statements);

		this.grammar.setStartRule(this.startRule);
		this.grammar.addRule(rStatement);
		this.grammar.addRule(rStatements);
	}

	public IGrammar<String> buildGrammar(Class<?>... clazzes) {

		boolean waterSupported = true;

		for (Class<?> clazz : clazzes) {
			logger.info("class " + clazz.getCanonicalName() + " has annotation: "
					+ Arrays.toString(clazz.getAnnotations()));

			// check for configuration options
			Map<String, String> classOptions = GrammarBuilder.getOptions(clazz.getAnnotation(DSL.class), getDefaultOptions());

			// logger.info("clazz: " + clazz + ", " + classOptions + " : " + getDefaultOptions() + ", ");

			DSL annotation = clazz.getAnnotation(DSL.class);
			if (annotation != null) {
				// check for additionals rules
				Class<? extends TypeHandler>[] typeRules = annotation.typeRules();
				this.typeHandler.addAdditionalTypeRules(typeRules);

				// check for host language rules
				Class<? extends HostLanguageGrammar>[] hostLanguageRules = annotation.hostLanguageRules();
				this.setupHostLanguageRules(hostLanguageRules);

				// check water support
				waterSupported &= annotation.waterSupported();
			}

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

		this.setWaterEnabled(waterSupported);
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

	public static String getMethodProduction(AnnotatedElement element, String defaultName) {
		DSLMethod dslAnnotation = element.getAnnotation(DSLMethod.class);

		String methodProduction = null;

		if (dslAnnotation != null) {
			methodProduction = dslAnnotation.prettyName();
		}

		return assignFirstStringOrDefault(methodProduction, defaultName);
	}

	public static Map<String, String> getOptions(DSL dslAnnotation, Map<String, String> currentOptions) {
		Map<String, String> resultMap = new HashMap<String, String>();

		if (dslAnnotation != null) {
			resultMap.put("parameterEscape", dslAnnotation.parameterEscape());
			resultMap.put("whitespaceEscape", dslAnnotation.whitespaceEscape());
			resultMap.put("arrayDelimiter", dslAnnotation.arrayDelimiter());
			resultMap.put("stringQuotation", dslAnnotation.stringQuotation());
		}

		for (Entry<String, String> e : currentOptions.entrySet()) {
			String currentOption = currentOptions.get(e.getKey());
			String newOption = resultMap.get(e.getKey());
			resultMap.put(e.getKey(), assignFirstStringOrDefault(newOption, currentOption));
		}

		return resultMap;
	}

	private static String assignFirstStringOrDefault(String stringToCheck, String defaultString) {
		final String UNASSIGNED = "[unassigned]";

		return (stringToCheck == null || stringToCheck.equals(UNASSIGNED)) ? defaultString : stringToCheck;
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
		String methodProduction = GrammarBuilder.getMethodProduction(method, method.getName());

		Pattern[] pattern = GrammarBuilder
				.getPattern(methodOptions.get("parameterEscape"), methodOptions.get("whitespaceEscape"));

		Type[] parameters = method.getGenericParameterTypes();

		this.handleNonLiteral(method, methodProduction, method.getGenericReturnType(), parameters, Modifier
				.isPublic(method.getModifiers()), pattern, method.getParameterAnnotations(), methodOptions);
	}

	// TODO: check if caching would speed up this method
	public static Pattern[] getPattern(String methodParameterEscape, String methodWhitespaceEscape) {
		return new Pattern[] {
				Pattern.compile("((?:\\Q" + methodWhitespaceEscape + "\\E{1,2})|(?:\\Q" + methodParameterEscape
						+ "\\E\\d+)|(?:(?!(?:\\Q" + methodParameterEscape + "\\E\\d+|\\Q" + methodWhitespaceEscape
						+ "\\E)).)+)"),

				Pattern.compile("\\Q" + methodParameterEscape + "\\E(\\d+)"),
				Pattern.compile("\\Q" + methodWhitespaceEscape + "\\E{1,2}") };
	}

	public static ICategory<String> getWhitespaceCategory(IGrammar<String> grammar, boolean optional) {

		ICategory<String> optionalWhitespaces = new StringCategory("\\s*");

//		ICategory<String> OWS = new Category("OWHITESPACE", false);//XXX never read
		ICategory<String> OWSS = new Category("OWHITESPACES", false);

		Rule r1 = new Rule(OWSS, optionalWhitespaces, OWSS);
		Rule r2 = new Rule(OWSS, optionalWhitespaces);

		grammar.addRule(r1);
		grammar.addRule(r2);

		ICategory<String> requiredWhitespaces = new StringCategory("\\s+");

//		ICategory<String> RWS = new Category("RWHITESPACE", false); //XXX Never read
		ICategory<String> RWSS = new Category("RWHITESPACES", false);

		Rule r3 = new Rule(RWSS, requiredWhitespaces, OWSS);
		Rule r4 = new Rule(RWSS, requiredWhitespaces);

		grammar.addRule(r3);
		grammar.addRule(r4);

		if (optional) {
			return OWSS;
		} else {
			return RWSS;
		}
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
						categories.add(GrammarBuilder.getWhitespaceCategory(this.grammar, true));
					} else {
						categories.add(GrammarBuilder.getWhitespaceCategory(this.grammar, false));
					}
				} else {

					Character c = GrammarBuilder.unicodeLookupTable.transform(keyword);

					if (c != null) {
						keyword = c.toString();
					}

					sb.append(keyword);

					categories.add(new Category(keyword, true));

					this.keywords.add(keyword);
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

				Map<String, String> parameterOptions = GrammarBuilder.getOptions(parameterDSLAnnotation, methodOptions);

				ICategory<String> parameterMapping = this.typeHandler.handle(parameterType, parameterOptions);
				Rule rule = new Rule(parameterCategory, parameterMapping);

				ICategory<String> typeCategory = new Category("PTYPE", false);
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
		this.methodAliases.put(indexedMethodProduction, new MethodOptions(method.getName(), parameterIndices, method
				.getDeclaringClass()));

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

			ICategory<String> typeCategory = new Category("RTYPE", false);
			Rule typeToMethod = new Rule(typeCategory, methodCategory);
			this.grammar.addRule(typeToMethod);

			Rule returnTypeToMethod = new Rule(returnTypeCategory, methodCategory);
			this.grammar.addRule(returnTypeToMethod);
		}
	}

	public IRule<String> getStartRule() {
		return this.startRule;
	}

	public IGrammar<String> getGrammar() {
		return this.grammar;
	}

	public MethodOptions getMethodOptions(String string) {
		return this.methodAliases.get(string);
	}

	public Set<String> getKeywords() {
		return this.keywords;
	}

	public static Map<String, String> getDefaultOptions() {
		Map<String, String> defaultOptions = new HashMap<String, String>();
		defaultOptions.put("parameterEscape", DEFAULT_PARAMETER_ESCAPE);
		defaultOptions.put("whitespaceEscape", DEFAULT_WHITESPACE_ESCAPE);
		defaultOptions.put("arrayDelimiter", DEFAULT_ARRAY_DELIMITER);
		defaultOptions.put("stringQuotation", DEFAULT_STRING_QUOTATION);
		return defaultOptions;
	}

	// public static void main(String[] args) {
	//
	// logger.info(GrammarBuilder.getOptions(StateMachineDSL.class.getAnnotation(DSL.class),
	// GrammarBuilder
	// .getDefaultOptions()));
	// }
}
