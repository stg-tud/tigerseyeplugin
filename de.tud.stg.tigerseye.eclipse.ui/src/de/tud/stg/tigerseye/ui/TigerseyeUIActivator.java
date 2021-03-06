package de.tud.stg.tigerseye.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.tud.stg.tigerseye.eclipse.core.TigerseyeImage;
import de.tud.stg.tigerseye.ui.preferences.TigerseyeUIPreferenceInitializer;

/**
 * The activator class controls the plug-in life cycle
 */
public class TigerseyeUIActivator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "de.tud.stg.tigerseye.ui"; //$NON-NLS-1$

    // The shared instance
    private static TigerseyeUIActivator plugin;

    /**
     * The constructor
     */
    public TigerseyeUIActivator() {
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
	/*
	 * can not use the preference initializer extension since he will be
	 * only invoked once for every preference store, but I reuse the
	 * TigerseyeCore preference store
	 */
	new TigerseyeUIPreferenceInitializer().initialize();
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
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static TigerseyeUIActivator getDefault() {
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
     * @param imageName
     * @return predefined icon from the default image store.
     */
    public static ImageDescriptor getTigerseyeImage(TigerseyeImage imageName) {
	String imagePath = "/icons/" + imageName.imageName;
	return getImageDescriptor(imagePath);
    }
}
