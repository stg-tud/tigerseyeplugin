package de.tud.stg.tigerseye.eclipse.core.codegeneration.utils;

import static de.tud.stg.tigerseye.util.ListBuilder.newList;
import de.tud.stg.parlex.core.Category;
import de.tud.stg.parlex.core.ICategory;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.core.Rule;
import de.tud.stg.parlex.core.groupcategories.StringCategory;

/**
 * A class containing the definition of the whitespace category. 
 * 
 * @author Leo Roos
 * 
 */
public class WhitespaceCategoryDefinition {

	private static ICategory<String> getWhitespaceCategory(
			IGrammar<String> grammar, boolean optional) {

		ICategory<String> optionalWhitespaces = new StringCategory("\\s*");
		/*XXX(Leo_Roos;Aug 27, 2011) why is it not OWSS -> "" and OWSS -> " " . OWSS*/
		

		// ICategory<String> OWS = new Category("OWHITESPACE", false);//XXX
		// never read
		ICategory<String> OWSS = new Category("OWHITESPACES", false);

		Rule r1 = new Rule(OWSS, newList(optionalWhitespaces)
				.add(OWSS).toList());
		Rule r2 = new Rule(OWSS, optionalWhitespaces);

		grammar.addRule(r1);
		grammar.addRule(r2);

		ICategory<String> requiredWhitespaces = new StringCategory("\\s+");

//		 ICategory<String> RWS = new Category("RWHITESPACE", false); //XXX
		// Never read
		ICategory<String> RWSS = new Category("RWHITESPACES", false);

		Rule r3 = new Rule(RWSS, newList(requiredWhitespaces)
				.add(OWSS).toList());
		Rule r4 = new Rule(RWSS, requiredWhitespaces);

		grammar.addRule(r3);
		grammar.addRule(r4);

		if (optional) {
			return OWSS;
		} else {
			return RWSS;
		}
	}

	/**
	 * Whitespace defined by the regular expression {@code \s*}
	 */
	public static ICategory<String> getAndSetOptionalWhitespace(
			IGrammar<String> grammar) {
		return getWhitespaceCategory(grammar, true);
	}

	/**
	 * Whitespace defined by the regular expression {@code \s+}
	 */
	public static ICategory<String> getAndSetRequiredWhitespace(IGrammar<String> grammar) {
		return getWhitespaceCategory(grammar, false);
	}

}
