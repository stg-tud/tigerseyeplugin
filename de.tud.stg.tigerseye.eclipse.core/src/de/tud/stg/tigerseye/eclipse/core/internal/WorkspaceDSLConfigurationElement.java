package de.tud.stg.tigerseye.eclipse.core.internal;

import de.tud.stg.tigerseye.eclipse.core.api.DSLContributor;
import de.tud.stg.tigerseye.eclipse.core.internal.WorkspaceDSLDefintionsResolver.WorkspaceDSL;
import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeCoreConstants.DSLDefinitionsAttribute;

public class WorkspaceDSLConfigurationElement implements
	DSLConfigurationElement {

    private final WorkspaceDSL workspaceDSL;

    public WorkspaceDSLConfigurationElement(WorkspaceDSL workspaceDSL) {
	this.workspaceDSL = workspaceDSL;
    }

    @Override
    public String getAttribute(DSLDefinitionsAttribute attrName) {
	String attribute = workspaceDSL.getAttribute(attrName);
	return attribute;
    }

    @Override
    public DSLContributor getContributor() {
	WorkspaceDSLContributor dslContributor = new WorkspaceDSLContributor(
		workspaceDSL);
	return dslContributor;
    }

}
