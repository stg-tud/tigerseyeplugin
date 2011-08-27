package de.tud.stg.popart.builder.test.dsls;

import groovy.lang.Closure;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import de.tud.stg.parlex.core.Category;
import de.tud.stg.parlex.core.Grammar;
import de.tud.stg.popart.builder.core.annotations.DSL;
import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars.CategoryNames;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.TypeHandler;

/**
 * {@link ImprovedBnfDSL} is a DSL with support for conditional statements in an alternative if-then-else syntax
 * 
 * @author Kamil Erhard
 * 
 */
@DSL(whitespaceEscape = " ", typeRules = { ImprovedBnfDSL.IdentifierType.class, ImprovedBnfDSL.QuotedSymbolType.class }, arrayDelimiter = " ")
public class ImprovedBnfDSL implements de.tud.stg.popart.dslsupport.DSL {

	private Grammar grammar = new Grammar();

	public static class IdentifierType extends TypeHandler {
		@Override
		public String getRegularExpression() {
			return "([A-Za-z_](?:[A-Za-z_]|[0-9])*)";
		}

		@Override
		public Class<?> getMainType() {
			return Identifier.class;
		}
	}

	public static class QuotedSymbolType extends TypeHandler {

		@Override
		public String getRegularExpression() {
			return "\".*\"";
		}

		@Override
		public Class<?> getMainType() {
			return QuotedSymbol.class;
		}

	}

	public Object eval(HashMap map, Closure cl) {
		cl.setDelegate(this);
		cl.setResolveStrategy(Closure.DELEGATE_FIRST);
		return cl.call();
	}

	@DSLMethod(production = "p0")
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 1)
	public Syntax syntax(Rule[] rules) {
		Syntax syntax = new Syntax(rules);

		Category syntaxCategory = new Category(CategoryNames.PROGRAM_CATEGORY, false);
		this.grammar.addCategory(syntaxCategory);

		for (Rule r : rules) {
			Category rulesCategory = new Category(r.toString(), false);
			de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(syntaxCategory, rulesCategory);
			this.grammar.addCategory(rulesCategory);
			this.grammar.addRule(rule);
		}

		syntax.setGrammar(this.grammar);
		this.grammar = new Grammar();
		return syntax;
	}

	@DSLMethod(production = "p0 ::= p1", topLevel = false)
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 1)
	public Rule rule(Identifier identifier, Expression expression) {

		Rule rule = new Rule(identifier, expression);

		Category identifierCategory = new Category(identifier.toString(), false);
		Category expressionCategory = new Category(expression.toString(), false);

		this.grammar.addCategories(identifierCategory, expressionCategory);

		de.tud.stg.parlex.core.Rule r = new de.tud.stg.parlex.core.Rule(identifierCategory, expressionCategory);

		this.grammar.addRule(r);

		return rule;
	}

	@DSLMethod(production = "p0", topLevel = false)
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 1)
	public Expression expression(@DSL(arrayDelimiter = "|") Term[] terms) {

		Expression expression = new Expression(terms);
		Category expressionCategory = new Category(expression.toString(), false);
		this.grammar.addCategory(expressionCategory);

		for (Term t : terms) {
			Category termCategory = new Category(t.toString(), false);
			de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(expressionCategory, termCategory);
			this.grammar.addCategory(termCategory);
			this.grammar.addRule(rule);
		}

		return expression;
	}

	@DSLMethod(production = "p0", topLevel = false)
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 1)
	public Term termFromFactors(Factor[] factors) {

		Term term = new Term(factors);
		Category termCategory = new Category(term.toString(), false);
		this.grammar.addCategory(termCategory);

		for (Factor f : factors) {
			Category factorCategory = new Category(f.toString(), false);
			de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(termCategory, factorCategory);
			this.grammar.addCategory(factorCategory);
			this.grammar.addRule(rule);
		}

		return term;
	}

	@DSLMethod(production = "p0", topLevel = false)
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 1)
	public Factor factorFromIdentifier(Identifier identifier) {

		Factor factor = new Factor(identifier);
		Category factorCategory = new Category(factor.toString(), false);
		Category identifierCategory = new Category(identifier.toString(), false);

		de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(factorCategory, identifierCategory);
		this.grammar.addCategories(factorCategory, identifierCategory);
		this.grammar.addRule(rule);

		return factor;
	}

	@DSLMethod(production = "p0", topLevel = false)
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 1)
	public Factor factorFromQuotedSymbol(QuotedSymbol quotedSymbol) {

		Factor factor = new Factor(quotedSymbol);
		Category factorCategory = new Category(factor.toString(), false);
		Category quotedSymbolCategory = new Category(quotedSymbol.toString(), false);

		de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(factorCategory, quotedSymbolCategory);
		this.grammar.addCategories(factorCategory, quotedSymbolCategory);
		this.grammar.addRule(rule);

		return factor;
	}

	@DSLMethod(production = "( p0 )", topLevel = false)
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 1)
	public Factor factorFromExpressionInParanthesis(Expression expression) {

		Factor factor = new Factor(expression);
		// Category factorCategory = new Category(factor.toString(), false);
		// Category leftP = new Category("(", true);
		// Category expressionCategory = new Category(expression.toString(), false);
		// Category rightP = new Category(")", true);
		//
		// de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(factorCategory, leftP, expressionCategory,
		// rightP);
		// this.grammar.addCategories(factorCategory, leftP, expressionCategory, rightP);
		// this.grammar.addRule(rule);

		return factor;
	}

	@DSLMethod(production = "[ p0 ]", topLevel = false)
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 1)
	public Factor factorFromExpressionInBrackets(Expression expression) {
		Factor factor = new Factor(expression);
		// Category factorCategory = new Category(factor.toString(), false);
		// Category leftP = new Category("[", true);
		// Category expressionCategory = new Category(expression.toString(), false);
		// Category rightP = new Category("]", true);
		//
		// de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(factorCategory, leftP, expressionCategory,
		// rightP);
		// this.grammar.addCategories(factorCategory, leftP, expressionCategory, rightP);
		// this.grammar.addRule(rule);

		return factor;
	}

	@DSLMethod(production = "{ p0 }", topLevel = false)
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 1)
	public Factor factorFromExpressionInBraces(Expression expression) {

		Factor factor = new Factor(expression);

		Category factorCategory = new Category(factor.toString(), false);
		Category expressionCategory = new Category(expression.toString(), false);

		de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(factorCategory, expressionCategory);
		this.grammar.addCategories(factorCategory, expressionCategory);
		this.grammar.addRule(rule);

		return factor;
	}

	private static AtomicInteger uuidCounter = new AtomicInteger();

	public static class Syntax {

		private final int uid = uuidCounter.getAndIncrement();
		private Grammar grammar;

		public Syntax(Rule[] rules) {
			// TODO Auto-generated constructor stub
		}

		public void setGrammar(Grammar grammar) {
			this.grammar = grammar;
		}

		public Grammar getGrammar() {
			return this.grammar;
		}

		@Override
		public String toString() {
			return Integer.toString(this.uid);
		}
	}

	public static class Rule {

		private final int uid = uuidCounter.getAndIncrement();

		public Rule(de.tud.stg.popart.builder.test.dsls.ImprovedBnfDSL.Identifier identifier,
				de.tud.stg.popart.builder.test.dsls.ImprovedBnfDSL.Expression expression) {
			// TODO Auto-generated constructor stub
		}

		@Override
		public String toString() {
			return Integer.toString(this.uid);
		}
	}

	public static class Expression {

		private final int uid = uuidCounter.getAndIncrement();

		public Expression(Term[] terms) {
			// TODO Auto-generated constructor stub
		}

		public Expression(Term t) {
			// TODO Auto-generated constructor stub
		}

		@Override
		public String toString() {
			return Integer.toString(this.uid);
		}
	}

	public static class Term {

		private final int uid = uuidCounter.getAndIncrement();

		private final de.tud.stg.popart.builder.test.dsls.ImprovedBnfDSL.Factor[] factors;

		public Term(Factor[] factors) {
			this.factors = factors;
		}

		@Override
		public String toString() {
			return Integer.toString(this.uid);
		}
	}

	public static class Factor {

		private final int uid = uuidCounter.getAndIncrement();

		private final Object o;

		public Factor(Expression expression) {
			this.o = expression;
		}

		public Factor(QuotedSymbol quotedSymbol) {
			this.o = quotedSymbol;
		}

		public Factor(Identifier identifier) {
			this.o = identifier;
		}

		@Override
		public String toString() {
			return Integer.toString(this.uid);
		}
	}

	public static class Identifier {

		private final String representation;

		public Identifier(String representation) {
			this.representation = representation;
		}

		@Override
		public String toString() {
			return this.representation;
		}
	}

	public static class QuotedSymbol {

		private final String representation;

		public QuotedSymbol(String representation) {
			this.representation = representation;
		}

		@Override
		public String toString() {
			return this.representation;
		}
	}
}
