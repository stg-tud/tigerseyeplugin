package de.tud.stg.popart.builder.core.typeHandling;

import java.util.Map;

import de.tud.stg.parlex.core.ICategory;
import de.tud.stg.parlex.core.IGrammar;

public interface ClassTypeHandler {
	ICategory<String> handle(IGrammar<String> grammar, Class<?> clazz, Map<String, String> parameterOptions);
}
