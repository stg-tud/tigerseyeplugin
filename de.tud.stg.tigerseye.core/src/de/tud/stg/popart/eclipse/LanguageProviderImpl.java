package de.tud.stg.popart.eclipse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;

import de.tud.stg.tigerseye.core.DSLDefinition;
import de.tud.stg.tigerseye.core.DSLKey;
import de.tud.stg.tigerseye.core.ILanguageProvider;
import de.tud.stg.tigerseye.core.NoLegalPropertyFound;
import de.tud.stg.tigerseye.core.preferences.DSLDefinitionImpl;

/**
 * Provides access to registered DSLs.
 * 
 * @author Leo Roos
 * 
 */
public class LanguageProviderImpl implements ILanguageProvider {

    private final IPreferenceStore store;

    public LanguageProviderImpl(IPreferenceStore store) {
	this.store = store;
    }

    private IPreferenceStore getStore() {
	return store;
    }

    @Override
    public List<DSLDefinition> getDSLDefinitions() {
	List<DSLDefinition> registeredDefinitions = getPluginConfiguredDSLLanguages();
	return Collections.unmodifiableList(registeredDefinitions);
    }

    private ArrayList<DSLDefinition> getPluginConfiguredDSLLanguages() {
	IConfigurationElement[] confElements = Platform.getExtensionRegistry()
		.getConfigurationElementsFor(
			"de.tud.stg.tigerseye.dslDefinitions");

	ArrayList<DSLDefinition> dslDefinitions = new ArrayList<DSLDefinition>(
		confElements.length);
	for (IConfigurationElement confEl : confElements) {
	    String dslNameAttribute = confEl.getAttribute("name");
	    String dslClassAttribute = confEl.getAttribute("class");
	    String dslContributorPlugin = confEl.getContributor().getName();
	    String languageKey = dslContributorPlugin + dslClassAttribute;
	    DSLDefinitionImpl dsl = new DSLDefinitionImpl(dslClassAttribute,
		    dslContributorPlugin, dslNameAttribute, languageKey);
	    fillOptionalValues(confEl, dsl);
	    dslDefinitions.add(dsl);
	}
	return dslDefinitions;
    }

    private void fillOptionalValues(IConfigurationElement confEl,
	    DSLDefinitionImpl dsl) {
	dsl.setStore(getStore());
	String defExt = confEl.getAttribute("extension");
	if (defExt == null)
	    return;
	try {
	    String keyFor = dsl.getKeyFor(DSLKey.EXTENSION);
	    getStore().setDefault(keyFor, defExt);
	    dsl.getValue(DSLKey.EXTENSION);
	} catch (NoLegalPropertyFound e) {
	    // default value will be returned if nothing else set
	}

    }

    @Override
    public List<DSLDefinition> getDSLForExtension(String dslName) {
	List<DSLDefinition> dsls = this.getDSLDefinitions();
	List<DSLDefinition> possibleTarget = new ArrayList<DSLDefinition>();
	for (DSLDefinition dsl : dsls) {
	    String nextExt;
	    try {
		nextExt = dsl.getValue(DSLKey.EXTENSION);
		if (nextExt.equals(dslName))
		    possibleTarget.add(dsl);
	    } catch (NoLegalPropertyFound e) {
		// Can be safely ignored
	    }
	}
	return possibleTarget;
    }

}
