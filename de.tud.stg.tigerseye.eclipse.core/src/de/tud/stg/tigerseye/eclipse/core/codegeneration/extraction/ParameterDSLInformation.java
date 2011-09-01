package de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.tud.stg.popart.builder.core.annotations.DSLParameter;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ConfigurationOptions;

public class ParameterDSLInformation extends DSLInformation {

    private DSLParameter annotation = DSLInformationDefaults.DEFAULT_DSLParameter;
    private final Type type;
    private boolean isAnnotated = false;
    private final int parameterIndex;

    public ParameterDSLInformation(Type parameterType, int index) {
	this(parameterType, null, index);
    }

    public ParameterDSLInformation(Type type, DSLParameter annotation, int index) {
	this.type = type;
	this.parameterIndex = index;
	if (annotation != null) {
	    this.isAnnotated = true;
	    this.annotation = annotation;
	}
    }

    /**
     * @return the parameter type
     */
    public Type getType() {
	return type;
    }

    @Override
    public void load(Map<ConfigurationOptions, String> defaultConfigurationOptions) {
	if (isAnnotated()) {
	    setConfigurationOptions(getAnnotationParameterOptionsOverInitialMap(this.annotation,
		    defaultConfigurationOptions));
	}

    }

    public Map<ConfigurationOptions, String> getAnnotationParameterOptionsOverInitialMap(DSLParameter dslAnnotation,
	    Map<ConfigurationOptions, String> initialMap) {
	Map<ConfigurationOptions, String> resultMap = new HashMap<ConfigurationOptions, String>(initialMap);
	if (dslAnnotation != null) {
	    putIfValid(resultMap, ConfigurationOptions.ARRAY_DELIMITER, dslAnnotation.arrayDelimiter());
	    putIfValid(resultMap, ConfigurationOptions.STRING_QUOTATION, dslAnnotation.stringQuotation());
	}
	return resultMap;
    }

    @Override
    public boolean isAnnotated() {
	return isAnnotated;
    }

    /**
     * @return the index of the parameter in its origination method.
     */
    public int getIndex() {
	return this.parameterIndex;
    }

    @Override
    public String toString() {
	return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("isannotated", isAnnotated)
		.append("type", type).append("parameterIndex", parameterIndex)
		.append("annotation", annotation.getClass()).append("configurations", getConfigurationOptions())
		.toString();
    }

}
