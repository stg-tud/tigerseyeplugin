package de.tud.stg.popart.eclipse.wizards.popartLanguageWizard;

import legacy.org.codehaus.groovy.eclipse.wizards.WizardUtil;

import org.apache.commons.lang.UnhandledException;
import org.codehaus.groovy.eclipse.wizards.NewClassWizard;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.INewWizard;

import de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.PopartLanguageModel;
import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeRuntime;
import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeRuntimeConstants;

/**
 * Wizard for creating language definitions
 * 
 * @author David Marx
 * @author Thorsten Peter
 */
public class NewPopartLanguageWizzard extends NewClassWizard implements INewWizard {

	private NewPopartLanguageKeywordPage keywordPage;
	private NewPopartLanguageWizardPage newClassPage;
	
    public NewPopartLanguageWizzard() {
	super();
	setWindowTitle("Create new Tigerseye Language.");
    }

	@Override
	public void addPages() {
		newClassPage = new NewPopartLanguageWizardPage();
		newClassPage.init(getSelection());		
		addPage(newClassPage);		
		
		keywordPage = new NewPopartLanguageKeywordPage("");
	keywordPage.setTitle("New Tigersye Language Definition.");
	keywordPage
		.setDescription("Create a new Tigerseye language definition.");
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
	    throw new UnhandledException(e);
	}
	dialogAskToAddNature();
	return true;
    }

    private void dialogAskToAddNature() {
	IJavaProject javaProject = newClassPage.getJavaProject();
	boolean hasNature = false;
	try {
	    hasNature = javaProject.getProject().hasNature(
		    TigerseyeRuntimeConstants.TIGERSEYE_NATURE_ID);
	} catch (CoreException e) {
	}
	if (!hasNature) {
	boolean openQuestion = MessageDialog.openQuestion(getShell(),
		"Add Tigerseye Nature",
		"Do you want to add the Tigerseye runtime libraries?");
	if (openQuestion) {	
	TigerseyeRuntime
		.addTigersEyeRuntimeConfiguration(javaProject.getProject());
	}
	}
    }
}	