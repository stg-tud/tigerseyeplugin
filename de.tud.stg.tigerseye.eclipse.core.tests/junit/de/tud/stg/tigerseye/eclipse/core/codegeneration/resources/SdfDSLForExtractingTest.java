package de.tud.stg.tigerseye.eclipse.core.codegeneration.resources;

import groovy.lang.Closure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import de.tud.stg.parlex.core.Grammar;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.popart.builder.core.annotations.DSLParameter;
import de.tud.stg.popart.builder.core.annotations.DSLClass;
import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.builder.core.annotations.DSLParameter;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars.HostLanguageGrammar;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.TypeHandler;

@DSLClass(whitespaceEscape = " ",//
		typeRules = { //
		SdfDSLForExtractingTest.SortSymbolType.class,//
		SdfDSLForExtractingTest.ModuleIdType.class,//
		SdfDSLForExtractingTest.CharacterClassSymbolType.class,//		
		SdfDSLForExtractingTest.CaseInsensitiveLiteralSymbolType.class },// 
		hostLanguageRules = { //
		SdfDSLForExtractingTest.MyHostLanguage.class })
public class SdfDSLForExtractingTest implements de.tud.stg.popart.dslsupport.DSL {

	public static class MyHostLanguage implements HostLanguageGrammar {

		@Override
		public void applySpecificGrammar(IGrammar<String> grammar) {
		}

	}

	/**
	 * Transforms the module with the given name into a parlex grammar,
	 * processing SDF macros, imports and renamings.
	 * 
	 * <p>
	 * The grammar can also be cleaned, i.e. unused rules are removed.
	 * 
	 * @param topLevelModuleName
	 *            name of the top-level module
	 * @param cleanGrammar
	 *            if true, unused rules are removed from the generated grammar.
	 * @return the generated Grammar for the given Module
	 */
	public Grammar getGrammar(String topLevelModuleName, boolean cleanGrammar) {
		return null;
	}

	public Object eval(@SuppressWarnings("rawtypes") HashMap map, @SuppressWarnings("rawtypes") Closure cl) {
		cl.setDelegate(this);
		cl.setResolveStrategy(Closure.DELEGATE_FIRST);
		return cl.call();
	}

	// // TOP LEVEL ELEMENTS ////

	// created (and used in other classes
	// such as the converters instead of SdfDSL), or remove the Definition
	// class. TBD.

	// required for each case):
	// - modules with only imports but no exports/hiddens (cases for module with
	// and without parameters)
	// - modules with neither imports nor exports (makes no sense, but still
	// legal)

	// module p0 p1 p2 (imports, no parameters)
	@DSLMethod(production = "module  p0  p1  p2", topLevel = true)
	public Module moduleWithoutParameters(ModuleId name, @DSLParameter(arrayDelimiter = " ") Imports[] imports,
			@DSLParameter(arrayDelimiter = " ") ExportOrHiddenSection[] exportOrHiddenSections) {
		return null;
	}


	// module p0[p1] p2 p3
	@DSLMethod(production = "module  p0 [ p1 ]  p2  p3")
	public Module moduleWithParameters(ModuleId name, @DSLParameter(arrayDelimiter = ",") Symbol[] params,
			@DSLParameter(arrayDelimiter = " ") Imports[] imports,
			@DSLParameter(arrayDelimiter = " ") ExportOrHiddenSection[] exportOrHiddenSections) {
		return null;
	}

	// for testing purposes

	@DSLMethod(production = "parse  p0  p1")
	public boolean parseString(String topLevelModule, String input) {
		return false;
	}

	@DSLMethod(production = "printGeneratedGrammarHTML  p0  p1")
	public void printGeneratedGrammarHTML(String topLevelModule, String fileName) {
	}

	// // SYMBOLS ////

	// "p0"
	@DSLMethod(production = "p0", topLevel = false)
	public LiteralSymbol caseSensitiveLiteralSymbol(String text) {
		return new LiteralSymbol(text, true);
	}

	// 'p0'
	@DSLMethod(production = "p0", topLevel = false)
	public LiteralSymbol caseInsensitiveLiteralSymbol(CaseInsensitiveLiteralSymbol sym) {
		return null;
	}

	// convenience method for manual tests
	public LiteralSymbol caseInsensitiveLiteralSymbol(String str) {
		return null;
	}

	// p0
	// convenience method for manual tests
	// @DSLMethod(production = "p0", topLevel = false)
	public SortSymbol sortSymbol(String name) {
		return new SortSymbol(name);
	}

	// [p0]
	// convenience method for manual tests
	// @DSLMethod(production = "[p0]", topLevel = false)
	//
	public CharacterClassSymbol characterClassSymbol(String pattern) {
		return new CharacterClassSymbol(pattern);
	}

	// ~p0
	@DSLMethod(production = "~ p0", topLevel = false)
	public CharacterClassComplement characterClassComplement(CharacterClassSymbol sym) {
		return new CharacterClassComplement(sym);
	}

	// p0/p1
	@DSLMethod(production = "p0 / p1", topLevel = false)
	public CharacterClassDifference characterClassDifference(CharacterClassSymbol left, CharacterClassSymbol right) {
		return new CharacterClassDifference(left, right);
	}

	// p0/\p1
	@DSLMethod(production = "p0 /\\ p1", topLevel = false)
	public CharacterClassIntersection characterClassIntersection(CharacterClassSymbol left, CharacterClassSymbol right) {
		return new CharacterClassIntersection(left, right);
	}

	// p0\/p1
	@DSLMethod(production = "p0 \\/ p1", topLevel = false)
	public CharacterClassUnion characterClassUnion(CharacterClassSymbol left, CharacterClassSymbol right) {
		return new CharacterClassUnion(left, right);
	}

	// p0?
	@DSLMethod(production = "p0 ?", topLevel = false)
	public OptionalSymbol optionalSymbol(Symbol symbol) {
		return new OptionalSymbol(symbol);
	}

	// p0*
	@DSLMethod(production = "p0 *", topLevel = false)
	public RepetitionSymbol repetitionSymbolAtLeastZero(Symbol symbol) {
		return new RepetitionSymbol(symbol, false);
	}

	// p0+
	@DSLMethod(production = "p0 +", topLevel = false)
	public RepetitionSymbol repetitionSymbolAtLeastOnce(Symbol symbol) {
		return new RepetitionSymbol(symbol, true);
	}

	// (p0)
	@DSLMethod(production = "( p0 )", topLevel = false)
	public SequenceSymbol sequenceSymbol(@DSLParameter(arrayDelimiter = " ") Symbol[] symbols) {
		return new SequenceSymbol(new ArrayList<Symbol>(Arrays.asList(symbols)));
	}

	// {p0 p1}*
	@DSLMethod(production = "{ p0 p1 } *", topLevel = false)
	public ListSymbol listSymbolAtLeastZero(Symbol element, Symbol seperator) {
		return new ListSymbol(element, seperator, false);
	}

	// {p0 p1}+
	@DSLMethod(production = "{ p0 p1 } +", topLevel = false)
	public ListSymbol listSymbolAtLeastOnce(Symbol element, Symbol seperator) {
		return new ListSymbol(element, seperator, true);
	}

	// p0 | p1
	@DSLMethod(production = "p0 | p1", topLevel = false)
	public AlternativeSymbol alternativeSymbol(Symbol left, Symbol right) {
		return new AlternativeSymbol(left, right);
	}

	// <p0>
	@DSLMethod(production = "< p0 >", topLevel = false)
	public TupleSymbol tupleSymbol(@DSLParameter(arrayDelimiter = ",") Symbol[] symbol) {
		return new TupleSymbol(new ArrayList<Symbol>(Arrays.asList(symbol)));
	}

	// (p0 => p1)
	@DSLMethod(production = "( p0 => p1 )", topLevel = false)
	public FunctionSymbol functionSymbol(@DSLParameter(arrayDelimiter = " ") Symbol[] left, Symbol right) {
		return new FunctionSymbol(new ArrayList<Symbol>(Arrays.asList(left)), right);
	}

	// p1:p0
	public Symbol labelledSymbol(Symbol sym, String label) {
		return null;
	}

	// Methods to convert symbol subclasses to symbol

	@DSLMethod(production = "p0", topLevel = false)
	public Symbol symbol(SortSymbol s) {
		return null;
	}

	@DSLMethod(production = "p0", topLevel = false)
	public Symbol symbol(LiteralSymbol s) {
		return null;
	}

	@DSLMethod(production = "p0", topLevel = false)
	public Symbol symbol(AlternativeSymbol s) {
		return null;
	}

	@DSLMethod(production = "p0", topLevel = false)
	public Symbol symbol(ListSymbol s) {
		return null;
	}

	@DSLMethod(production = "p0", topLevel = false)
	public Symbol symbol(OptionalSymbol s) {
		return null;
	}

	@DSLMethod(production = "p0", topLevel = false)
	public Symbol symbol(RepetitionSymbol s) {
		return null;
	}

	@DSLMethod(production = "p0", topLevel = false)
	public Symbol symbol(SequenceSymbol s) {
		return null;
	}

	@DSLMethod(production = "p0", topLevel = false)
	public Symbol symbol(CharacterClass s) {
		return null;
	}

	@DSLMethod(production = "p0", topLevel = false)
	public CharacterClass characterClass(CharacterClassSymbol s) {
		return null;
	}

	@DSLMethod(production = "p0", topLevel = false)
	public CharacterClass characterClass(CharacterClassComplement s) {
		return null;
	}

	@DSLMethod(production = "p0", topLevel = false)
	public CharacterClass characterClass(CharacterClassDifference s) {
		return null;
	}

	@DSLMethod(production = "p0", topLevel = false)
	public CharacterClass characterClass(CharacterClassIntersection s) {
		return null;
	}

	@DSLMethod(production = "p0", topLevel = false)
	public CharacterClass characterClass(CharacterClassUnion s) {
		return null;
	}

	@DSLMethod(production = "p0", topLevel = false)
	public Symbol symbol(TupleSymbol s) {
		return null;
	}

	@DSLMethod(production = "p0", topLevel = false)
	public Symbol symbol(FunctionSymbol s) {
		return null;
	}

	// // MODULE LEVEL /////

	@DSLMethod(production = "exports  p0", topLevel = false)
	public Exports exports(@DSLParameter(arrayDelimiter = " ") GrammarElement[] grammarElements) {
		return new Exports(new ArrayList<GrammarElement>(Arrays.asList(grammarElements)));
	}

	@DSLMethod(production = "hiddens  p0", topLevel = false)
	public Hiddens hiddens(@DSLParameter(arrayDelimiter = " ") GrammarElement[] grammarElements) {
		return new Hiddens(new ArrayList<GrammarElement>(Arrays.asList(grammarElements)));
	}

	// Methods to convert Exports/Hiddens to ExportOrHiddenSection

	@DSLMethod(production = "p0", topLevel = false)
	public ExportOrHiddenSection exportOrHiddenSection(Exports e) {
		return null;
	}

	@DSLMethod(production = "p0", topLevel = false)
	public ExportOrHiddenSection exportOrHiddenSection(Hiddens e) {
		return null;
	}

	// // GRAMMAR ELEMENTS ////

	// imports p0
	@DSLMethod(production = "imports  p0", topLevel = false)
	public Imports importsStatement(@DSLParameter(arrayDelimiter = " ") Import[] importList) {
		return new Imports(new ArrayList<Import>(Arrays.asList(importList)));
	}

	// p0
	@DSLMethod(production = "p0", topLevel = false)
	public Import importModuleWithoutParameters(ModuleId moduleName) {
		return new Import(moduleName.toString());
	}

	// p0[p1]
	@DSLMethod(production = "p0 [ p1 ]", topLevel = false)
	public Import importModuleWithParameters(ModuleId moduleName, @DSLParameter(arrayDelimiter = ",") Symbol[] params) {
		return new Import(moduleName.toString(), new ArrayList<Symbol>(Arrays.asList(params)));
	}

	// p0[p1]
	@DSLMethod(production = "p0 [ p1 ]", topLevel = false)
	public Import importModuleWithRenamings(ModuleId moduleName, @DSLParameter(arrayDelimiter = ",") Renaming[] renamings) {
		return new Import(moduleName.toString(), new ArrayList<Symbol>(), new ArrayList<Renaming>(
				Arrays.asList(renamings)));
	}

	// p0[p1][p2]
	@DSLMethod(production = "p0 [ p1 ] [ p2 ]", topLevel = false)
	public Import importModuleWithParametersAndRenamings(ModuleId moduleName,
			@DSLParameter(arrayDelimiter = ",") Symbol[] params, @DSLParameter(arrayDelimiter = ",") Renaming[] renamings) {
		return new Import(moduleName.toString(), new ArrayList<Symbol>(Arrays.asList(params)), new ArrayList<Renaming>(
				Arrays.asList(renamings)));
	}

	// p0 => p1
	@DSLMethod(production = "p0 => p1", topLevel = false)
	public Renaming renaming(Symbol oldSymbol, Symbol newSymbol) {
		return new Renaming(oldSymbol, newSymbol);
	}

	// sorts p0
	@DSLMethod(production = "sorts  p0", topLevel = false)
	public Sorts sortsDeclaration(@DSLParameter(arrayDelimiter = " ") SortSymbol[] sortSymbols) {
		return new Sorts(new ArrayList<SortSymbol>(Arrays.asList(sortSymbols)));
	}

	// lexical syntax p0
	@DSLMethod(production = "lexical  syntax  p0", topLevel = false)
	public LexicalSyntax lexicalSyntax(@DSLParameter(arrayDelimiter = " ") Production[] productions) {
		return new LexicalSyntax(new ArrayList<Production>(Arrays.asList(productions)));
	}

	// context-free syntax p0
	@DSLMethod(production = "context-free  syntax  p0", topLevel = false)
	public ContextFreeSyntax contextFreeSyntax(@DSLParameter(arrayDelimiter = " ") Production[] productions) {
		return new ContextFreeSyntax(new ArrayList<Production>(Arrays.asList(productions)));
	}

	// lexical start-symbols p0
	@DSLMethod(production = "lexical  start-symbols  p0", topLevel = false)
	public LexicalStartSymbols lexicalStartSymbols(@DSLParameter(arrayDelimiter = " ") Symbol[] symbols) {
		return new LexicalStartSymbols(new ArrayList<Symbol>(Arrays.asList(symbols)));
	}

	// context-free start-symbols p0
	@DSLMethod(production = "context-free  start-symbols  p0", topLevel = false)
	public ContextFreeStartSymbols contextFreeStartSymbols(@DSLParameter(arrayDelimiter = " ") Symbol[] symbols) {
		return new ContextFreeStartSymbols(new ArrayList<Symbol>(Arrays.asList(symbols)));
	}

	// aliases p0
	@DSLMethod(production = "aliases  p0", topLevel = false)
	public Aliases aliases(@DSLParameter(arrayDelimiter = " ") Alias[] aliases) {
		return new Aliases(new ArrayList<Alias>(Arrays.asList(aliases)));
	}

	// p0 -> p1 (alias)
	@DSLMethod(production = "p0 -> p1", topLevel = false)
	public Alias alias(Symbol original, Symbol aliasName) {
		return new Alias(original, aliasName);
	}

	// p0 -> p1 (production)
	@DSLMethod(production = "p0 -> p1", topLevel = false)
	public Production production(@DSLParameter(arrayDelimiter = " ") Symbol[] lhs, Symbol rhs) {
		return new Production(new ArrayList<Symbol>(Arrays.asList(lhs)), rhs);
	}

	// -> p0 (production with empty LHS)
	@DSLMethod(production = " -> p0", topLevel = false)
	public Production production(Symbol rhs, String something) {
		return new Production(new ArrayList<Symbol>(), rhs);
	}

	// Methods to convert grammar elements to GrammarElement

	@DSLMethod(production = "p0", topLevel = false)
	public GrammarElement grammarElement(Imports e) {
		return null;
	}

	@DSLMethod(production = "p0", topLevel = false)
	public GrammarElement grammarElement(Sorts e) {
		return null;
	}

	@DSLMethod(production = "p0", topLevel = false)
	public GrammarElement grammarElement(StartSymbols e) {
		return null;
	}

	@DSLMethod(production = "p0", topLevel = false)
	public GrammarElement grammarElement(Syntax e) {
		return null;
	}

	@DSLMethod(production = "p0", topLevel = false)
	public StartSymbols startSymbols(ContextFreeStartSymbols e) {
		return null;
	}

	@DSLMethod(production = "p0", topLevel = false)
	public StartSymbols startSymbols(LexicalStartSymbols e) {
		return null;
	}

	@DSLMethod(production = "p0", topLevel = false)
	public Syntax syntax(ContextFreeSyntax e) {
		return null;
	}

	@DSLMethod(production = "p0", topLevel = false)
	public Syntax syntax(LexicalSyntax e) {
		return null;
	}

	@DSLMethod(production = "p0", topLevel = false)
	public GrammarElement syntax(Aliases e) {
		return null;
	}

	// // For Testing Stubbed classes
	public static class Alias {

		public Alias(Symbol original, Symbol aliasName) {
		}
	}

	public static class Aliases {

		public Aliases(ArrayList<Alias> arrayList) {

		}
	}

	public static class AlternativeSymbol {

		public AlternativeSymbol(Symbol left, Symbol right) {
		}
	}

	public static class CaseInsensitiveLiteralSymbol {
	}

	public static class CharacterClass {
	}

	public static class CharacterClassComplement {

		public CharacterClassComplement(CharacterClassSymbol sym) {
		}
	}

	public static class CharacterClassDifference {

		public CharacterClassDifference(CharacterClassSymbol left, CharacterClassSymbol right) {
		}
	}

	public static class CharacterClassIntersection {

		public CharacterClassIntersection(CharacterClassSymbol left, CharacterClassSymbol right) {
		}
	}

	public static class CharacterClassSymbol {

		public CharacterClassSymbol(String pattern) {
		}
	}

	public static class CharacterClassUnion {

		public CharacterClassUnion(CharacterClassSymbol left, CharacterClassSymbol right) {
		}
	}

	public static class ContextFreeStartSymbols {

		public ContextFreeStartSymbols(ArrayList<Symbol> arrayList) {
		}
	}

	public static class ContextFreeSyntax {

		public ContextFreeSyntax(ArrayList<Production> arrayList) {
		}
	}

	public static class Definition {
	}

	public static class ExportOrHiddenSection {
	}

	public static class Exports {

		public Exports(ArrayList<GrammarElement> arrayList) {
		}
	}

	public static class FunctionSymbol {

		public FunctionSymbol(ArrayList<Symbol> arrayList, Symbol right) {
		}
	}

	public static class GrammarElement {
	}

	public static class Hiddens {

		public Hiddens(ArrayList<GrammarElement> arrayList) {
		}
	}

	public static class Import {

		public Import(String string) {
		}

		public Import(String string, ArrayList<Symbol> arrayList) {
		}

		public Import(String string, ArrayList<Symbol> arrayList, ArrayList<Renaming> arrayList2) {
		}
	}

	public static class Imports {

		public Imports(ArrayList<Import> arrayList) {
		}
	}

	public static class LexicalStartSymbols {

		public LexicalStartSymbols(ArrayList<Symbol> arrayList) {
		}
	}

	public static class LexicalSyntax {

		public LexicalSyntax(ArrayList<Production> arrayList) {
		}
	}

	public static class ListSymbol {

		public ListSymbol(Symbol element, Symbol seperator, boolean b) {
		}
	}

	public static class LiteralSymbol {

		public LiteralSymbol(String text, boolean b) {
		}
	}

	public static class Module {

		public void setImportSections(ArrayList<Imports> arrayList) {

		}
	}

	public static class ModuleId {
	}

	public static class OptionalSymbol {

		public OptionalSymbol(Symbol symbol) {
		}
	}

	public static class Production {

		public Production(ArrayList<Symbol> arrayList, Symbol rhs) {
		}
	}

	public static class Renaming {

		public Renaming(Symbol oldSymbol, Symbol newSymbol) {
		}
	}

	public static class RepetitionSymbol {

		public RepetitionSymbol(Symbol symbol, boolean b) {
		}
	}

	public static class SdfElement {
	}

	public static class SequenceSymbol {

		public SequenceSymbol(ArrayList<Symbol> arrayList) {
		}
	}

	public static class Sorts {

		public Sorts(ArrayList<SortSymbol> arrayList) {
		}
	}

	public static class SortSymbol {

		public SortSymbol(String name) {
		}
	}

	public static class StartSymbols {
	}

	public static class Symbol {
	}

	public static class Syntax {
	}

	public static class TupleSymbol {

		public TupleSymbol(ArrayList<Symbol> arrayList) {
		}
	}

	public static class Visitor {
	}

	// // TYPE HANDLERS ////

	/**
	 * A sort corresponds to a non-terminal, e.g., Bool. Sort names always start
	 * with a capital letter and may be followed by letters and/or digits.
	 * Hyphens (-) may be embedded in a sort name.
	 * <p>
	 * Parameterized sort names (TO-DO):
	 * {@code <Sort>[[<Symbol1>, <Symbol2>, ... ]]}
	 * 
	 */
	public static class SortSymbolType extends TypeHandler {

		@Override
		public Class<?> getMainType() {
			return SortSymbol.class;
		}

		@Override
		public String getRegularExpression() {
			return "([A-Z][-A-Za-z0-9]*)";
		}

	}

	/**
	 * A module name consists of letters, numbers, hyphens and underscores,
	 * potentionally seperated by slashes (like a path name).
	 * 
	 * @author Pablo Hoch
	 * 
	 */
	public static class ModuleIdType extends TypeHandler {

		@Override
		public Class<?> getMainType() {
			return ModuleId.class;
		}

		@Override
		public String getRegularExpression() {
			return "(/?([-_A-Za-z0-9]+)(/[-_A-Za-z0-9]+)*)";
		}

	}

	public static class CharacterClassSymbolType extends TypeHandler {

		@Override
		public Class<?> getMainType() {
			return CharacterClassSymbol.class;
		}

		@Override
		public String getRegularExpression() {
			return "\\[([^\\]]+)\\]";
		}

	}

	public static class CaseInsensitiveLiteralSymbolType extends TypeHandler {

		@Override
		public Class<?> getMainType() {
			return CaseInsensitiveLiteralSymbol.class;
		}

		@Override
		public String getRegularExpression() {
			return "'(.*?)'";
		}

	}
}
