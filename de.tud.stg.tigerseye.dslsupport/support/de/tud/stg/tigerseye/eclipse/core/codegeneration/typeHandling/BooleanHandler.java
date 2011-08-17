package de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling;

import java.util.Map;

import de.tud.stg.parlex.core.Category;
import de.tud.stg.parlex.core.ICategory;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.core.Rule;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars.CategoryNames;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.utils.HandlingDispatcherHelper;

public class BooleanHandler implements ClassTypeHandler {

	@Override
	public ICategory<String> handle(IGrammar<String> grammar, Class<?> clazz, Map<ParameterOptions, String> parameterOptions) {
		ICategory<String> booleanCategory = HandlingDispatcherHelper.getObjectHierarchy(grammar, Boolean.class);

		Category trueCategory = new Category("true", true);
		Category falseCategory = new Category("false", true);

		grammar.addCategories(trueCategory, falseCategory);

		Rule rule1 = new Rule(booleanCategory, trueCategory);
		Rule rule2 = new Rule(booleanCategory, falseCategory);

		grammar.addRules(rule1, rule2);

		// Category objectCategory = new Category("Object", false);
		//
		// Rule rule3 = new Rule(booleanCategory, objectCategory, new
		// Category("==", true), objectCategory);
		// Rule rule4 = new Rule(booleanCategory, objectCategory, new
		// Category(">=", true), objectCategory);
		// Rule rule5 = new Rule(booleanCategory, objectCategory, new
		// Category("<=", true), objectCategory);
		// Rule rule6 = new Rule(booleanCategory, objectCategory, new
		// Category(">", true), objectCategory);
		// Rule rule7 = new Rule(booleanCategory, objectCategory, new
		// Category("<", true), objectCategory);
		// Rule rule8 = new Rule(booleanCategory, objectCategory, new
		// Category("!=", true), objectCategory);
		//
		// Category pType = new Category(CategoryNames.PTYPE_CATEGORY, false);
		// Rule rule9 = new Rule(pType, objectCategory);
		//
		// grammar.addRules(rule3, rule4, rule5, rule6, rule7, rule8, rule9);

		Category rType = new Category(CategoryNames.TYPE_CATEGORY, false);
		Rule typeRule = new Rule(rType, booleanCategory);
		grammar.addRule(typeRule);

		return booleanCategory;
	}

}
