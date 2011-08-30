package de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Map;

import org.eclipse.core.runtime.Assert;

import de.tud.stg.popart.builder.core.annotations.AnnotationConstants;
import de.tud.stg.popart.builder.core.annotations.DSL;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ConfigurationOptions;

public abstract class ExtractorBase {

    public ExtractorBase() {
	super();
    }

    public abstract void load(Map<ConfigurationOptions, String> defaultConfigurationOptions);

    public abstract boolean isAnnotated();

    /**
     * assigns value with key to resultMap if the value is neither null nor
     * equal to the UNASSIGNED constant.
     */
    protected static Map<ConfigurationOptions, String> putIfValid(
	    Map<ConfigurationOptions, String> resultMap,
	    ConfigurationOptions confOption, String value) {
	Assert.isNotNull(value);
	if (value.equals(AnnotationConstants.UNASSIGNED))
	    return resultMap;
	else {
	    resultMap.put(confOption, value);
	    return resultMap;
	}
    }

    // XXX(LeoRoos;Aug 28, 2011) delete when appropriate
    protected static void assertAvoidanceOfDSLAnnotation(AnnotatedElement el,
	    Class<? extends Annotation> useInstead) {
	DSL hasDeprecatedAnnotation = el.getAnnotation(DSL.class);
	if (hasDeprecatedAnnotation != null)
	    throw new IllegalArgumentException(DSL.class
		    + " is no longer supported. It was used on " + el
		    + ". Use " + useInstead + " instead.");
    }

    public String getConfiguratinoOption(ConfigurationOptions confOp) {
	String string = getConfigurationOptions().get(confOp);
	if (string == null)
	    throw new IllegalArgumentException(confOp
		    + " is not supported by this class");
	return string;
    }

    public abstract Map<ConfigurationOptions, String> getConfigurationOptions();

}