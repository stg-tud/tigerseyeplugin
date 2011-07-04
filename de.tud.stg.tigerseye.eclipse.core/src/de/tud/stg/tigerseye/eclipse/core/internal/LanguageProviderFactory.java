package de.tud.stg.tigerseye.eclipse.core.internal;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.api.ILanguageProvider;
import de.tud.stg.tigerseye.eclipse.core.internal.WorkspaceDSLDefintionsResolver.WorkspaceDSL;
import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeCoreConstants;
import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeCoreConstants.DSLDefinitionsAttribute;

/**
 * Computes all DSL candidates, collects them as a collection of
 * {@link DSLConfigurationElement} and create a new {@link ILanguageProvider}
 * with those elements.
 * 
 * @author Leo Roos
 * 
 */
public class LanguageProviderFactory {

    private static final Logger logger = LoggerFactory
	    .getLogger(LanguageProviderFactory.class);

    /**
     * Create new {@link ILanguageProvider} instance based on the passed store.
     * 
     * @param store
     * @return
     */
    public ILanguageProvider createLanguageProvider(IPreferenceStore store) {

	HashMap<String, DSLConfigurationElement> dslConfsCollector = new HashMap<String, DSLConfigurationElement>();

	// Prefer workspace location plug-ins
	IPluginModelBase[] workspaceModels = PluginRegistry
		.getWorkspaceModels();
	WorkspaceDSLDefintionsResolver wdslr = new WorkspaceDSLDefintionsResolver(
		workspaceModels);
	Set<WorkspaceDSLDefintionsResolver.WorkspaceDSL> wdsls = wdslr
		.getWorkspaceDSLDefintions();

	for (WorkspaceDSL workspaceDSL : wdsls) {
	    String id = workspaceDSL.dslLanguageDefinition.getPluginBase()
		    .getId();
	    String classAttrib = workspaceDSL
		    .getAttribute(DSLDefinitionsAttribute.Class);
	    String key = id + classAttrib;
	    dslConfsCollector.put(key, new WorkspaceDSLConfigurationElement(
		    workspaceDSL));
	}

	// Then load installed bundles
	IExtensionRegistry registry = RegistryFactory.getRegistry();
	if (registry != null) {
	    IConfigurationElement[] configurationElementsFor = registry
		    .getConfigurationElementsFor(TigerseyeCoreConstants.DSLDEFINITIONS_EXTENSION_POINT_ID);

	    for (IConfigurationElement iConfigurationElement : configurationElementsFor) {
		String pluginId = iConfigurationElement.getContributor()
			.getName();
		String attribute = iConfigurationElement
			.getAttribute(DSLDefinitionsAttribute.Class.attributeName);
		String key = pluginId + attribute;
		if (!dslConfsCollector.containsKey(key))
		    dslConfsCollector.put(key,
			    new PluginDSLConfigurationElement(
				    iConfigurationElement));
	    }
	}

	/*
	 * By preferring the workspace projects it is possible to apply a
	 * consistent refresh mechanism to reload classes of changed DSL
	 * plug-ins during runtime. When a DSL language is used via the bundle
	 * mechanism it is necessary to refresh it via the OSGI update
	 * mechanism. I.e. the bundle has to be stopped, its classpath updated
	 * and started again. See
	 * org.osgi.service.packageadmin.PackageAdmin#refreshPackages(Bundle[])
	 * for a more precise description. This might have side effects on
	 * dependent plug-ins. When I use the JDTRuntime to compute the
	 * classpath and instantiate a new URLClassloader all the classes
	 * declared on the DSL projects will be loaded in their most recent
	 * state without interfering with any installed Bundles.
	 */

	return new LanguageProviderImpl(
		store,
		new HashSet<DSLConfigurationElement>(dslConfsCollector.values()));
    }

    private boolean isBundleInstallation(WorkspaceDSL workspaceDSL) {
	boolean isBundleInstallation = false;
	Bundle bundle = Platform.getBundle(workspaceDSL.dslLanguageDefinition
		.getPluginBase().getId());
	if (bundle != null) {
	    String installLocation = workspaceDSL.dslLanguageDefinition
		    .getPluginModel()
		    .getInstallLocation();
	    File installFile = new File(installLocation);
	    try {
		File bundleFile = FileLocator.getBundleFile(bundle);
		isBundleInstallation = bundleFile.equals(installFile);
	    } catch (IOException e) {
		logger.debug(
			"Failed to determine whether bundle {} is plug-in or workspace project of a teststarting Eclipse Instance. Will assume pessimistic that it is and not add it.",
			bundle);
		isBundleInstallation = true;
	    }
	}
	return isBundleInstallation;
    }

}
