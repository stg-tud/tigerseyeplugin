package de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model;

import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartKeyword;

/**
 * A callback interface for change listeners to the PopartLanguageModel.
 *
 * @author David Marx
 * @author Thorsten Peter
 */
public interface PopartLanguageModelChangeListener {
	
	/**
	 * Called if a keyword was added to the model.
	 * 
	 * @param keyword The keyword which caused the change
	 */
	public void keywordAdded(PopartKeyword keyword);
	
	/**
	 * Called if a keyword was removed from the model.
	 * 
	 * @param keyword The keyword which caused the change
	 */
	public void keywordRemoved(PopartKeyword keyword);

}
