package de.tud.stg.popart.eclipse.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.TigerseyeRuntime;

/**
 * NewPopartClassWizzard is used to provide responsibility that makes it
 * possible to add Popart classes.
 * 
 * @see Wizard
 * 
 * @author Yevgen Fanshil
 * @author Leonid Melnyk
 */
public class NewPopartClassWizzard extends NewElementWizard {
	private NewPopartClassWizardPage fPage;

    private static final Logger logger = LoggerFactory
	    .getLogger(NewPopartClassWizzard.class);

	public NewPopartClassWizzard() {
		super();
	setWindowTitle("Create a new Tigerseye class");
	}

	@Override
	public void addPages() {
		fPage = new NewPopartClassWizardPage();
		addPage(fPage);
		fPage.init(getSelection());
	}

    private IFile file;
	@Override
    protected void finishPage(final IProgressMonitor monitor)
	    throws CoreException {

	

	PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

	    @Override
	    public void run() {
		try {
		    file = fPage.createGroovyType(new SubProgressMonitor(
			    monitor, 1));

		    openResource(file);

		    TigerseyeRuntime
.addTigerseyeDSLUsingConfiguration(fPage
			    .getProject().getProject());

		} catch (CoreException e) {
		    logger.error("Error while creating new class", e);
		}
	    }
	});


	}

    @Override
    public IJavaElement getCreatedElement() {
	return fPage.getCreatedType();
    }
}