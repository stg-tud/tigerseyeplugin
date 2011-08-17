package de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling;

import groovy.lang.Closure;

import java.util.Map;

import de.tud.stg.parlex.core.Category;
import de.tud.stg.parlex.core.ICategory;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.core.Rule;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars.CategoryNames;

public class ClosureHandler implements ClassTypeHandler {

	private static final String CLOSURE_CATEGORY = "Closure";

	@Override
	public ICategory<String> handle(IGrammar<String> grammar, Class<?> clazz, Map<ParameterOptions, String> parameterOptions) {
		Category closure = new Category(CLOSURE_CATEGORY, false);

		Category statements = new Category(CategoryNames.STATEMENTS_CATEGORY, false);

		grammar.addCategory(closure);

		Rule rule = new Rule(closure, statements);
		grammar.addRule(rule);

		return closure;
	}

}
