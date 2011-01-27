package de.tud.stg.popart.builder.core.typeHandling;

import java.util.Map;

import de.tud.stg.parlex.core.Category;
import de.tud.stg.parlex.core.ICategory;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.core.Rule;
import de.tud.stg.parlex.core.groupcategories.CustomCategory;
import de.tud.stg.parlex.core.groupcategories.StringCategory;

public abstract class TypeHandler implements ClassTypeHandler {

	private final Category group;
	private ICategory<String> category;

	public TypeHandler() {
		this.group = new CustomCategory(this.getRegularExpression(), this.getMainType());
	}

	public void setGrammar(IGrammar<String> grammar) {
		this.category = new HandlingDispatcher().getExplicitObjectHierarchy(grammar, this.getMainType());
	}

	@Override
	public ICategory<String> handle(IGrammar<String> grammar, Class<?> clazz, Map<String, String> parameterOptions) {
		grammar.addCategories(this.group);

		Rule rule1 = new Rule(this.category, this.group);
		grammar.addRules(rule1);

		Category rType = new Category("TYPE", false);
		Rule typeRule = new Rule(rType, this.category);
		grammar.addRule(typeRule);

		return this.category;
	}

	public abstract Class<?> getMainType();

	public Class<?>[] getAdditionalTypes() {
		return null;
	}

	public abstract String getRegularExpression();
}
