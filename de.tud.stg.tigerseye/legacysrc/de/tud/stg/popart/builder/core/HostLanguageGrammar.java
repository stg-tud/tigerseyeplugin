package de.tud.stg.popart.builder.core;

import de.tud.stg.parlex.core.IGrammar;

/**
 * {@link HostLanguageGrammar} defines grammar rules for a concrete host language
 * 
 * @author Kamil Erhard
 * 
 */
public interface HostLanguageGrammar {
	public void applySpecificGrammar(IGrammar<String> grammar);
}
