package de.tud.stg.tigerseye.eclipse.core.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;

import de.tud.stg.tigerseye.eclipse.core.api.ILanguageProvider;

/**
 * Computes all DSL candidates, collects them as a collection of
 * {@link DSLConfigurationElement} and creates a new {@link ILanguageProvider}
 * with those elements.
 * 
 * @author Leo Roos
 * 
 */
public class LanguageProviderFactory {

    /**
     * Create new {@link ILanguageProvider} instance based on the passed store.
     * 
     * @param store
     * @return
     */
    public ILanguageProvider createLanguageProvider(IPreferenceStore store) {

	HashMap<String, DSLConfigurationElement> dslConfsCollector = new HashMap<String, DSLConfigurationElement>();

	Set<WorkspaceDSLConfigurationElement> wdslConfs = DSLConfigurationElementResolver
		.getWorkspaceDSLConfigurationElements();

	for (DSLConfigurationElement dslConfigurationElement : wdslConfs) {
	    String id = dslConfigurationElement.getId();
	    dslConfsCollector.put(id, dslConfigurationElement);
	}

	// Then load installed bundles
	Set<PluginDSLConfigurationElement> installedPlugins = DSLConfigurationElementResolver
		.getInstalledDSLConfigurationElements();

	// Add only not in workspace defined dsls
	for (DSLConfigurationElement instplugin : installedPlugins) {
	    String key = instplugin.getId();
	    if (!dslConfsCollector.containsKey(key))
		dslConfsCollector.put(key, instplugin);
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


}
