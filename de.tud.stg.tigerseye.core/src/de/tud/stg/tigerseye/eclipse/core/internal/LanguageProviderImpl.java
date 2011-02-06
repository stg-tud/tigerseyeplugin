package de.tud.stg.tigerseye.eclipse.core.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.preference.IPreferenceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.DSLKey;
import de.tud.stg.tigerseye.eclipse.core.ILanguageProvider;
import de.tud.stg.tigerseye.eclipse.core.NoLegalPropertyFound;
import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeRuntimeException;

/**
 * Provides access to registered DSLs.
 * 
 * @author Leo Roos
 * 
 */
public class LanguageProviderImpl implements ILanguageProvider {

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
	return Collections.unmodifiableList(registeredDefinitions);
    }

    private ArrayList<DSLDefinition> getPluginConfiguredDSLLanguages() {
	;

	ArrayList<DSLDefinition> dslDefinitions = new ArrayList<DSLDefinition>(
		confEls.length);
	for (IConfigurationElement confEl : confEls) {
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
    public DSLDefinition getActiveDSLForExtension(String dslName) {
	List<DSLDefinition> possibleTargets = new ArrayList<DSLDefinition>();
	for (DSLDefinition dsl : this.getDSLDefinitions()) {
	    if (dsl.isActive()) {
		try {
		    String nextExt = dsl.getValue(DSLKey.EXTENSION);
		    if (nextExt.equals(dslName))
			possibleTargets.add(dsl);
		} catch (NoLegalPropertyFound e) {
		    // Can be safely ignored
		}
	    }
	}
	if (possibleTargets.isEmpty()) {
	    logger.warn("No active DSL for extension '{}' found", dslName);
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

}
