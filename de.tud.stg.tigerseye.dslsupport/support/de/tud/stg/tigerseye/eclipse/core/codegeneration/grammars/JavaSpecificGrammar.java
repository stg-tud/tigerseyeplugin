package de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars;

import de.tud.stg.parlex.core.Category;
import de.tud.stg.parlex.core.ICategory;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.core.IRule;
import de.tud.stg.parlex.core.Rule;
import de.tud.stg.parlex.core.groupcategories.StringCategory;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.utils.WhitespaceCategoryDefinition;

/**
 * {@link JavaSpecificGrammar} is a host language specific grammar and setups some basic rules for interaction with
 * variables, methods and constructors and handling of parenthesis.
 * 
 * @author Kamil Erhard
 * 
 */
public class JavaSpecificGrammar implements HostLanguageGrammar {

	//categories used only locally
	private static final String RCUB_CATEGORY = "}";
	private static final String LCUB_CATEGORY = "{";
	private static final String NEW_CATEGORY = "new";	
	private static final String CONSTRUCTOR_CATEGORY = "CONSTRUCTOR";
	private static final String METHOD_CATEGORY = "METHOD";
	private static final String PARENTHESIS_RIGHT_CATEGORY = ")";
	private static final String PARENTHESIS_LEFT_CATEGORY = "(";
	private static final String APPLICATION_CATEGORY = "(...)";
	private static final String COMMA_CATEGORY = ",";
	private static final String ARGUMENTS_CATEGORY = "ARGUMENTS";
	private static final String DOT_CATEGORY = ".";
	private static final String VARIABLE_CATEGORY = "VARIABLE";
	
	@Override
	public void applySpecificGrammar(IGrammar<String> grammar) {
		WhitespaceCategoryDefinition.setWhitspaceRules(grammar);
		ICategory<String> optionalWS = WhitespaceCategoryDefinition.getNewOptionalWhitespaceCategory();
		ICategory<String> WS = WhitespaceCategoryDefinition.getNewRequiredWhitespaceCategory();

		Category pType = new Category(CategoryNames.PARAMETERTYPE_CATEGORY, false);
		Category rType = new Category(CategoryNames.RETURNTYPE_CATEGORY, false);
		Category type = new Category(CategoryNames.TYPE_CATEGORY, false);

		IRule<String> typeRule1 = new Rule(type, pType);
		Rule typeRule2 = new Rule(type, rType);

		grammar.addRules(typeRule1, typeRule2);

		Category var = new Category(VARIABLE_CATEGORY, false);
		Category varName = new StringCategory("[A-Za-z0-9_]+");
		Category dot = new Category(DOT_CATEGORY, true);

		Rule varRule1 = new Rule(pType, var);
		Rule varRule2 = new Rule(var, varName, optionalWS, dot, optionalWS, var);
		Rule varRule3 = new Rule(var, varName);

		grammar.addRules(varRule1, varRule2, varRule3);

		Category arguments = new Category(ARGUMENTS_CATEGORY, false);
		Category comma = new Category(COMMA_CATEGORY, true);

		Rule argumentRule1 = new Rule(arguments, arguments, optionalWS, comma, optionalWS, type);
		Rule argumentRule2 = new Rule(arguments, type);

		grammar.addRules(argumentRule1, argumentRule2);

		Category application = new Category(APPLICATION_CATEGORY, false);
		Category paraL = new Category(PARENTHESIS_LEFT_CATEGORY, true);
		Category paraR = new Category(PARENTHESIS_RIGHT_CATEGORY, true);

		Rule applicationRule1 = new Rule(application, paraL, optionalWS, arguments, optionalWS, paraR);
		Rule applicationRule2 = new Rule(application, paraL, optionalWS, paraR);

		grammar.addRules(applicationRule1, applicationRule2);

		Category methodC = new Category(METHOD_CATEGORY, false);
		Rule methodRule1 = new Rule(methodC, var, optionalWS, application);
		Rule methodRule2 = new Rule(pType, methodC);

		grammar.addRules(methodRule1, methodRule2);

		Category constructorC = new Category(CONSTRUCTOR_CATEGORY, false);
		Category newC = new Category(NEW_CATEGORY, true);

		Rule constructorRule1 = new Rule(constructorC, newC, WS, var, optionalWS, application);
		Rule constructorRule2 = new Rule(pType, constructorC);

		grammar.addRules(constructorRule1, constructorRule2);

		grammar.addCategories(pType, rType, type, var, varName, arguments, application, methodC, newC, dot, comma,
				paraL, paraR);

		Category statement = new Category(CategoryNames.STATEMENT_CATEGORY, false);
		Category statements = new Category(CategoryNames.STATEMENTS_CATEGORY, false);

		Rule groupStatement = new Rule(statement, new Category(LCUB_CATEGORY, true), WhitespaceCategoryDefinition.getAndSetOptionalWhitespace(
				grammar), statements, WhitespaceCategoryDefinition.getAndSetOptionalWhitespace(grammar),
				new Category(RCUB_CATEGORY, true));

		// Rule emptyGroupStatement = new Rule(statement, new Category("{", true), optionalWS, new Category("}", true));

		grammar.addRule(groupStatement);
	}
}
