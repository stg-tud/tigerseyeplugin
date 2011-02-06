package de.tud.stg.popart.builder.transformers;

import java.util.Set;

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
