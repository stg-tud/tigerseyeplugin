package de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling;

import java.util.Map;

import de.tud.stg.parlex.core.Category;
import de.tud.stg.parlex.core.ICategory;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.core.Rule;
import de.tud.stg.parlex.core.groupcategories.StringCategory;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars.CategoryNames;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.utils.HandlingDispatcherHelper;

public class StringHandler implements ClassTypeHandler {


	@Override
	public ICategory<String> handle(IGrammar<String> grammar, Class<?> clazz, Map<ConfigurationOptions, String> parameterOptions) {
		ICategory<String> stringCategory = HandlingDispatcherHelper.getObjectHierarchy(grammar, String.class);

		String stringQuotation = parameterOptions.get(ConfigurationOptions.STRING_QUOTATION);

		Category group = new StringCategory(stringQuotation);

		grammar.addCategory(group);

		Rule rule1 = new Rule(stringCategory, group);

		grammar.addRule(rule1);

		Category rType = new Category(CategoryNames.TYPE_CATEGORY, false);
		Rule typeRule = new Rule(rType, stringCategory);
		grammar.addRule(typeRule);

		return stringCategory;
	}

}
