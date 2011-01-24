package de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model;

import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartLiteralKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartStructuredElementKeyword;
import de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.generators.PopartLiteralKeywordCodeGenerator;
import de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.generators.PopartOperationKeywordCodeGenerator;
import de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.generators.PopartStructuredElementKeywordCodeGenerator;

/**
 * A factory for PopartKeywordCodeGenerators.
 * Given a specific PopartKeyword, this factory returns the appropriate
 * IPopartKeywordCodeGenerator.
 *
 * @author David Marx
 * @author Thorsten Peter
 */
public class PopartKeywordCodeGeneratorFactory {
	
	/**
	 * Returns the appropriate IPopartKeywordCodeGenerator for the specified PopartKeyword.
	 * 
	 * @param keyword The PopartKeyword
	 * @return The corresponding IPopartKeywordCodeGenerator
	 */
	public static IPopartKeywordCodeGenerator getCodeGenerator(PopartKeyword keyword) {
		if (keyword instanceof PopartLiteralKeyword) {
			return new PopartLiteralKeywordCodeGenerator((PopartLiteralKeyword) keyword);
		}
		else if (keyword instanceof PopartOperationKeyword) {
			return new PopartOperationKeywordCodeGenerator((PopartOperationKeyword) keyword);
		}
		else if (keyword instanceof PopartStructuredElementKeyword) {
			return new PopartStructuredElementKeywordCodeGenerator((PopartStructuredElementKeyword) keyword);
		}
		
		return null;
	}

}
