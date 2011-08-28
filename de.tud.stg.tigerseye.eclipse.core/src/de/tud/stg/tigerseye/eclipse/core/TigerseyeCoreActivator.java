package de.tud.stg.tigerseye.eclipse.core;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import de.tud.stg.tigerseye.eclipse.core.api.ILanguageProvider;
import de.tud.stg.tigerseye.eclipse.core.internal.DSLActivationState;
import de.tud.stg.tigerseye.eclipse.core.internal.DSLConfigurationElementResolver;
import de.tud.stg.tigerseye.eclipse.core.internal.LanguageProviderFactory;
import de.tud.stg.tigerseye.eclipse.core.internal.PluginDSLConfigurationElement;
import de.tud.stg.tigerseye.eclipse.core.runtime.ProjectLinker;

/**
 * The activator class controls the plug-in life cycle
 */
public class TigerseyeCoreActivator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "de.tud.stg.tigerseye.core"; //$NON-NLS-1$ // NO_UCD

    // The shared instance
    private static TigerseyeCoreActivator plugin;

    private static final String unicodeLookupTablePath = "UnicodeLookupTable.txt";

    private boolean haveMadeInitialConsistencyCheck;

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
    /*
     * don't use this method inside this class to avoid loops
     */
    public static TigerseyeCoreActivator getDefault() {
	if (!isRunning())
	    throw new IllegalStateException(
		    "Tried to access shared instance, but this plugin has not been activated");
	makeInitialConsistencyCheckIfNecessary();
	return plugin;
    }

    private static void makeInitialConsistencyCheckIfNecessary() {
	if (!plugin.haveMadeInitialConsistencyCheck) {
	    plugin.linkActiveDSLProjectsIntoWorkspace();
	    ILanguageProvider provider = new LanguageProviderFactory()
		    .createLanguageProvider(plugin.getPreferenceStore());
	    provider.validateDSLDefinitionsState();
	    plugin.haveMadeInitialConsistencyCheck = true;
	}
    }

    public static boolean isRunning() {
	return plugin != null;
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
	if (!isRunning()) {
	    // plug-in not activated try to retrieve from project root
	    try {
		return new FileReader(unicodeLookupTablePath);
	    } catch (FileNotFoundException e) {
		throw new IllegalStateException("Could not resolve resource "
			+ unicodeLookupTablePath);
	    }
	}
	URL entry = plugin.getBundle().getEntry(unicodeLookupTablePath);
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

    // FIXME(Leo_Roos;Aug 25, 2011) should be tested
    public void linkActiveDSLProjectsIntoWorkspace() {
	Set<PluginDSLConfigurationElement> installedDSLConfigurationElements = DSLConfigurationElementResolver
		.getInstalledDSLConfigurationElements();
	for (PluginDSLConfigurationElement confEl : installedDSLConfigurationElements) {
	    Boolean active = DSLActivationState.getValue(confEl.getId(),
		    plugin.getPreferenceStore());
	    String id = confEl.getContributor().getId();
	    if (active) {
		Bundle bundle = Platform.getBundle(id);
		if (DSLConfigurationElementResolver
			.isBundleWorkspaceProject(bundle)) {
		    // is linked in this workspace
		    ProjectLinker.linkOpenedPluginIntoWorkspace(bundle);
		}
	    }
	}
    }

}
