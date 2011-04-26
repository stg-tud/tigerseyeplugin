package de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartLiteralKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartStructuredElementKeyword;

/**
 * The model for the NewPopartLanguageWizzard. This class stores the
 * DSL language elements specified during the definition of a
 * Popart language using the above-noted wizzard.
 *
 * @author David Marx
 * @author Thorsten Peter
 */
public class PopartLanguageModel {

	private static final PopartLanguageModel INSTANCE = new PopartLanguageModel();
	private Set<PopartLanguageModelChangeListener> listeners = new HashSet<PopartLanguageModelChangeListener>();
	private Set<PopartKeyword> keywords = new HashSet<PopartKeyword>();
	
	/**
	 * Private constructor. Not to be instantiated.
	 */
	private PopartLanguageModel() {
	}

	/**
	 * Returns the single instance of this PopartLanguageModel
	 * 
	 * @return The single instance
	 */
	public static PopartLanguageModel getInstance() {
		return INSTANCE;
	}

	/**
	 * Adds the specified keyword to this model.
	 * 
	 * @param keyword The PopartKeyword
	 * @return True if this model did not already contain the
	 * 		   specified keyword 
	 */
	public boolean addKeyword(PopartKeyword keyword) {
		boolean result = this.keywords.add(keyword);
		signalKeywordAdded(keyword);		
		return result;
	}

	/**
	 * Removes the specified keyword from this model.
	 * 
	 * @param keyword The PopartKeyword
	 * @return True if this model contained the
	 * 		   specified keyword 
	 */
	public boolean removeKeyword(PopartKeyword keyword) {
		boolean result = this.keywords.remove(keyword);
		signalKeywordRemoved(keyword);
		return result;
	}

	/**
	 * Returns all keywords currently stored in this model.
	 * 
	 * @return All keywords
	 */
	public Object[] getKeywords() {
		return keywords.toArray();
	}
	
	/**
	 * Returns a list of the keywords currently stored in this model.
	 * 
	 * @return All keywords
	 */
	public ArrayList<PopartKeyword> getKeywordList() {
		
		ArrayList<PopartKeyword> result = new ArrayList<PopartKeyword>();
		
		for (Iterator<PopartKeyword> iterator = keywords.iterator(); iterator.hasNext();) {
			PopartKeyword keyword = iterator.next();
			if (keyword instanceof PopartLiteralKeyword) {
				result.add(keyword);
			}			
		}
		
		for (Iterator<PopartKeyword> iterator = keywords.iterator(); iterator.hasNext();) {
			PopartKeyword keyword = iterator.next();
			if (keyword instanceof PopartOperationKeyword) {
				result.add(keyword);
			}			
		}
		
		for (Iterator<PopartKeyword> iterator = keywords.iterator(); iterator.hasNext();) {
			PopartKeyword keyword = iterator.next();
			if (keyword instanceof PopartStructuredElementKeyword) {
				result.add(keyword);
			}			
		}
		
		return result;
	}

	/**
	 * Adds a change listener for this model.
	 * 
	 * @param listener The change listener
	 */
	public void addChangeListener(PopartLanguageModelChangeListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * Removes a change listener from this model.
	 * 
	 * @param listener The change listener
	 */
	public void removeChangeListener(PopartLanguageModelChangeListener listener) {
		this.listeners.remove(listener);
	}

	/**
	 * Notifies all PopartLanguageModelChangeListeners about a added keyword.
	 * 
	 * @param keyword The added keyword
	 */
	private void signalKeywordAdded(PopartKeyword keyword) {
		for (PopartLanguageModelChangeListener listener : this.listeners) {
			listener.keywordAdded(keyword);
		}
	}

	/**
	 * Notifies all PopartLanguageModelChangeListeners about a removed keyword.
	 * 
	 * @param keyword The removed keyword
	 */
	private void signalKeywordRemoved(PopartKeyword keyword) {
		for (PopartLanguageModelChangeListener listener : this.listeners) {
			listener.keywordRemoved(keyword);
		}
	}

	/**
	 * Clears keywords and listeners from this model.
	 */
	public void reset() {
		keywords.clear();
		listeners.clear();		
	}

}
