package de.tud.stg.tigerseye.eclipse.core.internal;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.eclipse.core.resources.IProject;

import de.tud.stg.tigerseye.eclipse.core.api.ClassLoaderStrategy;
import de.tud.stg.tigerseye.eclipse.core.api.DSLContributor;
import de.tud.stg.tigerseye.eclipse.core.internal.WorkspaceDSLDefintionsResolver.WorkspaceDSL;

public class WorkspaceDSLContributor implements DSLContributor {

    private final String contributorId;
    private final IProject workspaceProject;

    public WorkspaceDSLContributor(WorkspaceDSL workspaceDSL) {
	this.contributorId = workspaceDSL.getPluginID();
	this.workspaceProject = workspaceDSL.workspaceProject;
    }

    @Override
    public String getId() {
	return contributorId;
    }

    @Override
    public ClassLoaderStrategy createClassLoaderStrategy() {
	WorkspaceProjectClassLoaderStrategy wpcls = new WorkspaceProjectClassLoaderStrategy(
		workspaceProject);
	return wpcls;
    }

    @Override
    public String toString() {
	return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
		.append(contributorId).append(workspaceProject).toString();
    }

}
