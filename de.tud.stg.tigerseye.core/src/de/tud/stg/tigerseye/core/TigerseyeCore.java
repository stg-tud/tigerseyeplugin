package de.tud.stg.tigerseye.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.popart.builder.eclipse.DSLNature;
import de.tud.stg.popart.builder.eclipse.TigerseyeClassPathContainerInitializer;
import de.tud.stg.popart.eclipse.LanguageProviderImpl;
import de.tud.stg.tigerseye.core.preferences.TigerseyePreferenceConstants;

/**
 * Core class providing core plug-in functionality needed by many other code
 * parts and extending plug-ins.
 * 
 * @author Leo Roos
 * 
 */
public class TigerseyeCore {
    private static final Logger logger = LoggerFactory
	    .getLogger(TigerseyeCore.class);

    public static IPreferenceStore getPreferences() {
	return TigerseyeCoreActivator.getDefault().getPreferenceStore();
    }

    public static ImageDescriptor getImageByName(String imageName) {
	String imagePath = "/icons/" + imageName;
	return TigerseyeCoreActivator.getImageDescriptor(imagePath);
    }

    public static String getOutputDirectoryPath() {
	String outputfolder = getPreferences().getString(
		TigerseyePreferenceConstants.TIGERSEYE_OUTPUT_FOLDER_PATH);
	return outputfolder;
    }

    public static Bundle getBundle() {
	return TigerseyeCoreActivator.getDefault().getBundle();
    }

    /**
     * Provides the object which gives access to registered DSLs. Clients should
     * not cache the language provider, since it might change when new DSL
     * plug-ins are added or old ones removed.
     * 
     * @return an updated language provider
     */
    public static ILanguageProvider getLanguageProvider() {
	return new LanguageProviderImpl(TigerseyeCore.getPreferences());
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

    private static void updateTigerseyeCPContainerofProject(IJavaProject jp) {
	try {
	    new TigerseyeClassPathContainerInitializer().initialize(
		    TigerseyeClassPathContainerInitializer.TIGERSEYE_SUPPORT, jp);
	} catch (CoreException e) {
	    logger.warn("Failed updating project {}", jp.getProject().getName());
	}
    }

    private static boolean isTigerseyeNature(IProject iProject) {
	try {
	    return iProject.isNatureEnabled(DSLNature.TIGERSEYE_NATURE_ID);
	} catch (CoreException e) {
	    return false;
	}
    }

}
