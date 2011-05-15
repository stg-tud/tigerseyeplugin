package de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars;

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
