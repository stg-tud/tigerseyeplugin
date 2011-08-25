package de.tud.stg.tigerseye.eclipse.core.internal;

import javax.annotation.CheckForNull;

import de.tud.stg.tigerseye.eclipse.core.api.DSLContributor;
import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeCoreConstants.DSLDefinitionsAttribute;

public interface DSLConfigurationElement {

    @CheckForNull
    String getAttribute(DSLDefinitionsAttribute attrName);

    DSLContributor getContributor();

    String getId();

}
