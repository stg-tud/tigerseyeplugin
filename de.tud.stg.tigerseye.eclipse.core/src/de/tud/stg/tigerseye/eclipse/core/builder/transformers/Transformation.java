package de.tud.stg.tigerseye.eclipse.core.builder.transformers;

import java.util.Set;

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

}
