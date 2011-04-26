package de.tud.stg.popart.eclipse.wizards.popartLanguageWizard;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;

import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartKeyword;
import de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.PopartLanguageModel;
import de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.PopartLanguageModelChangeListener;

/**
 * A content provider which reflects the contents of the PopartLanguageModel.
 * Consequently this content provider provides PopartKeywords to the views it
 * is set as the view's provider.
 * 
 * The NewPopartLanguageKeywordPage uses this provider for its viewer.
 *
 * @author David Marx
 * @author Thorsten Peter
 */
public class PopartLanguageModelContentProvider implements IStructuredContentProvider, PopartLanguageModelChangeListener {
	
	private ListViewer viewer;

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(Object)
	 */
	public Object[] getElements(Object inputElement) {
		PopartLanguageModel model = (PopartLanguageModel) inputElement;
		return model.getKeywords();
	}

	/**
	 * * @see org.eclipse.jface.viewers.IStructuredContentProvider#dispose()
	 */
	public void dispose() {
	}

	/**
	 * * @see org.eclipse.jface.viewers.IStructuredContentProvider#inputChanged(Viewer, Object, Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (this.viewer == null) {
			this.viewer = (ListViewer) viewer;
		}
		if (oldInput != null) {
			PopartLanguageModel model = (PopartLanguageModel) oldInput;
			model.removeChangeListener(this);
		}
		if (newInput != null) {
			PopartLanguageModel model = (PopartLanguageModel) newInput;
			model.addChangeListener(this);
		}
	}

	/**
	 * * @see de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.PopartLanguageModelChangeListener#keywordAdded(PopartKeyword)
	 */
	public void keywordAdded(PopartKeyword keyword) {		
		viewer.add(keyword);		
	}

	/**
	 * * @see de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.PopartLanguageModelChangeListener#keywordRemoved(PopartKeyword)
	 */
	public void keywordRemoved(PopartKeyword keyword) {
		viewer.remove(keyword);
	}

}
