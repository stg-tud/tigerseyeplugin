package de.tud.stg.tigerseye.eclipse.core.runtime;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.annotation.CheckForNull;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.internal.DSLConfigurationElementResolver;

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

    /**
     * Tries to determine bundle install location and will link the project into
     * the workspace
     * 
     * @param bundle
     * @return the linked and opened project <code>null</code> otherwise
     */
    public static @CheckForNull
    IProject linkOpenedPluginIntoWorkspace(Bundle bundle) {
	if (!DSLConfigurationElementResolver.isBundleWorkspaceProject(bundle)) {
	    logger.warn("tried to link non workspace bundle {}"
		    + bundle.getSymbolicName());
	    return null;
	}
	IProject linkProject = linkBundleGetProject(bundle);
	if (linkProject == null) {
	    // linking failed
	    return null;
	}

	try {
	    if (!linkProject.isOpen()) {
		linkProject.open(null);
	    }
	} catch (CoreException e) {
	    logger.error("Failed to open project {}", linkProject, e);
	}

	return linkProject;
    }

    private static @CheckForNull
    IProject linkBundleGetProject(Bundle bundle) {
	IProject linkProject = null;
	try {
	    File bundleFile = FileLocator.getBundleFile(bundle);
	    linkProject = new ProjectLinker().linkProject(bundleFile.toURI(),
		    bundle.getSymbolicName());
	} catch (CoreException e) {
	    logger.error("linking failed", e);
	} catch (IOException e) {
	    logger.error("linking failed", e);
	}
	return linkProject;
    }
}
