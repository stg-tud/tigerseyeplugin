package de.tud.stg.tigerseye.eclipse.core.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import org.eclipse.jface.preference.IPreferenceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.api.DSLKey;
import de.tud.stg.tigerseye.eclipse.core.api.ILanguageProvider;
import de.tud.stg.tigerseye.eclipse.core.api.NoLegalPropertyFoundException;
import de.tud.stg.tigerseye.eclipse.core.api.TigerseyeRuntimeException;
import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeCoreConstants.DSLDefinitionsAttribute;

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

    private final Set<DSLConfigurationElement> dslConfigurationElements;

    /**
     * Lazy initialized
     */
    @Nullable
    private Set<DSLDefinition> dslDefinitionsCache;

    public LanguageProviderImpl(IPreferenceStore store,
	    Set<DSLConfigurationElement> dslConfs) {
	this.store = store;
	this.dslConfigurationElements = dslConfs;
    }

    private IPreferenceStore getStore() {
	return store;
    }

    @Override
    public Set<DSLDefinition> getDSLDefinitions() {
	if (dslDefinitionsCache == null) {
	    dslDefinitionsCache = recomputeDSLDefinitions();
	}
	return dslDefinitionsCache;
    }

    private Set<DSLDefinition> recomputeDSLDefinitions() {
	Set<DSLDefinitionImpl> dslDefinitions = getConfiguredDSLDefinitions();
	Set<DSLDefinition> result = new HashSet<DSLDefinition>();
	for (DSLDefinitionImpl dslDefinition : dslDefinitions) {
	    result.add(dslDefinition);
	}
	setValidActivationState(result);
	return Collections.unmodifiableSet(result);
    }

    private Set<DSLDefinitionImpl> getConfiguredDSLDefinitions() {
	HashSet<DSLDefinitionImpl> dslDefinitions = new HashSet<DSLDefinitionImpl>();
	for (DSLConfigurationElement confEl : this.dslConfigurationElements) {
	    DSLDefinitionImpl dsl = createDSLDefinition(confEl);
	    fillOptionalValues(confEl, dsl);
	    dslDefinitions.add(dsl);
	}
	return dslDefinitions;
    }

    private void fillOptionalValues(DSLConfigurationElement wdsl,
	    DSLDefinitionImpl dsl) {
	dsl.setStore(getStore());
	String defExt = wdsl.getAttribute(DSLDefinitionsAttribute.Extension);
	if (defExt == null)
	    return;
	String keyFor = dsl.getKeyFor(DSLKey.EXTENSION);
	getStore().setDefault(keyFor, defExt);
    }

    private DSLDefinitionImpl createDSLDefinition(DSLConfigurationElement confEl) {
	String className = confEl.getAttribute(DSLDefinitionsAttribute.Class);
	String prettyName = confEl
		.getAttribute(DSLDefinitionsAttribute.PrettyName);
	DSLDefinitionImpl dslDefinitionImpl = new DSLDefinitionImpl(className,
		confEl, prettyName);
	return dslDefinitionImpl;
    }

    void setValidActivationState(
Collection<DSLDefinition> registeredDefinitions) {
	ArrayList<DSLDefinition> manuallyConfiguredDSLs = new ArrayList<DSLDefinition>();
	ArrayList<DSLDefinition> notManuallyConfiguredDSLs = new ArrayList<DSLDefinition>();
	for (DSLDefinition dslDefinition : registeredDefinitions) {
	    boolean wasManuallySet = activeStateAlreadySet(dslDefinition);
	    if (wasManuallySet) {
		manuallyConfiguredDSLs.add(dslDefinition);
	    } else {
		notManuallyConfiguredDSLs.add(dslDefinition);
	    }
	}
	Map<String, DSLDefinition> alreadyActivatedExtension = new HashMap<String, DSLDefinition>();
	/* prefer manual configuration */
	for (DSLDefinition dsl : manuallyConfiguredDSLs) {
	    alreadyActivatedExtension = deactivateDSLIfAlreadyActiveDSLOfSameExtensionExists(
		    dsl, alreadyActivatedExtension);
	}
	for (DSLDefinition dslDefinition : notManuallyConfiguredDSLs) {
	    alreadyActivatedExtension = deactivateDSLIfAlreadyActiveDSLOfSameExtensionExists(
		    dslDefinition, alreadyActivatedExtension);
	}

    }

    private Map<String, DSLDefinition> deactivateDSLIfAlreadyActiveDSLOfSameExtensionExists(
	    DSLDefinition dsl,
	    Map<String, DSLDefinition> alreadyActivatedExtension) {
	boolean isActive = dsl.isActive();
	if (!isActive)
	    return alreadyActivatedExtension;

	String extension = getExtensionOrNull(dsl);
	boolean hasExtension = extension != null;
	if (!hasExtension)
	    return alreadyActivatedExtension;

	boolean otherDSLOfSameExtensionIsActive = alreadyActivatedExtension
		.containsKey(extension);
	if (otherDSLOfSameExtensionIsActive) {
	    logger.info(
		    "More than one dsl has the extension [{}]. Cannot activate this DSL [{}] since the DSL [{}] has the same extension.",
		    new Object[] { extension, dsl,
			    alreadyActivatedExtension.get(extension) });
	    dsl.setActive(false);
	} else {
	    alreadyActivatedExtension.put(extension, dsl);
	}
	return alreadyActivatedExtension;
    }

    private @CheckForNull
    String getExtensionOrNull(DSLDefinition dsl) {
	try {
	    return dsl.getValue(DSLKey.EXTENSION);
	} catch (NoLegalPropertyFoundException e) {
	    return null;
	}
    }

    private boolean activeStateAlreadySet(DSLDefinition dsl) {
	return getStore().contains(DSLActivationState.getKeyFor(dsl));
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
	    logger.trace("No active DSL for extension '{}' found", dslName);
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
	} catch (NoLegalPropertyFoundException e) {
	    return "";
	}
    }

    @Override
    public Map<DSLDefinition, Throwable> validateDSLDefinitionsStateReturnInvalidDSLs() {
	HashMap<DSLDefinition, Throwable> notLoadable = new HashMap<DSLDefinition, Throwable>();
	Set<DSLDefinitionImpl> dslDefinitions = getConfiguredDSLDefinitions();
	for (DSLDefinitionImpl dslDefinition : dslDefinitions) {
	    try {
		dslDefinition.loadClassRaw();
	    } catch (Exception e) {
		notLoadable.put(dslDefinition, e);
	    }
	}
	return Collections.unmodifiableMap(notLoadable);
    }

}
