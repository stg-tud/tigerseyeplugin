package de.tud.stg.tigerseye.eclipse.core.codegeneration.utils;

import static de.tud.stg.tigerseye.util.ListBuilder.newList;
import de.tud.stg.parlex.core.Category;
import de.tud.stg.parlex.core.ICategory;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.core.Rule;
import de.tud.stg.parlex.core.groupcategories.StringCategory;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars.CategoryNames;

/**
 * A class containing the definition of the whitespace category.
 * 
 * @author Leo Roos
 * 
 */
public class WhitespaceCategoryDefinition {

	public static ICategory<String> getNewOptionalWhitespaceCategory() {
		return new Category(CategoryNames.OWHITESPACES_CATGORY, false);
	}

	public static ICategory<String> getNewRequiredWhitespaceCategory() {
		return new Category(CategoryNames.RWHITESPACES_CATEGORY, false);
	}

	public static void setWhitspaceRules(IGrammar<String> grammar) {
		setOptionalWhitespaceRule(grammar);
		setRequiredWhitespaceRule(grammar);
	}

	private static void setRequiredWhitespaceRule(IGrammar<String> grammar) {
		ICategory<String> requiredWhitespaces = new StringCategory("\\s+");
		ICategory<String> RWSS = getNewRequiredWhitespaceCategory();
		ICategory<String> OWSS = getNewOptionalWhitespaceCategory();
		Rule r3 = new Rule(RWSS, newList(requiredWhitespaces).add(OWSS)
				.toList());
		Rule r4 = new Rule(RWSS, requiredWhitespaces);
		grammar.addRule(r3);
		grammar.addRule(r4);
	}

	private static void setOptionalWhitespaceRule(IGrammar<String> grammar) {
		ICategory<String> optionalWhitespaces = new StringCategory("\\s*");
		ICategory<String> OWSS = getNewOptionalWhitespaceCategory();
		Rule r1 = new Rule(OWSS, newList(optionalWhitespaces).add(OWSS)
				.toList());
		Rule r2 = new Rule(OWSS, optionalWhitespaces);
		grammar.addRule(r1);
		grammar.addRule(r2);
	}

	/**
	 * Whitespace defined by the regular expression {@code \s*}
	 */
	public static ICategory<String> getAndSetOptionalWhitespace(
			IGrammar<String> grammar) {
		setWhitspaceRules(grammar);
		return getNewOptionalWhitespaceCategory();
	}

	/**
	 * Whitespace defined by the regular expression {@code \s+}
	 */
	public static ICategory<String> getAndSetRequiredWhitespace(
			IGrammar<String> grammar) {
		setWhitspaceRules(grammar);
		return getNewRequiredWhitespaceCategory();
	}

}
