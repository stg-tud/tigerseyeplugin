package de.tud.stg.tigerseye.eclipse.core.runtime;

import java.io.File;
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
     * Creates a project in the workspace from the location {@code location}
     * with the name {@code projectName}. The returned project will still
     * closed.
     * 
     * @param location
     *            location of the project to which to link
     * @param projectName
     *            name of the project
     * @return the created project or <code>null</code> if project can either
     *         not be linked or the workspace project with given name already
     *         exists and has a different locationURI
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
	    return project;
	} else {
	    URI locationURI = project.getLocationURI();
	    String path = location.getPath();
	    String path2 = locationURI == null ? "" : locationURI.getPath();
	    boolean equalsPath = new File(path).equals(new File(path2));
	    if (locationURI != null && equalsPath) {
		logger.trace(
			"project {} with location {} already exists; assuming this is the project you need.",
			project, locationURI);
		return project;
	    } else
		logger.error(
			"project {} already exists but its location [{}] does not fit the expected location: [{}].\nThis means probably you already have a different project in your workspace which has the same name as the project to be linked.\nCannot link to the project.",
			new Object[] { project, locationURI, location });
	}
	return null;
    }
}
