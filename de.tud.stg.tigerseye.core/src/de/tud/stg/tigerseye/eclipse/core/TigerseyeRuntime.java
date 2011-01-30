package de.tud.stg.tigerseye.eclipse.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.groovy.eclipse.core.model.GroovyRuntime;
import org.codehaus.jdt.groovy.model.GroovyNature;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.popart.builder.eclipse.DSLNature;
import de.tud.stg.popart.builder.eclipse.TigerseyeClassPathContainerInitializer;
import de.tud.stg.popart.builder.eclipse.TigerseyeClasspathContainer;
import de.tud.stg.tigerseye.eclipse.core.preferences.TigerseyePreferenceConstants;

public class TigerseyeRuntime {
    private static final Logger logger = LoggerFactory
	    .getLogger(TigerseyeRuntime.class);

    public static String getOutputDirectoryPath() {
        String outputfolder = TigerseyeCore.getPreferences().getString(
        	TigerseyePreferenceConstants.TIGERSEYE_OUTPUT_FOLDER_PATH);
        return outputfolder;
    }

    /**
     * Performs in all Projects with Tigerseye Nature an update of Tigerseye
     * classpath containers according to current preferences.
     */
    public static void updateTigerseyeClassPaths() {
        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
        	.getProjects();
        for (IProject iProject : projects) {
            if (isTigerseyeNature(iProject)) {
        	IJavaProject jp = JavaCore.create(iProject);
        	updateTigerseyeCPContainerofProject(jp);
            }
        }
    }

    static void updateTigerseyeCPContainerofProject(IJavaProject jp) {
        try {
            TigerseyeClassPathContainerInitializer tigerseyeClassPathContainerInitializer = new TigerseyeClassPathContainerInitializer();
            tigerseyeClassPathContainerInitializer.initialize(
        	    TigerseyeClasspathContainer.CONTAINER_ID, jp);
        } catch (CoreException e) {
	    logger.warn("Failed updating project {}", jp.getProject().getName());
        }
    }

    static boolean isTigerseyeNature(IProject iProject) {
        try {
            return iProject.isNatureEnabled(DSLNature.TIGERSEYE_NATURE_ID);
        } catch (CoreException e) {
            return false;
        }
    }

    /**
     * Adds only the tigerseye runtime configuration, without any DSLs, to the
     * specified project
     * 
     * @param project
     */
    public static void addTigersEyeRuntimeConfiguration(IProject project) {
	logger.debug("Adding tigerseye configuration for project {} ", project);
        setNecessaryNatures(project);
        initializeNecessaryClasspathContainer(project);
    }

    /**
     * Adds the configuration for a project that uses DSLs
     * 
     * @param project
     */
    public static void addTigerseyeDSLUsingConfiguration(IProject project) {
        addTigersEyeRuntimeConfiguration(project);
    }

    private static void initializeNecessaryClasspathContainer(IProject project) {
        IJavaProject javaProject = JavaCore.create(project);
        addTigerseyeRuntimeLibraries(javaProject);
        GroovyRuntime.addGroovyClasspathContainer(javaProject);
    }

    public static void addTigerseyeRuntimeLibraries(IJavaProject javaProject) {
        try {
            new TigerseyeClassPathContainerInitializer().initialize(
        	    TigerseyeClasspathContainer.CONTAINER_ID, javaProject);
        } catch (CoreException e) {
	    logger.error("Failed to add Tigerseye runtime libraries to {}",
        	    javaProject.getProject().getName(), e);
	    throw new TigerseyeRuntimeException(e);
        }
    }

    public static void setNecessaryNatures(IProject project) {
        try {
            final IProjectDescription description = project.getDescription();
            List<String> natureIds = new LinkedList<String>();
            Collections.addAll(natureIds, description.getNatureIds());
            String[] necessaryNatures = new String[] { //
            DSLNature.TIGERSEYE_NATURE_ID,//
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

}
