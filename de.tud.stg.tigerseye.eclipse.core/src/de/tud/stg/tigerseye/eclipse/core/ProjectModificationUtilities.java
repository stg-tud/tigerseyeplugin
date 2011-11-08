package de.tud.stg.tigerseye.eclipse.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.groovy.eclipse.core.model.GroovyRuntime;
import org.codehaus.jdt.groovy.model.GroovyNature;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.api.TigerseyeRuntimeException;
import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeClasspathContainer;
import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeCoreConstants;
import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeLibraryClasspathResolver;

public class ProjectModificationUtilities {

    public static final String tigerseyeBuilder = "de.tud.stg.tigerseye.core.tigerseyeBuilder";

    private static final Logger logger = LoggerFactory.getLogger(ProjectModificationUtilities.class);

    public static List<IProject> getOpenTigerseyeProjects() {
	IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

	ArrayList<IProject> projectslist = new ArrayList<IProject>(projects.length);
	for (IProject proj : projects) {
	    try {
		if (proj.isOpen() && proj.hasNature(TigerseyeCoreConstants.TIGERSEYE_NATURE_ID)) {
		    projectslist.add(proj);
		}
	    } catch (CoreException e) {
		logger.warn("Failed to check for nature on project: {}", proj, e);
	    }

	}

	return projectslist;
    }

    public static void createTigerseyeSourceFoldersIfNotExisting() {
	List<IProject> tigerseyeProjects = getOpenTigerseyeProjects();
	for (IProject proj : tigerseyeProjects) {
	    setSourceFolder(proj);
	}
    }

    public static void updateTigerseyeClasspathContainerOnProjects(IJavaProject... projects) throws JavaModelException {

	final TigerseyeLibraryClasspathResolver resolver = new TigerseyeLibraryClasspathResolver();
	resolver.recomputeClassPathEntries();

	List<TigerseyeClasspathContainer> containers = new ArrayList<TigerseyeClasspathContainer>(projects.length);
	for (IJavaProject input : projects) {
	    TigerseyeClasspathContainer container = new TigerseyeClasspathContainer(input.getProject());
	    container.setTigerseyeLibraryClasspathResolver(resolver);
	    containers.add(container);
	}

	JavaCore.setClasspathContainer(TigerseyeClasspathContainer.CONTAINER_ID, projects,
		containers.toArray(new IClasspathContainer[containers.size()]), null);
    }

    public static boolean isEnabledTigerseyeNature(IProject iProject) {
	try {
	    return iProject.isNatureEnabled(TigerseyeCoreConstants.TIGERSEYE_NATURE_ID);
	} catch (CoreException e) {
	    return false;
	}
    }

    public static void setSourceFolder(IProject project) {
	IJavaProject jp = JavaCore.create(project);
	IFolder outPutFolder = project.getFolder(TigerseyeCore.getOutputDirectoryPath());
	setSourceFolder(jp, outPutFolder);
    }

    public static void setSourceFolder(IJavaProject project, IFolder outPutFolder) {
	try {
	    if (!outPutFolder.exists()) {
		outPutFolder.create(IResource.DERIVED, true, new NullProgressMonitor());
	    }
	    IClasspathEntry entry = JavaCore.newSourceEntry(outPutFolder.getFullPath());
	    addClassPathEntry(project, entry);
	} catch (CoreException e) {
	    throw new TigerseyeRuntimeException("Failed to set source folder " + outPutFolder);
	}
    }

    public static void removeSourceFolder(IJavaProject project, IFolder outPutFolder) {
	try {
	    IClasspathEntry entry = JavaCore.newSourceEntry(outPutFolder.getFullPath());
	    removeClassPathEntry(project, entry);
	} catch (CoreException e) {
	    throw new TigerseyeRuntimeException("Failed to remove source folder " + outPutFolder);
	}
    }

    /**
     * @see GroovyRuntime#addClassPathEntry(IJavaProject, IClasspathEntry)
     */
    public static void addClassPathEntry(IJavaProject project, IClasspathEntry newEntry) throws JavaModelException {
	IClasspathEntry[] oldClassPath = project.getRawClasspath();
	if (!ArrayUtils.contains(oldClassPath, newEntry)) {
	    IClasspathEntry[] newEntries = (IClasspathEntry[]) ArrayUtils.add(oldClassPath, newEntry);
	    project.setRawClasspath(newEntries, null);
	}
    }

    /**
     * @see GroovyRuntime#addClassPathEntry(IJavaProject, IClasspathEntry)
     */
    public static void removeClassPathEntry(IJavaProject project, IClasspathEntry newEntry) throws JavaModelException {
	IClasspathEntry[] oldClassPath = project.getRawClasspath();
	if (ArrayUtils.contains(oldClassPath, newEntry)) {
	    IClasspathEntry[] newEntries = (IClasspathEntry[]) ArrayUtils.removeElement(oldClassPath, newEntry);
	    project.setRawClasspath(newEntries, null);
	}
    }

    public static void addTigerseyeRuntimeLibraries(IJavaProject javaProject) {
	try {
	    IClasspathEntry newContainerEntry = JavaCore.newContainerEntry(TigerseyeClasspathContainer.CONTAINER_ID,
		    true);
	    addClassPathEntry(javaProject, newContainerEntry);
	} catch (CoreException e) {
	    logger.error("Failed to add Tigerseye runtime libraries to {}", javaProject.getProject().getName(), e);
	    throw new TigerseyeRuntimeException(e);
	}
    }

    public static void addTigerseyeNatures(IProject project) {
	try {
	    final IProjectDescription description = project.getDescription();
	    List<String> natureIds = new LinkedList<String>();
	    if (natureIds.contains(TigerseyeCoreConstants.TIGERSEYE_NATURE_ID))
		return;

	    Collections.addAll(natureIds, description.getNatureIds());
	    String[] necessaryNatures = new String[] { //
	    TigerseyeCoreConstants.TIGERSEYE_NATURE_ID,//
		    GroovyNature.GROOVY_NATURE,//
	    /*
	     * If the Java nature has not already been initialized, simply
	     * adding it won't result in a completely configured java project.
	     * Therefore I won't allow it at first. This results in an error
	     * message that the java nature is required before Tigerseye can be
	     * configured.
	     */
	    // JavaCore.NATURE_ID, //
	    };
	    for (String necessaryNature : necessaryNatures) {
		if (!natureIds.contains(necessaryNature)) {
		    natureIds.add(necessaryNature);
		}
	    }
	    String[] array = natureIds.toArray(new String[natureIds.size()]);
	    description.setNatureIds(array);
	    project.setDescription(description, new NullProgressMonitor());
	} catch (CoreException e) {
	    logger.error("Failed to set necessary natures", e);
	    throw new TigerseyeRuntimeException(e);
	}
    }

    public static void removeTigerseyeNature(IProject project) throws CoreException {
	IProjectDescription description = project.getDescription();
	String[] natureIds = description.getNatureIds();
	List<String> asList = new ArrayList<String>(Arrays.asList(natureIds));
	asList.remove(TigerseyeCoreConstants.TIGERSEYE_NATURE_ID);
	String[] noTigerseye = asList.toArray(new String[asList.size()]);
	description.setNatureIds(noTigerseye);
	project.setDescription(description, new NullProgressMonitor());
    }

    public static void addBuilder(IProject project) {
	try {
	    IProjectDescription desc = project.getDescription();
	    ICommand[] commands = desc.getBuildSpec();
	    for (int i = 0; i < commands.length; ++i)
		if (tigerseyeBuilder.equals(commands[i].getBuilderName()))
		    return;
	    // add builder to project
	    ICommand command = desc.newCommand();
	    command.setBuilderName(tigerseyeBuilder);
	    ICommand[] nc = new ICommand[commands.length + 1];
	    // Add it before other builders.
	    System.arraycopy(commands, 0, nc, 1, commands.length);
	    nc[0] = command;
	    desc.setBuildSpec(nc);
	    project.setDescription(desc, null);
	} catch (CoreException e) {
	    throw new TigerseyeRuntimeException("Failed to add Builder", e);
	}
    }

    /**
     * 
     * 
     * Adds only the tigerseye runtime configuration, without any DSLs, to the
     * specified project
     * 
     * @param project
     */
    public static void addTigersEyeRuntimeConfiguration(IProject project) {
	logger.debug("Adding tigerseye configuration for project {} ", project);
	addTigerseyeNatures(project);
	initializeNecessaryClasspathContainer(project);
	setSourceFolder(project);
	addBuilder(project);
    }

    /**
     * Adds the configuration for a project that uses DSLs
     * 
     * @param project
     */
    public static void addTigerseyeLanguageDesignConfiguration(IProject project) {
	addTigersEyeRuntimeConfiguration(project);
    }

    private static void initializeNecessaryClasspathContainer(IProject project) {
	IJavaProject javaProject = JavaCore.create(project);
	ProjectModificationUtilities.addTigerseyeRuntimeLibraries(javaProject);
	GroovyRuntime.addGroovyClasspathContainer(javaProject);
    }

}
