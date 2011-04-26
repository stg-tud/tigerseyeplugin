package de.tud.stg.popart.eclipse.wizards.popartLanguageWizard;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartLiteralKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartStructuredElementKeyword;

/**
 * A label provider for PopartKeywords.
 * 
 * The NewPopartLanguageKeywordPage uses this provider for its viewer.
 *
 * @author David Marx
 * @author Thorsten Peter
 */
public class PopartLanguageModelLabelProvider extends LabelProvider {

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(Object)
	 */
	@Override
	public Image getImage(Object element) {
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(Object)
	 */
	@Override
	public String getText(Object element) {
		PopartKeyword keyword = (PopartKeyword) element;	
		String type = "";
		if (keyword instanceof PopartLiteralKeyword) {
			type = "(L)";
		}
		if (keyword instanceof PopartOperationKeyword) {
			type = "(O)";
		}
		if (keyword instanceof PopartStructuredElementKeyword) {
			type = "(S)";
		}
		return type + " " + keyword.getName();
	}

}
