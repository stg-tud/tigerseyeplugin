package de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.builder.core.annotations.DSLMethod.DslMethodType;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ConfigurationOptions;

/**
 * Extracts DSLMethod annotation informations. If none have been set the methods
 * will return default values.
 * 
 * @author Leo_Roos
 * 
 */
public class ExtractedMethodInformation extends ExtractorBase {
    @Nullable
    private DSLMethod dslMethodAnnotation = ExtractorDefaults.DEFAULT_DSLMethod;

    private boolean hasDSLMethodAnnotation = false;
    @Nonnull
    final Method method;
    @Nonnull
    Map<ConfigurationOptions, String> methodOptions = ExtractorDefaults.DEFAULT_CONFIGURATIONOPTIONS_MAP;

    public ExtractedMethodInformation(Method method) {
	this.method = method;
    }

    @Override
    public Map<ConfigurationOptions, String> getConfigurationOptions() {
	return this.methodOptions;
    }

    public DslMethodType getDSLType() {
	return this.dslMethodAnnotation.type();
    }

    public Method getMethod() {
	return method;
    }

    @Override
    public boolean isAnnotated() {
	return hasDSLMethodAnnotation;
    }

    public boolean isToplevel() {
	return this.dslMethodAnnotation.topLevel();
    }

    /**
     * Actually extract the method information
     * 
     * @param defaultParameterOptions
     */
    @Override
    public void load(Map<ConfigurationOptions, String> defaultParameterOptions) {
	assertAvoidanceOfDSLAnnotation(method, DSLMethod.class);
	DSLMethod annotation = method.getAnnotation(DSLMethod.class);
	if (annotation == null) {
	    hasDSLMethodAnnotation = false;
	} else {
	    hasDSLMethodAnnotation = true;
	    this.dslMethodAnnotation = annotation;
	    this.methodOptions = getAnnotationParameterOptionsOverInitialMap(
		    this.dslMethodAnnotation, defaultParameterOptions);
	}
    }

    static Map<ConfigurationOptions, String> getAnnotationParameterOptionsOverInitialMap(
	    DSLMethod dslAnnotation,
	    Map<ConfigurationOptions, String> initialMap) {
	Map<ConfigurationOptions, String> resultMap = new HashMap<ConfigurationOptions, String>(
		initialMap);
	if (dslAnnotation != null) {
	    putIfValid(resultMap, ConfigurationOptions.PARAMETER_ESCAPE,
		    dslAnnotation.parameterEscape());
	    putIfValid(resultMap, ConfigurationOptions.WHITESPACE_ESCAPE,
		    dslAnnotation.whitespaceEscape());
	    putIfValid(resultMap, ConfigurationOptions.ARRAY_DELIMITER,
		    dslAnnotation.arrayDelimiter());
	    putIfValid(resultMap, ConfigurationOptions.STRING_QUOTATION,
		    dslAnnotation.stringQuotation());
	}
	return resultMap;
    }

    @Override
    public String toString() {
	ToStringBuilder append = new ToStringBuilder(this,
		ToStringStyle.SHORT_PREFIX_STYLE)
		.append("annotation", dslMethodAnnotation.getClass())
		.append("isannotated", hasDSLMethodAnnotation)
		.append("configurationoptions", methodOptions)
		.append("underlyingmethod", method);
	return append.toString();
    }
}