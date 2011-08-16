package de.tud.stg.tigerseye.eclipse.core.codegeneration.utils;

import de.tud.stg.parlex.core.Category;
import de.tud.stg.parlex.core.ICategory;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.core.Rule;
import de.tud.stg.parlex.core.groupcategories.StringCategory;
import de.tud.stg.tigerseye.utils.ListBuilder;

/**
 * A class containing extracted methods from
 * {@link de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarBuilder} which
 * seems to be utility methods and not class specific
 * 
 * @author Leo Roos
 * 
 */
public class GrammarBuilderHelper {

	private static ICategory<String> getWhitespaceCategory(
			IGrammar<String> grammar, boolean optional) {

		ICategory<String> optionalWhitespaces = new StringCategory("\\s*");

		// ICategory<String> OWS = new Category("OWHITESPACE", false);//XXX
		// never read
		ICategory<String> OWSS = new Category("OWHITESPACES", false);

		Rule r1 = new Rule(OWSS, ListBuilder.begin(optionalWhitespaces)
				.add(OWSS).toList());
		Rule r2 = new Rule(OWSS, ListBuilder.single(optionalWhitespaces));

		grammar.addRule(r1);
		grammar.addRule(r2);

		ICategory<String> requiredWhitespaces = new StringCategory("\\s+");

		// ICategory<String> RWS = new Category("RWHITESPACE", false); //XXX
		// Never read
		ICategory<String> RWSS = new Category("RWHITESPACES", false);

		Rule r3 = new Rule(RWSS, ListBuilder.begin(requiredWhitespaces)
				.add(OWSS).toList());
		Rule r4 = new Rule(RWSS, ListBuilder.single(requiredWhitespaces));

		grammar.addRule(r3);
		grammar.addRule(r4);

		if (optional) {
			return OWSS;
		} else {
			return RWSS;
		}
	}

	public static ICategory<String> getOptionalWhitespace(
			IGrammar<String> grammar) {
		return getWhitespaceCategory(grammar, true);
	}

	public static ICategory<String> getRWhitespace(IGrammar<String> grammar) {
		return getWhitespaceCategory(grammar, false);
	}

}
