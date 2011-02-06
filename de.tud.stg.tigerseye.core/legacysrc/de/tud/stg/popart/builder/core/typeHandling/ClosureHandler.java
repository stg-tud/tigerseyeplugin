package de.tud.stg.popart.builder.core.typeHandling;

import groovy.lang.Closure;

import java.util.Map;

import de.tud.stg.parlex.core.Category;
import de.tud.stg.parlex.core.ICategory;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.core.Rule;

public class ClosureHandler implements ClassTypeHandler {

	@Override
	public ICategory<String> handle(IGrammar<String> grammar, Class<?> clazz, Map<String, String> parameterOptions) {
		Category closure = new Category("Closure", false);

		Category statements = new Category("STATEMENTS", false);

		grammar.addCategory(closure);

		Rule rule = new Rule(closure, statements);
		grammar.addRule(rule);

		return closure;
	}

}
