package de.tud.stg.tigerseye.eclipse.core.builder.transformers;

import java.util.Set;

import de.tud.stg.tigerseye.eclipse.core.api.Transformation;

/**
 * The interface for all textual transformations that act as a preprocessor before parsing
 * 
 * @author Kamil Erhard
 * 
 */
public interface TextualTransformation extends Transformation {
	public StringBuffer transform(Context context, StringBuffer sb);

	public Set<String> getRequirements();

	public Set<String> getAssurances();

}