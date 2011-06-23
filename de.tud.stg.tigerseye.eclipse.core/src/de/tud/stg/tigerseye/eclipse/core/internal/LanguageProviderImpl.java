package de.tud.stg.tigerseye.eclipse.core.internal;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.CheckForNull;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.popart.dslsupport.DSL;
import de.tud.stg.tigerseye.eclipse.core.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.DSLKey;
import de.tud.stg.tigerseye.eclipse.core.ILanguageProvider;
import de.tud.stg.tigerseye.eclipse.core.NoLegalPropertyFound;
import de.tud.stg.tigerseye.eclipse.core.preferences.TigerseyePreferenceConstants;
import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeRuntimeException;

/**
 * Provides access to registered DSLs.
 * 
 * @author Leo Roos
 * 
 */
public class LanguageProviderImpl implements ILanguageProvider {

    private static final String EXTENSION_ATTRIBUTE = "extension";

    private static final String CLASS_ATTRIBUTE = "class";

    private static final String NAME_ATTRIBUTE = "name";

    private static final Logger logger = LoggerFactory
	    .getLogger(LanguageProviderImpl.class);

    private final IPreferenceStore store;

    private final IConfigurationElement[] confEls;

    public LanguageProviderImpl(IPreferenceStore store,
	    IConfigurationElement[] iConfigurationElements) {
	this.store = store;
	this.confEls = iConfigurationElements;

    }

    private IPreferenceStore getStore() {
	return store;
    }

    @Override
    public List<DSLDefinition> getDSLDefinitions() {
	List<DSLDefinition> registeredDefinitions = getPluginConfiguredDSLLanguages();

	setValidActivationState(registeredDefinitions);

	return Collections.unmodifiableList(registeredDefinitions);
    }

    void setValidActivationState(
	    List<DSLDefinition> registeredDefinitions) {

	Map<DSLDefinition, String> dslToKey = new HashMap<DSLDefinition, String>(
		registeredDefinitions.size());
	Map<String, DSLDefinition> activatedExtensions = new HashMap<String, DSLDefinition>(
		registeredDefinitions.size());

	for (DSLDefinition dsl : registeredDefinitions) {
	    String extension = getExtensionOrNull(dsl);
	    dslToKey.put(dsl, extension);
	}

	// Activate manually changed at first
	for (Entry<DSLDefinition, String> entry : dslToKey.entrySet()) {
	    DSLDefinition dsl = entry.getKey();
	    String extension = entry.getValue();
	    if (extension != null && activeStateAlreadySet(dsl)
		    && dsl.isActive())
		if (activatedExtensions.containsKey(extension)) {
		    logger.error(
			    "More than one dsl has the extension [{}]. Cannot activate this DSL [{}] since the DSL [{}] has the same extension.",
			    new Object[] { extension, dsl,
				    activatedExtensions.get(extension) });
		    dsl.setValue(DSLKey.LANGUAGE_ACTIVE, false);
		} else {
		    activatedExtensions.put(extension, dsl);
	    }
	}

	boolean defaultActivationState = TigerseyePreferenceConstants.DEFAULT_LANGUAGE_ACTIVE_VALUE;
	// Check newly added languages
	for (Entry<DSLDefinition, String> entry : dslToKey.entrySet()) {
	    DSLDefinition dsl = entry.getKey();
	    String extension = entry.getValue();
	    if (extension != null
		    && !activatedExtensions.containsKey(extension)
		    && !activeStateAlreadySet(dsl)) {
		dsl.setValue(DSLKey.LANGUAGE_ACTIVE, defaultActivationState);
		activatedExtensions.put(extension, dsl);
	    }
	}
    }

    private @CheckForNull
    String getExtensionOrNull(DSLDefinition dsl) {
	try {
	    return dsl.getValue(DSLKey.EXTENSION);
	} catch (NoLegalPropertyFound e) {
	    return null;
	}
    }

    private boolean activeStateAlreadySet(DSLDefinition dsl) {
	String dslIsActiveKey = dsl.getKeyFor(DSLKey.LANGUAGE_ACTIVE);
	return getStore().contains(dslIsActiveKey);
    }

    private ArrayList<DSLDefinition> getPluginConfiguredDSLLanguages() {
	ArrayList<DSLDefinition> dslDefinitions = new ArrayList<DSLDefinition>(
		confEls.length);
	for (IConfigurationElement confEl : confEls) {
	    DSLDefinitionImpl dsl = createDSLDefinition(confEl);
	    if (dsl != null) {
		fillOptionalValues(confEl, dsl);
		dslDefinitions.add(dsl);
	    } else
		logger.warn(
			"Could not create DSL for dslDefiniton Extension of contributor: {}",
			confEl.getContributor());
	}
	return dslDefinitions;
    }

    private @CheckForNull
    DSLDefinitionImpl createDSLDefinition(IConfigurationElement confEl) {
	String dslNameAttribute = confEl.getAttribute(NAME_ATTRIBUTE);
	if (dslNameAttribute == null)
	    dslNameAttribute = "";
	String dslClassAttribute = confEl.getAttribute(CLASS_ATTRIBUTE);
	String dslContributorPlugin = confEl.getContributor().getName();
	String languageKey = dslContributorPlugin + dslClassAttribute;
	DSLDefinitionImpl dsl = new DSLDefinitionImpl(dslClassAttribute,
		dslContributorPlugin, dslNameAttribute, languageKey);
	return validatedDSLorNull(dsl);
    }

    private @CheckForNull
    DSLDefinitionImpl validatedDSLorNull(DSLDefinitionImpl dsl) {
	try {
	    // Check existence
	    Class<? extends DSL> loadClass = dsl.loadClass();
	    // Cannot do the next check since that would also execute possible
	    // logic within the constructor
	    // loadClass.newInstance();
	    /*
	     * TODO: Error Message if the constructor has non-empty args
	     */
	} catch (Exception e) {
	    logger.warn(
		    "Could not access registered DSL {} with class {} of plug-in {}. It will be ignored. Check your configuration. Is the correct DSL class name given? Is the plug-in accessible?",
		    new Object[] { dsl.getDslName(), dsl.getClassPath(),
			    dsl.getContributorSymbolicName(), e });
	    return null;
	}
	URL entry = Platform.getBundle(dsl.getContributorSymbolicName())
		.getEntry("/");
	if (entry == null) {
	    logger.warn(
		    "location of registered bundle {} not found. Can not add DSL {} to classpath; Consider not to change the location of an active plug-in ;)",
		    dsl.getContributorSymbolicName(), dsl.getDslName());
	    return null;
	}
	return dsl;
    }

    private void fillOptionalValues(IConfigurationElement confEl,
	    DSLDefinitionImpl dsl) {
	dsl.setStore(getStore());
	String defExt = confEl.getAttribute(EXTENSION_ATTRIBUTE);
	if (defExt == null)
	    return;
	String keyFor = dsl.getKeyFor(DSLKey.EXTENSION);
	getStore().setDefault(keyFor, defExt);
    }

    @Override
    public DSLDefinition getActiveDSLForExtension(String dslName) {
	List<DSLDefinition> possibleTargets = new ArrayList<DSLDefinition>();
	for (DSLDefinition dsl : this.getDSLDefinitions()) {
	    if (dsl.isActive()) {
		String nextExt = getExtensionOrEmptyStr(dsl);
		if (nextExt.equals(dslName))
		    possibleTargets.add(dsl);
	    }
	}
	if (possibleTargets.isEmpty()) {
	    //logger.info("No active DSL for extension '{}' found", dslName);
	    return null;
	}
	if (possibleTargets.size() > 1) {
	    logger.error(
		    "At most one active DSL of the same extension may be active but where {}: {}",
		    possibleTargets.size(), possibleTargets);
	    throw new TigerseyeRuntimeException(
		    "More than one DSL of same extension are active for extension "
			    + dslName);
	}
	return possibleTargets.get(0);
    }

    private String getExtensionOrEmptyStr(DSLDefinition dsl) {
	try {
	    return dsl.getValue(DSLKey.EXTENSION);
	} catch (NoLegalPropertyFound e) {
	    return "";
	}
    }

}
