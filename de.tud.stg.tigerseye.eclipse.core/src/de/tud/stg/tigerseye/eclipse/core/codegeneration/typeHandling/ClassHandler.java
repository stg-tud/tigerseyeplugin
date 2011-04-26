package de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling;

import java.util.Map;

import de.tud.stg.parlex.core.ICategory;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.core.groupcategories.ClassCategory;

public class ClassHandler implements ClassTypeHandler {

	@Override
	public ICategory<String> handle(IGrammar<String> grammar, Class<?> clazz, Map<String, String> parameterOptions) {
		return new ClassCategory();
	}

}
