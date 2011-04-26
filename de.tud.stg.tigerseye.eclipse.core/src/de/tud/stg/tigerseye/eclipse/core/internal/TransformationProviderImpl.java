package de.tud.stg.tigerseye.eclipse.core.internal;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.preference.IPreferenceStore;

import de.tud.stg.tigerseye.eclipse.core.ITransformationProvider;
import de.tud.stg.tigerseye.eclipse.core.TransformationHandler;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.Transformation;
import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeRuntimeException;


public class TransformationProviderImpl implements ITransformationProvider {

    private final IPreferenceStore preferences;
    private final IConfigurationElement[] configurationElementsFor;

    public TransformationProviderImpl(IPreferenceStore preferences,
	    IConfigurationElement[] configurationElementsFor) {
	this.preferences = preferences;
	this.configurationElementsFor = configurationElementsFor;
    }

    private IPreferenceStore getPreferences() {
	return preferences;
    }

    /* (non-Javadoc)
     * @see de.tud.stg.tigerseye.eclipse.core.internal.ITransformationProvider#getConfiguredTransformations()
     */
    @Override
    public ArrayList<TransformationHandler> getConfiguredTransformations() {
	try {
	    ArrayList<TransformationHandler> transformationsList = new ArrayList<TransformationHandler>();
	    for (IConfigurationElement configEl : configurationElementsFor) {
		String contributor = configEl.getContributor().getName();
		for (IConfigurationElement children : configEl.getChildren()) {
		    String name = children.getAttribute("name");
		    Transformation t = (Transformation) children
			    .createExecutableExtension("class");
		    TransformationHandler handler = new TransformationHandler(
			    contributor, name, t);
		    handler.setPreferenceStore(getPreferences());
		    transformationsList.add(handler);
		}
	    }
	    return transformationsList;
	} catch (CoreException e) {
	    throw new TigerseyeRuntimeException(
		    "Failed to create a registered Transformation. Check the implementation.",
		    e);
	}
    }

}
