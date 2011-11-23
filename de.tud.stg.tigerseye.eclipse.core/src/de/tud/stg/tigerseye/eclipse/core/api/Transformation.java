package de.tud.stg.tigerseye.eclipse.core.api;

import java.util.Set;

import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;

/**
 * The Base interface for all Transformations. Implementing classes are expected
 * to provide a zero argument constructor, since this class will be instantiated
 * via introspection.
 * 
 * @author Kamil Erhard
 * 
 */
public interface Transformation {

	/**
	 * @return A description of the transformation this class performs.
	 */
	public String getDescription();

	/**
	 * @return the {@link FileType} for which this transformer can be used.
	 */
	public Set<FileType> getSupportedFileTypes();

    /**
     * Determines when this Transformation has to be build
     * 
     * @return the priority when to execute this transformation.
     */
    public int getBuildOrderPriority();
    // XXX(leo;20.11.2011) add uniform transform statement or two! so they can
    // be

}
