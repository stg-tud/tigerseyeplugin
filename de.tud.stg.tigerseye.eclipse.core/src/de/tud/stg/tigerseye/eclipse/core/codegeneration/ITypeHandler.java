package de.tud.stg.tigerseye.eclipse.core.codegeneration;

import java.lang.reflect.Type;
import java.util.Map;

import de.tud.stg.parlex.core.ICategory;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ConfigurationOptions;

/**
 * Interface to use instead implementation to ensure no further change.
 * 
 * @author Leo_Roos
 * 
 */
public interface ITypeHandler {

    ICategory<String> handle(Type type, Map<ConfigurationOptions, String> parameterOptions);

}
