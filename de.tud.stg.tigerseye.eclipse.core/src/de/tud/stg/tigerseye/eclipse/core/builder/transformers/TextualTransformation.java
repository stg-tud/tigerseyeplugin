package de.tud.stg.tigerseye.eclipse.core.builder.transformers;

import java.util.Map;
import java.util.Set;

import de.tud.stg.tigerseye.eclipse.core.api.Transformation;

/**
 * The interface for all textual transformations that act as a preprocessor
 * before parsing
 * 
 * @author Kamil Erhard
 * @author Leo_Roos
 */
public interface TextualTransformation extends Transformation {

    /**
     * Performs textual transformations on the {@code input} and returns the
     * transformed result.
     * 
     * @param context
     *            information about the context of the input
     * @param input
     *            the current input result
     * @param data
     *            additional Information that Transformers can use pass results
     *            between each other.
     * @return the transformed input
     */
    public String transform(Context context, String input, Map<String, Object> data);

	//XXX(Leo_Roos;Nov 22, 2011) currentyl ignored maybe remove?
	public Set<String> getRequirements();

    // XXX(Leo_Roos;Nov 22, 2011) currentyl ignored maybe remove?
	public Set<String> getAssurances();

}
