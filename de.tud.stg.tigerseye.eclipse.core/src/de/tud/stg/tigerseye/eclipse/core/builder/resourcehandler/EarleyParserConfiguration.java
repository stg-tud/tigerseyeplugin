package de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler;

import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.lexer.KeywordSensitiveLexer;
import de.tud.stg.parlex.lexer.KeywordSeperator;
import de.tud.stg.parlex.parser.earley.EarleyParser;

/**
 * Class to centralize {@link EarleyParser} configuration. By doing that I can
 * assure to use the same configuration for tests and production code.
 * 
 * @author Leo_Roos
 * 
 */
public class EarleyParserConfiguration {

    public EarleyParser getDefaultEarleyParserConfiguration(IGrammar<String> grammar) {
	KeywordSensitiveLexer ksl = new KeywordSensitiveLexer(new KeywordSeperator());
	EarleyParser parser = new EarleyParser(ksl, grammar);
	// actually activate oracles
	parser.detectUsedOracles();
	return parser;
    }

}
