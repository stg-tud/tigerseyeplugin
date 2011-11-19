package de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.tud.stg.tigerseye.dslsupport.annotations.DSLClass;
import de.tud.stg.tigerseye.dslsupport.annotations.DSLMethod;
import de.tud.stg.tigerseye.dslsupport.annotations.DSLParameter;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ConfigurationOptions;

/**
 * Extracts the DSL information
 * 
 * @author Leo_Roos
 * 
 */
public class DSLInformationDefaults {

    public static final String SUBSTRING_DEFINING_FILTERED_METHODS = "$";

    private DSLInformationDefaults() {
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

    /**
     * don't use in collections does not support hashcode()
     */
    static public final DSLClass DEFAULT_DSLClass = DefaultedAnnotation
	    .of(DSLClass.class);

    /**
     * don't use in collections does not support hashcode()
     */
    static public final DSLMethod DEFAULT_DSLMethod = DefaultedAnnotation
	    .of(DSLMethod.class);

    /**
     * don't use in collections does not support hashcode()
     */
    static public final DSLParameter DEFAULT_DSLParameter = DefaultedAnnotation
	    .of(DSLParameter.class);

    /**
     * not modifiable
     */
    public static final Map<ConfigurationOptions, String> DEFAULT_CONFIGURATIONOPTIONS_MAP = getDefaultOptions();

    private static Map<ConfigurationOptions, String> getDefaultOptions() {
	Map<ConfigurationOptions, String> defaultOptions = new HashMap<ConfigurationOptions, String>();
	for (ConfigurationOptions parop : ConfigurationOptions.values()) {
	    defaultOptions.put(parop, parop.defaultValue);
	}
	return Collections.unmodifiableMap(defaultOptions);
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
