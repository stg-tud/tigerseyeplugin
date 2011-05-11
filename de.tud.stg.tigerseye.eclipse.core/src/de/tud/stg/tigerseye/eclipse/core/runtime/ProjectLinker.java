package de.tud.stg.tigerseye.eclipse.core.runtime;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectLinker {

    private static final Logger logger = LoggerFactory
	    .getLogger(ProjectLinker.class);

    /**
     * Creates a project in the workspace according to passed arguments. The
     * returned project is still closed.
     * 
     * @param location
     *            location of the project to which to link
     * @param projectName
     *            name of the project
     * @return the created project
     * @throws CoreException
     *             when project could not be created
     */
    public IProject linkProject(URI location, String projectName)
	    throws CoreException {
	IProject project = ResourcesPlugin.getWorkspace().getRoot()
		.getProject(projectName);
	if (!project.exists()) {
	    IProjectDescription projDesc = ResourcesPlugin.getWorkspace()
		    .newProjectDescription(projectName);
	    projDesc.setLocationURI(location);
	    project.create(projDesc, null);
	} else {
	    logger.warn(
		    "project {} already exists; assuming this is the project you need",
		    project);
	}
	return project;
    }

}
