package de.tud.stg.tigerseye.eclipse.core;

import java.util.ArrayList;

import org.apache.commons.lang.UnhandledException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;

import de.tud.stg.popart.builder.transformers.Transformation;
import de.tud.stg.tigerseye.eclipse.core.internal.LanguageProviderImpl;

/**
 * Provides access to this plug-ins preference store and bundle. Additionally it
 * provides access to registered {@link DSLDefinition}s and
 * {@link Transformation}s.
 * 
 * @author Leo Roos
 * 
 */
public class TigerseyeCore {

    public static IPreferenceStore getPreferences() {
	return TigerseyeCoreActivator.getDefault().getPreferenceStore();
    }

    public static ImageDescriptor getImage(TigerseyeImage imageName) {
	String imagePath = "/icons/" + imageName.imageName;
	return TigerseyeCoreActivator.getImageDescriptor(imagePath);
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
     * Returns the registered {@link Transformation} classes. This method will
     * return a Collection of new Transformation objects every time it is
     * invoked.
     * 
     * @return registered Transformations wrapped in a
     *         {@link TransformationHandler}
     */
    public static ArrayList<TransformationHandler> getConfiguredTransformations() {
	IConfigurationElement[] config = Platform.getExtensionRegistry()
		.getConfigurationElementsFor(TransformationHandler.ID);
	try {
	    ArrayList<TransformationHandler> transformationsList = new ArrayList<TransformationHandler>();
	    for (IConfigurationElement configEl : config) {
		for (IConfigurationElement children : configEl.getChildren()) {
		    String name = children.getAttribute("name");
		    Transformation t = (Transformation) children
			    .createExecutableExtension("class");
		    TransformationHandler handler = new TransformationHandler(
			    name, t);
		    transformationsList.add(handler);
		}
	    }
	    return transformationsList;
	} catch (CoreException e) {
	    throw new UnhandledException(
		    "Failed to create a registered Transformations. Check the implementation.",
		    e);
	}
    }

}
