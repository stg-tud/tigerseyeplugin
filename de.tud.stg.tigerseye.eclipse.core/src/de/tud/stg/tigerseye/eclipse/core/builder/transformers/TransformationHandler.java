package de.tud.stg.tigerseye.eclipse.core.builder.transformers;

import java.util.Set;

import javax.annotation.Nonnull;

import de.tud.stg.tigerseye.eclipse.core.api.ITransformationHandler;
import de.tud.stg.tigerseye.eclipse.core.api.Transformation;

/**
 * This class wraps actual {@link Transformation} objects and provides access to
 * meta data such as preference values.
 * 
 * @author Leo Roos
 * 
 */
@Nonnull
public class TransformationHandler implements ITransformationHandler {

    private final String contributor;

    private final String name;

    private final Transformation transformation;

    public TransformationHandler(String contributor, String name, Transformation transformation) {
	this.contributor = contributor;
	this.name = name;
	this.transformation = transformation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.tud.stg.tigerseye.eclipse.core.builder.transformers.ITransformationHandler
     * #getIdentifier()
     */
    @Override
    public String getIdentifier() {
	return this.contributor + getTransformation().getClass().getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.tud.stg.tigerseye.eclipse.core.builder.transformers.ITransformationHandler
     * #getName()
     */
    @Override
    public String getName() {
	return this.name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.tud.stg.tigerseye.eclipse.core.builder.transformers.ITransformationHandler
     * #getTransformation()
     */
    @Override
    public Transformation getTransformation() {
	return this.transformation;
    }

    @Override
    public String toString() {
	return getClass().getSimpleName() + "[" + getName() + "]";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.tud.stg.tigerseye.eclipse.core.builder.transformers.ITransformationHandler
     * #supports(de.tud.stg.tigerseye.eclipse.core.api.TransformationType)
     */
    @Override
    public boolean supports(FileType type) {
	Set<FileType> supportedFileTypes = getTransformation().getSupportedFileTypes();
	return supportedFileTypes.contains(type);
    }

}
