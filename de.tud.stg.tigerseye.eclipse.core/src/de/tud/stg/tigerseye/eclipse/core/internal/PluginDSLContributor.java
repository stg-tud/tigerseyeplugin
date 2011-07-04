package de.tud.stg.tigerseye.eclipse.core.internal;

import de.tud.stg.tigerseye.eclipse.core.api.ClassLoaderStrategy;
import de.tud.stg.tigerseye.eclipse.core.api.DSLContributor;


public class PluginDSLContributor implements DSLContributor {

    private final String contributorName;

    public PluginDSLContributor(String pluginContributorName) {
	contributorName = pluginContributorName;
    }

    @Override
    public String getId() {
	return contributorName;
    }

    @Override
    public ClassLoaderStrategy createClassLoaderStrategy() {
	BundleClassloaderStrategy bcls = new BundleClassloaderStrategy(getId());
	return bcls;
    }

}
