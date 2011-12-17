package de.tud.stg.tigerseye.eclipse.core.api;

import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;

/**
 * Wrapper around {@link Transformation} adds some additional properties without
 * breaking other dependencies the wrapped interface.
 * 
 * @author Leo_Roos
 * 
 */
public interface ITransformationHandler {

    /**
     * The id for the transformations extension point
     */
    public static final String ID = "de.tud.stg.tigerseye.transformers";

    /**
     * @return the unique identifier for this Transformation
     */
    String getIdentifier();

    /**
     * @return the registered {@link Transformation} object.
     */
    Transformation getTransformation();

    /**
     * @return The user friendly name under which the {@link Transformation}
     *         object of this handler has been registered.
     */
    String getName();

    /**
     * FileTypes this transformer is intended for.
     * 
     * @see Transformation#getSupportedFileTypes()
     */
    boolean supports(FileType type);

}