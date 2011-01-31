package de.tud.stg.tigerseye.eclipse.core;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.popart.builder.eclipse.TigerseyeClasspathContainer;
import de.tud.stg.tigerseye.eclipse.core.internal.TigerseyeCoreActivator;

//FIXME renaming, refactoring, tests
public class TigerseyeNature implements IProjectNature {

    public static final String TIGERSEYE_NATURE_ID = TigerseyeCoreActivator.PLUGIN_ID
	    + ".tigerseyeNature";

    private static final Logger logger = LoggerFactory
	    .getLogger(TigerseyeNature.class);

    private IProject project;

    @Override
    public void configure() throws CoreException {
	TigerseyeRuntime.setNecessaryNatures(getProject());
	TigerseyeRuntime.setSourceFolder(getProject());
	TigerseyeRuntime.addTigerseyeRuntimeLibraries(JavaCore
		.create(getProject()));
	TigerseyeRuntime.addBuilder(getProject());
    }



    @Override
    public void deconfigure() throws CoreException {
	// TODO remove and clean nature dependencies
    }

    @Override
    public IProject getProject() {
	return this.project;
    }

    @Override
    public void setProject(IProject project) {
	this.project = project;

    }

    private void addRequiredLibraries(IProject project) {
	try {
	    // XXX Possible place to add extension libraries, but problem is
	    // they might change
	    IClasspathEntry entry = JavaCore.newContainerEntry(
		    TigerseyeClasspathContainer.CONTAINER_ID, true);
	    this.addToClasspath(entry, project);
	} catch (JavaModelException e) {
	    logger.warn("Generated log statement", e);
	}
    }

    private void checkForSourceFolder(IProject project) {
	// XXX When adjusting the output source folder an according adjustment
	// have to be made in a way it is made here
	/*
	 * The adjustment would first check if it can create a folder without
	 * forcing it in the workspace. If that is possible and it can configure
	 * it as source folder it will delete the old folder (or should it
	 * stay?) and force a rebuild for all projects
	 */
	IClasspathEntry entry = null;
	IFolder outPutFolder = project.getFolder(TigerseyeRuntime
		.getOutputDirectoryPath());
	try {
	    outPutFolder.create(false, true, new NullProgressMonitor());
	    logger.debug("created folder {}", outPutFolder);

	    IPath outputSourceFolderPath = outPutFolder.getFullPath();

	    entry = JavaCore.newSourceEntry(outputSourceFolderPath);
	    this.addToClasspath(entry, project);
	    logger.info("entry added to classpath {}", entry);
	} catch (JavaModelException e1) {
	    logger.warn("Classpath {} could not be set.", entry, e1);
	} catch (CoreException e) {
	    logger.warn("Aborting setting of source folder {}", outPutFolder, e);
	}
    }

    private void addToClasspath(IClasspathEntry entry, IProject project)
	    throws JavaModelException {
	IJavaProject iJavaProject = JavaCore.create(project);
	IClasspathEntry[] entries = iJavaProject.getRawClasspath();

	for (IClasspathEntry e : entries) {
	    if (e.equals(entry)) {
		logger.info("classpath already added");
		return;
	    }
	}

	IClasspathEntry[] newEntries = new IClasspathEntry[entries.length + 1];
	System.arraycopy(entries, 0, newEntries, 0, entries.length);
	newEntries[newEntries.length - 1] = entry;
	iJavaProject.setRawClasspath(newEntries, null);
    }
}
