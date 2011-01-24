package de.tud.stg.popart.builder.eclipse;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TigerseyeClassPathContainerInitializer extends ClasspathContainerInitializer {

    private static final Logger logger = LoggerFactory
	    .getLogger(TigerseyeClassPathContainerInitializer.class);
    public static final Path TIGERSEYE_SUPPORT = new Path("TIGERSEYE_SUPPORT");

	public TigerseyeClassPathContainerInitializer() {
	}

	@Override
	public void initialize(IPath containerPath, IJavaProject project) throws CoreException {

	logger.info("initialize classpath for path: {} and project: {}",
		containerPath, project);

	IClasspathContainer container = new TigerseyeClasspathContainer(
		containerPath);

		JavaCore.setClasspathContainer(containerPath, new IJavaProject[] { project },
				new IClasspathContainer[] { container }, null);
	}

    @Override
    public boolean canUpdateClasspathContainer(IPath containerPath,
	    IJavaProject project) {
	/*
	 * Needs to be update-able in order to react to runtime changes to
	 * configured DSLs.
	 */
	return true;
    }

    @Override
    public void requestClasspathContainerUpdate(IPath containerPath,
	    IJavaProject project, IClasspathContainer containerSuggestion)
	    throws CoreException {
	logger.trace("classpathcontainer update requested");
	this.initialize(containerPath, project);
    }

}
