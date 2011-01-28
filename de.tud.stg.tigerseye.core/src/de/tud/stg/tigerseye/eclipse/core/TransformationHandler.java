package de.tud.stg.tigerseye.eclipse.core;

import javax.annotation.Nonnull;

import de.tud.stg.popart.builder.transformers.Transformation;

/**
 * This class wraps actual transformations and their attributes.
 * 
 * @author Leo Roos
 * 
 */
@Nonnull
public class TransformationHandler {

    /**
     * The id for the transformations extension point
     */
    public static final String ID = "de.tud.stg.tigerseye.transformers";

    private final Transformation transformation;
    private final String name;

    public TransformationHandler(String name, Transformation transformation) {
	this.name = name;
	this.transformation = transformation;
    }

    /**
     * @return the registered {@link Transformation} object.
     */
    public Transformation getTransformation() {
	return this.transformation;
    }

    /**
     * @return The user friendly name under which the transformer has been
     *         registered.
     */
    public String getName() {
	return this.name;
    }

}
