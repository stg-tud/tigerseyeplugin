package de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction;

import static de.tud.stg.tigerseye.util.Utils.illegalForArg;

import java.util.Map;

import javax.annotation.Nonnull;

import org.eclipse.core.runtime.Assert;

import de.tud.stg.tigerseye.dslsupport.annotations.AnnotationConstants;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ConfigurationOptions;

/**
 * A parsing wrapper around DSL class elements. Extracts all DSL specific
 * information. To avoid Exceptions during creation the class has to be loaded
 * before the information are extracted:
 * 
 * <pre>
 * cdi = new ClassDSLInformation(SomeClass.class)
 * cdi.load();
 * </pre>
 * 
 * @author Leo_Roos
 * 
 */
public abstract class DSLInformation {

    @Nonnull
    private Map<ConfigurationOptions, String> configurationOptions = DSLInformationDefaults.DEFAULT_CONFIGURATIONOPTIONS_MAP;

    public DSLInformation() {
	super();
    }

    /**
     * load constructor class with
     * {@link DSLInformationDefaults#DEFAULT_CONFIGURATIONOPTIONS_MAP}
     * configuration.
     */
    public void load() {
	this.load(DSLInformationDefaults.DEFAULT_CONFIGURATIONOPTIONS_MAP);
    }

    /**
     * load Extractor class with given map as default configuration
     * 
     * @param defaultConfigurationOptions
     */
    public abstract void load(Map<ConfigurationOptions, String> defaultConfigurationOptions);

    public abstract boolean isAnnotated();

    /**
     * assigns value with key to resultMap if the value is neither null nor
     * equal to the UNASSIGNED constant.
     */
    protected static Map<ConfigurationOptions, String> putIfValid(Map<ConfigurationOptions, String> resultMap,
	    ConfigurationOptions confOption, String value) {
	Assert.isNotNull(value);
	if (value.equals(AnnotationConstants.UNASSIGNED))
	    return resultMap;
	else {
	    resultMap.put(confOption, value);
	    return resultMap;
	}
    }

    public String getConfigurationOption(ConfigurationOptions confOp) {
	String string = getConfigurationOptions().get(confOp);
	if (string == null)
	    throw new IllegalArgumentException(confOp + " is not supported by this class");
	return string;
    }

    public Map<ConfigurationOptions, String> getConfigurationOptions() {
	return this.configurationOptions;
    }

    protected void setConfigurationOptions(Map<ConfigurationOptions, String> configurationOptions) {
	for (ConfigurationOptions entry : ConfigurationOptions.values()) {
	    String string = configurationOptions.get(entry);
	    if (string == null)
		throw illegalForArg(string);
	}
	this.configurationOptions = configurationOptions;
    }

}