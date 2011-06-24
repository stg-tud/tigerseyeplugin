package de.tud.stg.tigerseye.eclipse.core.api;

import java.util.Collection;

import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TransformationHandler;

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
    public Collection<ITransformationHandler> getConfiguredTransformations();

    // <T extends Transformation> Set<T> getTransformations(
    // TransformationFilter<T> filter, TransformationType... types);

}