package de.tud.stg.tigerseye.eclipse.core.internal;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

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

    @Override
    public String toString() {
	return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
		.append(contributorName).toString();
    }

}
