package de.tud.stg.popart.builder.transformers;

/**
 * Implemented by classes which need to be identifiable by a specific if possible unique key. 
 * 
 * @author Leo Roos
 */
public interface TransformationType {

	String getIdentifer();

    FileType getTransformationCategory();
}
