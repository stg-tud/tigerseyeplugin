package de.tud.stg.popart.builder.test.dsls;


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
 * {@link BnfDSL} is a DSL with support for conditional statements in an alternative if-then-else syntax
 * 
 * @author Kamil Erhard
 * 
 */
@DSL(whitespaceEscape = " ", typeRules = { BnfDSL.LetterType.class, BnfDSL.LetterType.class,
		BnfDSL.AnyCharacterType.class }, arrayDelimiter = " ")
public class BnfDSL implements de.tud.stg.popart.dslsupport.DSL {

	public static class LetterType extends TypeHandler {
		@Override
		public String getRegularExpression() {
			return "[A-Za-z_]";
		}

		@Override
		public Class<?> getMainType() {
			return Letter.class;
		}
	}

	public static class DigitType extends TypeHandler {

		@Override
		public String getRegularExpression() {
			return "[0-9]";
		}

		@Override
		public Class<?> getMainType() {
			return Digit.class;
		}
	}

	public static class AnyCharacterType extends TypeHandler {

		@Override
		public String getRegularExpression() {
			return ".";
		}

		@Override
		public Class<?> getMainType() {
			return AnyCharacter.class;
		}
	}

	private final HashMap<Identifier, Expression> mapping = new HashMap<Identifier, Expression>();
	private final HashMap<Object, Identifier> objectToIdentifier = new HashMap<Object, Identifier>();

	@DSLMethod(prettyName = "p0")
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 1)
	public Syntax syntax(Rule[] rules) {
		Syntax syntax = new Syntax(rules);

//		Category syntaxCategory = new Category(CategoryNames.PROGRAM_CATEGORY, false);
		// this.grammar.addCategory(syntaxCategory);
		//
		// for (Rule r : rules) {
		// Category rulesCategory = new Category(r.toString(), false);
		// de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(syntaxCategory, rulesCategory);
		// this.grammar.addCategory(rulesCategory);
		// this.grammar.addRule(rule);
		// }

		Grammar g = new Grammar();

		for (Rule r : rules) {
			this.mapping.put(r.lhs, r.rhs);
		}

		syntax.evaluate(g, this.mapping);

		syntax.setGrammar(g);

		return syntax;
	}

	@DSLMethod(prettyName = "p0 ::= p1", topLevel = false)
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 1)
	public Rule rule(Identifier identifier, Expression expression) {

		Rule rule = new Rule(identifier, expression);

		//
		// Category identifierCategory = new Category(identifier.toString(), false);
		// Category expressionCategory = new Category(expression.toString(), false);
		//
		// this.grammar.addCategories(identifierCategory, expressionCategory);
		//
		// de.tud.stg.parlex.core.Rule r = new de.tud.stg.parlex.core.Rule(identifierCategory, expressionCategory);
		//
		// this.grammar.addRule(r);

		return rule;
	}

	@DSLMethod(prettyName = "p0", topLevel = false)
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 1)
	public Expression expression(@DSL(arrayDelimiter = "|") Term[] terms) {

		Expression expression = new Expression(terms);

		// Category expressionCategory = new Category(expression.toString(), false);
		// this.grammar.addCategory(expressionCategory);
		//
		// for (Term t : terms) {
		// Category termCategory = new Category(t.toString(), false);
		// de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(expressionCategory, termCategory);
		// this.grammar.addCategory(termCategory);
		// this.grammar.addRule(rule);
		// }

		return expression;
	}

	@DSLMethod(prettyName = "p0", topLevel = false)
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 1)
	public Term termFromFactors(Factor[] factors) {

		Term term = new Term(factors);

		// Category termCategory = new Category(term.toString(), false);
		// this.grammar.addCategory(termCategory);
		//
		// for (Factor f : factors) {
		// Category factorCategory = new Category(f.toString(), false);
		// de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(termCategory, factorCategory);
		// this.grammar.addCategory(factorCategory);
		// this.grammar.addRule(rule);
		// }

		return term;
	}

	@DSLMethod(prettyName = "p0", topLevel = false)
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 1)
	public Factor factorFromIdentifier(Identifier identifier) {

		Factor factor = new Factor(identifier);

		// Category factorCategory = new Category(factor.toString(), false);
		// Category identifierCategory = new Category(identifier.toString(), false);
		//
		// de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(factorCategory, identifierCategory);
		// this.grammar.addCategories(factorCategory, identifierCategory);
		// this.grammar.addRule(rule);

		return factor;
	}

	@DSLMethod(prettyName = "p0", topLevel = false)
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 1)
	public Factor factorFromQuotedSymbol(QuotedSymbol quotedSymbol) {

		Factor factor = new Factor(quotedSymbol);
		// Category factorCategory = new Category(factor.toString(), false);
		// Category identifierCategory = new Category(quotedSymbol.toString(), false);
		//
		// de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(factorCategory, identifierCategory);
		// this.grammar.addCategories(factorCategory, identifierCategory);
		// this.grammar.addRule(rule);

		return factor;
	}

	// @DSLMethod(prettyName = "( p0 )", topLevel = false)
	// @PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 1)
	// public Factor factorFromExpressionInParanthesis(Expression expression) {
	// Factor factor = new Factor(expression);
	// Category factorCategory = new Category(factor.toString(), false);
	// Category leftP = new Category("(", true);
	// Category expressionCategory = new Category(expression.toString(), false);
	// Category rightP = new Category(")", true);
	//
	// de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(factorCategory, leftP, expressionCategory,
	// rightP);
	// this.grammar.addCategories(factorCategory, leftP, expressionCategory, rightP);
	// this.grammar.addRule(rule);
	//
	// return factor;
	// }
	//
	// @DSLMethod(prettyName = "[ p0 ]", topLevel = false)
	// @PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 1)
	// public Factor factorFromExpressionInBrackets(Expression expression) {
	// Factor factor = new Factor(expression);
	// Category factorCategory = new Category(factor.toString(), false);
	// Category leftP = new Category("[", true);
	// Category expressionCategory = new Category(expression.toString(), false);
	// Category rightP = new Category("]", true);
	//
	// de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(factorCategory, leftP, expressionCategory,
	// rightP);
	// this.grammar.addCategories(factorCategory, leftP, expressionCategory, rightP);
	// this.grammar.addRule(rule);
	//
	// return factor;
	// }

	@DSLMethod(prettyName = "{ p0 }", topLevel = false)
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 1)
	public Factor factorFromExpressionInBraces(Expression expression) {
		Factor factor = new Factor(expression);
		// Category factorCategory = new Category(factor.toString(), false);
		// Category leftP = new Category("{", true);
		// Category expressionCategory = new Category(expression.toString(), false);
		// Category rightP = new Category("}", true);
		//
		// de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(factorCategory, leftP, expressionCategory,
		// rightP);
		// this.grammar.addCategories(factorCategory, leftP, expressionCategory, rightP);
		// this.grammar.addRule(rule);

		return factor;
	}

	// @DSLMethod(prettyName = "p0")
	// @PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 1)
	// public Identifier identifierFromLetter(Letter letter) {
	// return new Identifier(letter);
	// }

	@DSLMethod(prettyName = "p0", topLevel = false)
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 1)
	public Identifier identifierFromLetters(@DSL(arrayDelimiter = "") LetterOrDigit[] letterOrDigit) {

		Identifier identifier = new Identifier(letterOrDigit);
		// Category identifierCategory = new Category(identifier.toString(), false);
		//
		// Category letterCategory = new Category(letterOrDigit.toString(), false);
		// de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(identifierCategory, letterCategory);
		// this.grammar.addCategories(identifierCategory, letterCategory);
		// this.grammar.addRule(rule);

		return identifier;
	}

	@DSLMethod(prettyName = "\" p0 \"", topLevel = false)
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 1)
	public QuotedSymbol quotedSymbolFromAnyCharacters(@DSL(arrayDelimiter = "") AnyCharacter[] ac) {
		QuotedSymbol quotedSymol = new QuotedSymbol(ac);
		// Category quotedSymbolCategory = new Category(quotedSymol.toString(), false);
		//
		// Category acCategory = new Category(ac.toString(), false);
		// de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(quotedSymbolCategory, acCategory);
		// this.grammar.addCategories(quotedSymbolCategory, acCategory);
		// this.grammar.addRule(rule);

		return quotedSymol;
	}

	private static AtomicInteger uuidCounter = new AtomicInteger();

	public static class Syntax implements Evaluable {

		private static final String RULES_CAGORY = "RULES";
		private Grammar grammar;
		private final de.tud.stg.popart.builder.test.dsls.BnfDSL.Rule[] rules;

		public Syntax(Rule[] rules) {
			this.rules = rules;
		}

		public void setGrammar(Grammar grammar) {
			this.grammar = grammar;
		}

		public Grammar getGrammar() {
			return this.grammar;
		}

		@Override
		public Category evaluate(Grammar grammar, HashMap<Identifier, Expression> mapping) {

			Category cat = new Category(CategoryNames.PROGRAM_CATEGORY, false);
			Category rules = new Category(RULES_CAGORY, false);

			de.tud.stg.parlex.core.Rule startRule = new de.tud.stg.parlex.core.Rule(cat, rules);
			grammar.addRule(startRule);

			for (Rule r : this.rules) {
				Category evaluate = r.evaluate(grammar, mapping);
				de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(rules, evaluate);
				grammar.addRule(rule);
			}

			return cat;
		}
	}

	public static class Rule implements Evaluable {

		private final Identifier lhs;
		private final Expression rhs;

		public Rule(Identifier lhs, Expression rhs) {
			this.lhs = lhs;
			this.rhs = rhs;
		}

		public Category evaluate(Grammar grammar, HashMap<Identifier, Expression> mapping) {

			Category identifierC = this.lhs.evaluate(grammar, mapping);
			Category expressionC = this.rhs.evaluate(grammar, mapping);

			de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(identifierC, expressionC);
			grammar.addRule(rule);

			// Category cat = new Category("RULE" + uuidCounter.getAndIncrement(), false);
			return identifierC;
		}

		@Override
		public String toString() {
			return this.lhs.toString();
		}
	}

	public static class Expression implements Evaluable {

		private final Term[] terms;

		public Expression(Term[] terms) {
			this.terms = terms;
		}

		@Override
		public Category evaluate(Grammar grammar, HashMap<Identifier, Expression> mapping) {

			Category[] children = new Category[this.terms.length];
			StringBuilder sb = new StringBuilder();
			int i = 0;

			for (Term t : this.terms) {
				children[i] = t.evaluate(grammar, mapping);
				sb.append(children[i].toString()).append('|');
				i++;
			}

			Category cat = new Category(sb.deleteCharAt(sb.length() - 1).toString(), false);

			for (Category c : children) {
				de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(cat, c);
				grammar.addRule(rule);
			}

			return cat;
		}
	}

	public static class Term implements Evaluable {

		private final Factor[] factors;

		public Term(Factor[] factors) {
			this.factors = factors;
		}

		@Override
		public Category evaluate(Grammar grammar, HashMap<Identifier, Expression> mapping) {

			Category[] c = new Category[this.factors.length];

			int i = 0;
			StringBuilder sb = new StringBuilder();
			for (Factor t : this.factors) {
				c[i] = t.evaluate(grammar, mapping);
				sb.append(c[i].getName()).append('&');
				i++;
			}

			Category cat = new Category(sb.deleteCharAt(sb.length() - 1).toString().toUpperCase(), false);

			de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(cat, c);
			grammar.addRule(rule);

			return cat;
		}
	}

	private static interface Evaluable {
		public Category evaluate(Grammar grammar, HashMap<Identifier, Expression> mapping);
	}

	public static class Factor implements Evaluable {

		private Evaluable e;

		public Factor(Expression expression) {
			this.e = expression;
		}

		public void setTerm(Term term) {
			this.e = term;
		}

		public Factor(QuotedSymbol quotedSymbol) {
			this.e = quotedSymbol;
		}

		public Factor(Identifier identifier) {
			this.e = identifier;
		}

		@Override
		public Category evaluate(Grammar grammar, HashMap<Identifier, Expression> mapping) {

			// Category cat = new Category("FACTOR" + uuidCounter.getAndIncrement(), false);

			Category to = this.e.evaluate(grammar, mapping);
			//
			// de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(cat, to);
			// grammar.addRule(rule);

			return to;
		}
	}

	public static class Identifier implements Evaluable {

		private String str = "";

		public Identifier(Letter letter, LetterOrDigit[] letterOrDigit) {

		}

		public Identifier(Letter letter) {
		}

		public Identifier(LetterOrDigit[] letterOrDigit) {
			for (LetterOrDigit l : letterOrDigit) {
				this.str += l.getRepresentation();
			}
		}

		@Override
		public String toString() {
			return this.str;
		}

		@Override
		public int hashCode() {
			return this.str.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Identifier) {
				Identifier i = (Identifier) obj;
				return this.str.equals(i.str);
			}
			return false;
		}

		@Override
		public Category evaluate(Grammar grammar, HashMap<Identifier, Expression> mapping) {
			return new Category(this.str, false);
		}
	}

	public static class Letter implements LetterOrDigit {

		private final String str;

		public Letter(String str) {
			this.str = str;
		}

		@Override
		public String getRepresentation() {
			return this.str;
		}

	}

	public static class Digit implements LetterOrDigit {

		private final String str;

		public Digit(String str) {
			this.str = str;
		}

		@Override
		public String getRepresentation() {
			return this.str;
		}

	}

	public static interface LetterOrDigit {
		String getRepresentation();
	}

	public static class QuotedSymbol implements Evaluable {

		private String str = "";

		public QuotedSymbol(AnyCharacter[] ac) {
			for (AnyCharacter a : ac) {
				this.str += a.getRepresentation();
			}
		}

		@Override
		public Category evaluate(Grammar grammar, HashMap<Identifier, Expression> mapping) {
			return new Category(this.str, true);
		}
	}

	public static class AnyCharacter {

		private final String str;

		public AnyCharacter(String str) {
			this.str = str;
		}

		public String getRepresentation() {
			return this.str;
		}
	}
}
