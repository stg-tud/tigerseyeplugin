package de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling;

import java.util.Map;

import de.tud.stg.parlex.core.ICategory;
import de.tud.stg.parlex.core.IGrammar;

public interface ClassTypeHandler {
	ICategory<String> handle(IGrammar<String> grammar, Class<?> clazz, Map<ConfigurationOptions, String> parameterOptions);
}
