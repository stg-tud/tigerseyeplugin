package de.tud.stg.tigerseye.eclipse.core;

import java.util.List;

import de.tud.stg.popart.builder.transformers.Transformation;

/**
 * Provides access to registered transformations
 * 
 * @author Leo Roos
 * 
 */
public interface ITransformationProvider {

    /**
     * Returns the registered {@link Transformation} classes. This method will
     * return a Collection of new Transformation objects every time it is
     * invoked.
     * 
     * @return registered Transformations wrapped in a
     *         {@link TransformationHandler}
     */
    public List<TransformationHandler> getConfiguredTransformations();

}