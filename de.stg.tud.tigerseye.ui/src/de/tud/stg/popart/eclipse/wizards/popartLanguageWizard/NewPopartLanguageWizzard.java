package de.tud.stg.popart.eclipse.wizards.popartLanguageWizard;

import legacy.org.codehaus.groovy.eclipse.wizards.WizardUtil;

import org.codehaus.groovy.eclipse.wizards.NewClassWizard;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.INewWizard;

import de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.PopartLanguageModel;

/**
 * Wizard for creating language definitions
 * 
 * @author David Marx
 * @author Thorsten Peter
 */
public class NewPopartLanguageWizzard extends NewClassWizard implements INewWizard {

	private NewPopartLanguageKeywordPage keywordPage;
	private NewPopartLanguageWizardPage newClassPage;
	
	@Override
	public void addPages() {
		newClassPage = new NewPopartLanguageWizardPage();
		newClassPage.init(getSelection());		
		addPage(newClassPage);		
		
		keywordPage = new NewPopartLanguageKeywordPage("");
		keywordPage.setTitle("Create a new popart language definition");
		keywordPage.setDescription("");
		addPage(keywordPage);
		
		PopartLanguageModel.getInstance().reset();
		WizardUtil.clearCreatedTypes();
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.codehaus.groovy.eclipse.wizards.NewClassWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {	
		keywordPage.disposeCurrentView();		
		IFile file;
		try {			
			newClassPage.createKewords();
			file = newClassPage.createLanguageClass();
			openResource(file);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
}	