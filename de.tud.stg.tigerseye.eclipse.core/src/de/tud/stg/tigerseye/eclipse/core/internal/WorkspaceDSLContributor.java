package de.tud.stg.tigerseye.eclipse.core.internal;

import de.tud.stg.tigerseye.eclipse.core.api.ClassLoaderStrategy;
import de.tud.stg.tigerseye.eclipse.core.api.DSLContributor;
import de.tud.stg.tigerseye.eclipse.core.internal.WorkspaceDSLDefintionsResolver.WorkspaceDSL;

public class WorkspaceDSLContributor implements DSLContributor {

    private final WorkspaceDSL workspaceDSL;

    public WorkspaceDSLContributor(WorkspaceDSL workspaceDSL) {
	this.workspaceDSL = workspaceDSL;
    }

    @Override
    public String getId() {
	return workspaceDSL.getPluginID();
    }

    @Override
    public ClassLoaderStrategy createClassLoaderStrategy() {
	WorkspaceProjectClassLoaderStrategy wpcls = new WorkspaceProjectClassLoaderStrategy(
		workspaceDSL.workspaceProject);
	return wpcls;
    }

}
