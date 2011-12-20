package de.tud.stg.tigerseye.eclipse.core.internal;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import de.tud.stg.tigerseye.eclipse.core.api.ITransformationHandler;
import de.tud.stg.tigerseye.eclipse.core.api.ITransformationProvider;
import de.tud.stg.tigerseye.eclipse.core.api.TigerseyeRuntimeException;
import de.tud.stg.tigerseye.eclipse.core.api.Transformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TransformationHandler;


public class TransformationProviderImpl implements ITransformationProvider {

    private final IConfigurationElement[] configurationElementsFor;

    public TransformationProviderImpl(IConfigurationElement[] configurationElementsFor) {
	this.configurationElementsFor = configurationElementsFor;
    }

    /* (non-Javadoc)
     * @see de.tud.stg.tigerseye.eclipse.core.internal.ITransformationProvider#getConfiguredTransformations()
     */
    @Override
    public ArrayList<ITransformationHandler> getConfiguredTransformations() {
	try {
	    ArrayList<ITransformationHandler> transformationsList = new ArrayList<ITransformationHandler>();
	    for (IConfigurationElement configEl : configurationElementsFor) {
		String contributor = configEl.getContributor().getName();
		for (IConfigurationElement children : configEl.getChildren()) {
		    String name = children.getAttribute("name");
		    Transformation t = (Transformation) children
			    .createExecutableExtension("class");
		    TransformationHandler handler = new TransformationHandler(
			    contributor, name, t);
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
