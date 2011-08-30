package de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import de.tud.stg.popart.builder.core.annotations.DSLClass;
import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.builder.core.annotations.DSLParameter;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ConfigurationOptions;

/**
 * Extracts the DSL information
 * 
 * @author Leo_Roos
 * 
 */
public class ExtractorDefaults {

    private ExtractorDefaults() {
	// utility class
    }

    static class DefaultedAnnotation implements InvocationHandler {
	@SuppressWarnings("unchecked")
	public static <A extends Annotation> A of(Class<A> annotation) {
	    return (A) Proxy.newProxyInstance(annotation.getClassLoader(),
		    new Class[] { annotation }, new DefaultedAnnotation());
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable {
	    return method.getDefaultValue();
	}
    }

    static final DSLClass DEFAULT_DSLClass = DefaultedAnnotation
	    .of(DSLClass.class);

    static final DSLMethod DEFAULT_DSLMethod = DefaultedAnnotation
	    .of(DSLMethod.class);

    static final DSLParameter DEFAULT_DSLParameter = DefaultedAnnotation
	    .of(DSLParameter.class);

    public static final Map<ConfigurationOptions, String> DEFAULT_CONFIGURATIONOPTIONS_MAP = getDefaultOptions();

    private static Map<ConfigurationOptions, String> getDefaultOptions() {
	Map<ConfigurationOptions, String> defaultOptions = new HashMap<ConfigurationOptions, String>();
	for (ConfigurationOptions parop : ConfigurationOptions.values()) {
	    defaultOptions.put(parop, parop.defaultValue);
	}
	return defaultOptions;
    }

    // private static Map<ConfigurationOptions, String>
    // getAnnotationParameterOptionsOverInitialMap(
    // DSLParameter dslAnnotation,
    // Map<ConfigurationOptions, String> initialMap) {
    // Map<ConfigurationOptions, String> resultMap = new
    // HashMap<ConfigurationOptions, String>(
    // initialMap);
    // if (dslAnnotation != null) {
    // putIfValid(resultMap, ConfigurationOptions.ARRAY_DELIMITER,
    // dslAnnotation.arrayDelimiter());
    // putIfValid(resultMap, ConfigurationOptions.STRING_QUOTATION,
    // dslAnnotation.stringQuotation());
    // }
    // return resultMap;
    // }

}
