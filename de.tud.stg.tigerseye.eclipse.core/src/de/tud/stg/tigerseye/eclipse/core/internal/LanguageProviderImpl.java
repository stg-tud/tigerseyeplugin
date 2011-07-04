package de.tud.stg.tigerseye.eclipse.core.internal;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.CheckForNull;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.ModelEntry;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.popart.dslsupport.DSL;
import de.tud.stg.tigerseye.eclipse.core.api.DSLContributor;
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.api.DSLKey;
import de.tud.stg.tigerseye.eclipse.core.api.ILanguageProvider;
import de.tud.stg.tigerseye.eclipse.core.api.NoLegalPropertyFoundException;
import de.tud.stg.tigerseye.eclipse.core.api.TigerseyeDefaultConstants;
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
	Set<DSLDefinition> dslDefinitions = getConfiguredDSLDefinitions();
	setValidActivationState(dslDefinitions);
	return Collections.unmodifiableSet(dslDefinitions);
    }

    private Set<DSLDefinition> getConfiguredDSLDefinitions() {
	HashSet<DSLDefinition> dslDefinitions = new HashSet<DSLDefinition>();
	for (DSLConfigurationElement confEl : this.dslConfigurationElements) {
	    // confEl is XML element "language" of xmlElement "dslDefinitions"
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
	String prettyName = confEl.getAttribute(DSLDefinitionsAttribute.Name);
	DSLContributor contributor = confEl.getContributor();
	String languageKey = makeLanguageKey(className, contributor);
	DSLDefinitionImpl dslDefinitionImpl = new DSLDefinitionImpl(className,
		contributor, prettyName, languageKey);
	return validatedDSLorNull(dslDefinitionImpl);
    }

    void setValidActivationState(Collection<DSLDefinition> registeredDefinitions) {

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

	boolean defaultActivationState = TigerseyeDefaultConstants.DEFAULT_LANGUAGE_ACTIVE_VALUE;
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
	} catch (NoLegalPropertyFoundException e) {
	    return null;
	}
    }

    private boolean activeStateAlreadySet(DSLDefinition dsl) {
	String dslIsActiveKey = dsl.getKeyFor(DSLKey.LANGUAGE_ACTIVE);
	return getStore().contains(dslIsActiveKey);
    }

    String makeLanguageKey(String dslClassAttribute,
	    DSLContributor dslContributorPlugin) {
	return dslContributorPlugin.getId() + dslClassAttribute;
    }

    private @CheckForNull
    DSLDefinitionImpl validatedDSLorNull(DSLDefinitionImpl dsl) {
	try {
	    // Check existence
	    Class<? extends DSL> loadClass = dsl.loadClass();
	    if (loadClass == null) {
		logger.warn(
			"DSL {} could not be loaded. Possible cause unknown",
			loadClass);
		return null;
	    }
	    /*
	     * Cannot do the next check since that would also execute possible
	     * logic within the constructor
	     */
	    // loadClass.newInstance();
	    logIfClassHasNoZeroArgConstructor(loadClass);
	} catch (Exception e) {
	    logger.warn(
		    "Could not access registered DSL {} with class {} of plug-in {}. It will be ignored. Check your configuration. Is the correct DSL class name given? Is the plug-in accessible?",
		    new Object[] { dsl.getDslName(), dsl.getClassPath(),
			    dsl.getContributor().getId(), e });
	    return null;
	}
	ModelEntry findEntry = PluginRegistry.findEntry(dsl.getContributor()
		.getId());
	IPluginModelBase model = findEntry.getModel();
	if (model != null) {
	    String installLocation = model.getInstallLocation();
	    File file = new File(installLocation);
	    if (!file.exists()) {
		logger.warn(
			"location of plugin {} not found. Can not add DSL {} to classpath; Consider not to change the location of an active plug-in ;)",
			dsl.getContributor().getId(), dsl.getDslName());
		return null;
	    }
	}
	if (model == null) {
	    logger.error("No plugin definition for given id {} can be found",
		    dsl.getContributor().getId());
	    return null;
	}
	return dsl;
    }

    private void logIfClassHasNoZeroArgConstructor(
	    Class<? extends DSL> loadClass) {
	Constructor<?>[] constructors = loadClass.getConstructors();
	if (constructors.length < 1) {
	    logger.warn("DSL Class has no public contructor. Tigerseye expects a constructor with zero arguments");
	} else {
	    boolean hasZeroArgConstructor = false;
	    for (Constructor<?> constructor : constructors) {
		Class<?>[] parameterTypes = constructor.getParameterTypes();
		if (parameterTypes.length < 1) {
		    hasZeroArgConstructor = true;
		    break;
		}
	    }
	    if (!hasZeroArgConstructor) {
		logger.warn("DSL Class has no public contructor with zero arguments. Tigerseye expects a public constructor with zero arguments");
	    }
	}
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

}
