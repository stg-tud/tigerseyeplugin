package de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.view;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;
import de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.PopartLanguageModel;
import de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.PopartLanguageModelChangeListener;

/**
 * ContentProvider for parameters in OperationKeywordView
 * 
 * @author David Marx
 * @author Thorsten Peter
 */
public class ParameterContentProvider implements IStructuredContentProvider, PopartLanguageModelChangeListener {
	
	private ListViewer viewer;
	private PopartOperationKeyword keyword;
	
	public ParameterContentProvider(PopartOperationKeyword keyword) {
		this.keyword = keyword;
	}
	
	public Object[] getElements(Object inputElement) {		
		return keyword.getParameter();
	}

	public void dispose() {
	}

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

	public void keywordAdded(PopartKeyword keyword) {		
		viewer.add(keyword);		
	}

	public void keywordRemoved(PopartKeyword keyword) {
		viewer.remove(keyword);
	}

}
