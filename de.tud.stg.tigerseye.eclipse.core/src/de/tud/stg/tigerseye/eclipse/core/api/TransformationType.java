package de.tud.stg.tigerseye.eclipse.core.api;

import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;

/**
 * A wrapper around types that have an activation state for
 * {@link ITransformationHandler}.
 * 
 * 
 * @see FileType
 * @see DSLDefinition
 * 
 * @author Leo Roos
 */
public interface TransformationType {

    boolean isActiveFor(ITransformationHandler handler);

    boolean getDefaultActiveFor(ITransformationHandler handler);

    void setActiveStateFor(ITransformationHandler handler, boolean value);

}
