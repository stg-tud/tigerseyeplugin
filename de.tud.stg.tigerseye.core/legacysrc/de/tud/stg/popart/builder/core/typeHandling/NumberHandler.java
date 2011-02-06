	package de.tud.stg.popart.builder.core.typeHandling;

import groovy.lang.Closure;

import java.util.Map;

import de.tud.stg.parlex.core.Category;
import de.tud.stg.parlex.core.ICategory;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.core.Rule;
import de.tud.stg.parlex.core.groupcategories.DoubleCategory;
import de.tud.stg.parlex.core.groupcategories.IntegerCategory;

public class NumberHandler implements ClassTypeHandler {

	@Override
	public ICategory<String> handle(IGrammar<String> grammar, Class<?> clazz, Map<String, String> parameterOptions) {
		if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
			return this.handleInteger(grammar, clazz, parameterOptions);
		} else if (clazz.equals(Double.class) || clazz.equals(double.class)) {
			return this.handleReal(grammar, clazz, parameterOptions);
		}

		return null;
	}

	private ICategory<String> handleInteger(IGrammar<String> grammar, Class<?> clazz,
			Map<String, String> parameterOptions) {
		ICategory<String> numberCategory = new HandlingDispatcher().getObjectHierarchy(grammar, Integer.class);

		Category group = new IntegerCategory();

		grammar.addCategories(group);

		Rule rule1 = new Rule(numberCategory, group);

		grammar.addRules(rule1);

		// Category rType = new Category("PTYPE", false);
		// Rule typeRule = new Rule(numberCategory, rType);
		// grammar.addRule(typeRule);

		return numberCategory;
	}

	private ICategory<String> handleReal(IGrammar<String> grammar, Class<?> clazz, Map<String, String> parameterOptions) {
		ICategory<String> doubleCategory = new HandlingDispatcher().getObjectHierarchy(grammar, Double.class);

		Category group = new DoubleCategory();
		Category floatCategory = new Category("Float", false);

		grammar.addCategories(group, floatCategory);

		Rule rule1 = new Rule(doubleCategory, group);
		Rule rule2 = new Rule(doubleCategory, floatCategory);

		grammar.addRules(rule1, rule2);

		Category rType = new Category("TYPE", false);
		Rule typeRule = new Rule(rType, doubleCategory);
		grammar.addRule(typeRule);

		return null;
	}
}
