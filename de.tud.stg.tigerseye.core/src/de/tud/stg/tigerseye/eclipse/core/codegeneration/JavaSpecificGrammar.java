package de.tud.stg.tigerseye.eclipse.core.codegeneration;

import de.tud.stg.parlex.core.Category;
import de.tud.stg.parlex.core.ICategory;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.core.IRule;
import de.tud.stg.parlex.core.Rule;
import de.tud.stg.parlex.core.groupcategories.StringCategory;

/**
 * {@link JavaSpecificGrammar} is a host language specific grammar and setups some basic rules for interaction with
 * variables, methods and constructors and handling of parenthesis.
 * 
 * @author Kamil Erhard
 * 
 */
public class JavaSpecificGrammar implements HostLanguageGrammar {

	@Override
	public void applySpecificGrammar(IGrammar<String> grammar) {
		GrammarBuilder gb = new GrammarBuilder();
		ICategory<String> optionalWS = gb.getWhitespaceCategory(grammar, true);
		ICategory<String> WS = gb.getWhitespaceCategory(grammar, false);

		Category pType = new Category("PTYPE", false);
		Category rType = new Category("RTYPE", false);
		Category type = new Category("TYPE", false);

		IRule<String> typeRule1 = new Rule(type, pType);
		Rule typeRule2 = new Rule(type, rType);

		grammar.addRules(typeRule1, typeRule2);

		Category var = new Category("VARIABLE", false);
		Category varName = new StringCategory("[A-Za-z0-9_]+");
		Category dot = new Category(".", true);

		Rule varRule1 = new Rule(pType, var);
		Rule varRule2 = new Rule(var, varName, optionalWS, dot, optionalWS, var);
		Rule varRule3 = new Rule(var, varName);

		grammar.addRules(varRule1, varRule2, varRule3);

		Category arguments = new Category("ARGUMENTS", false);
		Category comma = new Category(",", true);

		Rule argumentRule1 = new Rule(arguments, arguments, optionalWS, comma, optionalWS, type);
		Rule argumentRule2 = new Rule(arguments, type);

		grammar.addRules(argumentRule1, argumentRule2);

		Category application = new Category("(...)", false);
		Category paraL = new Category("(", true);
		Category paraR = new Category(")", true);

		Rule applicationRule1 = new Rule(application, paraL, optionalWS, arguments, optionalWS, paraR);
		Rule applicationRule2 = new Rule(application, paraL, optionalWS, paraR);

		grammar.addRules(applicationRule1, applicationRule2);

		Category methodC = new Category("METHOD", false);
		Rule methodRule1 = new Rule(methodC, var, optionalWS, application);
		Rule methodRule2 = new Rule(pType, methodC);

		grammar.addRules(methodRule1, methodRule2);

		Category constructorC = new Category("CONSTRUCTOR", false);
		Category newC = new Category("new", true);

		Rule constructorRule1 = new Rule(constructorC, newC, WS, var, optionalWS, application);
		Rule constructorRule2 = new Rule(pType, constructorC);

		grammar.addRules(constructorRule1, constructorRule2);

		grammar.addCategories(pType, rType, type, var, varName, arguments, application, methodC, newC, dot, comma,
				paraL, paraR);

		Category statement = new Category("STATEMENT", false);
		Category statements = new Category("STATEMENTS", false);

		Rule groupStatement = new Rule(statement, new Category("{", true), gb.getWhitespaceCategory(
				grammar, true), statements, gb.getWhitespaceCategory(grammar, true),
				new Category("}", true));

		// Rule emptyGroupStatement = new Rule(statement, new Category("{", true), optionalWS, new Category("}", true));

		grammar.addRules(groupStatement);
	}
}
