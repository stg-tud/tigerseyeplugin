package de.tud.stg.tigerseye.eclipse.core.runtime;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import de.tud.stg.tigerseye.eclipse.core.ProjectModificationUtilities;
import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;

//FIXME renaming, refactoring, tests
public class TigerseyeNature implements IProjectNature {

    private IProject project;

    @Override
    public void configure() throws CoreException {
	ProjectModificationUtilities.addTigerseyeNatures(getProject());
	ProjectModificationUtilities.setSourceFolder(getProject());
	ProjectModificationUtilities.addTigerseyeRuntimeLibraries(JavaCore
		.create(getProject()));
	ProjectModificationUtilities.addBuilder(getProject());
    }

    @Override
    public void deconfigure() throws CoreException {
	IJavaProject jp = JavaCore.create(getProject());
	deconfigureSourceFolder(jp);
	deconfigureClassPath(jp);
	ProjectModificationUtilities.removeTigerseyeNature(getProject());
    }

    @Override
    public IProject getProject() {
	return this.project;
    }

    @Override
    public void setProject(IProject project) {
	this.project = project;

    }


    private void deconfigureClassPath(IJavaProject jp)
	    throws JavaModelException {
	IClasspathEntry entry = JavaCore.newContainerEntry(
		TigerseyeClasspathContainer.CONTAINER_ID, true);
	ProjectModificationUtilities.removeClassPathEntry(jp, entry);
    }

    private void deconfigureSourceFolder(IJavaProject jp) {
	IFolder srcFolder = jp.getProject().getFolder(
TigerseyeCore
		.getOutputDirectoryPath());
	ProjectModificationUtilities.removeSourceFolder(jp, srcFolder);
    }

}
