package de.tud.stg.popart.builder.core.typeHandling;

import java.util.Map;

import de.tud.stg.parlex.core.Category;
import de.tud.stg.parlex.core.ICategory;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.core.Rule;
import de.tud.stg.parlex.core.groupcategories.StringCategory;

public class StringHandler implements ClassTypeHandler {

	@Override
	public ICategory<String> handle(IGrammar<String> grammar, Class<?> clazz, Map<String, String> parameterOptions) {
		ICategory<String> stringCategory = new HandlingDispatcher().getObjectHierarchy(grammar, String.class);

		String stringQuotation = parameterOptions.get("stringQuotation");

		Category group = new StringCategory(stringQuotation);

		grammar.addCategories(group);

		Rule rule1 = new Rule(stringCategory, group);

		grammar.addRules(rule1);

		Category rType = new Category("TYPE", false);
		Rule typeRule = new Rule(rType, stringCategory);
		grammar.addRule(typeRule);

		return stringCategory;
	}

}
