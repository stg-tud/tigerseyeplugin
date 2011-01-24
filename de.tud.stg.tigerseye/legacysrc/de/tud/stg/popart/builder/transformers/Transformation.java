package de.tud.stg.popart.builder.transformers;

import java.util.Set;

/**
 * The base interface for all Transformations
 * 
 * @author Kamil Erhard
 * 
 */
public interface Transformation {
	public String getDescription();

	public Set<Filetype> getSupportedFiletypes();
}
