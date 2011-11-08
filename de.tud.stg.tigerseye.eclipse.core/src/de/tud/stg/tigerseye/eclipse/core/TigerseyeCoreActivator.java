package de.tud.stg.tigerseye.eclipse.core;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class TigerseyeCoreActivator extends AbstractUIPlugin {

    static final Logger logger = LoggerFactory.getLogger(TigerseyeCoreActivator.class);

    // The plug-in ID
    public static final String PLUGIN_ID = "de.tud.stg.tigerseye.core"; //$NON-NLS-1$ // NO_UCD

    // The shared instance
    private static TigerseyeCoreActivator plugin;

    private final AtomicBoolean haveMadeInitialConsistencyCheck = new AtomicBoolean(false);

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
	    throw new IllegalStateException("Tried to access shared instance, but this plugin has not been activated");
	makeInitialConsistencyCheckIfNecessary();
	return plugin;
    }

    private static void makeInitialConsistencyCheckIfNecessary() {
	if (!plugin.haveMadeInitialConsistencyCheck.getAndSet(true)) {
	    StartupValidation.scheduleConsistencyCheck(plugin.getPreferenceStore());
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



}
