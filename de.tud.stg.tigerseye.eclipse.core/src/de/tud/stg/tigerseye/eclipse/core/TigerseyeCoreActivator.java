package de.tud.stg.tigerseye.eclipse.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.internal.DSLActivationState;
import de.tud.stg.tigerseye.eclipse.core.internal.DSLConfigurationElementResolver;
import de.tud.stg.tigerseye.eclipse.core.internal.PluginDSLConfigurationElement;
import de.tud.stg.tigerseye.eclipse.core.runtime.ProjectLinker;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Leo_Roos
 */
public class TigerseyeCoreActivator extends AbstractUIPlugin {

    private static final Logger logger = LoggerFactory
	    .getLogger(TigerseyeCoreActivator.class);

    // The plug-in ID
    public static final String PLUGIN_ID = "de.tud.stg.tigerseye.eclipse.core"; //$NON-NLS-1$ // NO_UCD

    private static final String unicodeLookupTablePath = "UnicodeLookupTable.txt";

    // The shared instance
    private static TigerseyeCoreActivator plugin;

    private boolean activeDSLslinked = false;

    /**
     * The constructor
     */
    public TigerseyeCoreActivator() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void start(BundleContext context) throws Exception {
	super.start(context);
	plugin = this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void stop(BundleContext context) throws Exception {
	plugin = null;
	super.stop(context);
    }

    /**
     * @param imageName
     * @return predefined icon from the default image store.
     */
    public static ImageDescriptor getTigerseyeImage(TigerseyeImage imageName) {
	String imagePath = "/icons/" + imageName.imageName;
	return getImageDescriptor(imagePath);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static TigerseyeCoreActivator getDefault() {
	return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in
     * relative path
     * 
     * @param path
     *            the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
	return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    /**
     * @return a reader for the MathClassEx resource, which defines the mapping
     *         for special character naming.
     */
    public static Reader getUnicodeLookupTableResource() {
	TigerseyeCoreActivator theInstance = getDefault();
	if (theInstance == null) {
	    // plug-in not activated try to retrieve from project root
	    try {
		return new FileReader(unicodeLookupTablePath);
	    } catch (FileNotFoundException e) {
		throw new IllegalStateException("Could not resolve resource "
			+ unicodeLookupTablePath);
	    }
	}
	URL entry = theInstance.getBundle().getEntry(unicodeLookupTablePath);
	if (entry == null)
	    throw new IllegalStateException("Could not resolve entry for"
		    + unicodeLookupTablePath);
	try {
	    InputStream openStream = entry.openStream();
	    return new InputStreamReader(openStream, "UTF-8");
	} catch (UnsupportedEncodingException e) {
	    throw new IllegalStateException(e);
	} catch (IOException e) {
	    throw new IllegalStateException(e);
	}
    }

    public boolean isActiveDSLsLinked() {
	return activeDSLslinked;
    }

    // FIXME(Leo_Roos;Aug 25, 2011) should be tested
    public void linkActiveDSLProjectsIntoWorkspace() {
	Set<PluginDSLConfigurationElement> installedDSLConfigurationElements = DSLConfigurationElementResolver
		.getInstalledDSLConfigurationElements();
	for (PluginDSLConfigurationElement confEl : installedDSLConfigurationElements) {
	    Boolean active = DSLActivationState.getValue(confEl.getId(),
		    getDefault().getPreferenceStore());
	    String id = confEl.getContributor().getId();
	    if (active) {
		Bundle bundle2 = Platform.getBundle(id);
		if (DSLConfigurationElementResolver
			.isBundleWorkspaceProject(bundle2)) {
		    // is linked in this workspace

		    try {
			File bundleFile = FileLocator.getBundleFile(bundle2);
			IProject linkProject = new ProjectLinker().linkProject(
				bundleFile.toURI(), id);
			if (linkProject == null)
			    logger.warn("unexpected problem");
			else
			    linkProject.open(null);
		    } catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		}
	    }
	}
	this.activeDSLslinked = true;
    }

    private void linkPluginIntoWorkspace(IPluginModelBase findModel) {
	String installLocation = findModel.getInstallLocation();
	URI uri = new File(installLocation).toURI();
	String id2 = findModel.getPluginBase().getId();
	try {
	    new ProjectLinker().linkProject(uri, id2);
	} catch (CoreException e) {
	    logger.error("Failed to link project " + id2
		    + " from install location" + installLocation);
	}
    }

}
