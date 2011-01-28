package de.tud.stg.popart.builder.eclipse;

import javax.annotation.CheckForNull;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.TigerseyeRuntime;

public class AddNatureAction implements IObjectActionDelegate {
    private static final Logger logger = LoggerFactory
	    .getLogger(AddNatureAction.class);

    private ISelection selection;

    public AddNatureAction() {
    }

    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
    }

    @Override
    public void run(IAction action) {
	logger.info("AddNatureAction run");
	IProject targetProject = getProjectForSelection();
	if (targetProject == null) {
	    logger.debug(
		    "Cannot perform AddNatureAction on selected object {}",
		    selection);
	    return;
	}

	TigerseyeRuntime.addTigersEyeRuntimeConfiguration(targetProject);
    }

    private @CheckForNull
    IProject getProjectForSelection() {
	if (selection == null)
	    return null;
	final IStructuredSelection s = (IStructuredSelection) selection;
	final Object selected = s.getFirstElement();
	IProject targetProject;
	if (selected instanceof IJavaProject) {
	    targetProject = ((IJavaProject) selected).getProject();
	} else if (selected instanceof IProject) {
	    targetProject = (IProject) selected;
	} else {
	    return null;
	}
	return targetProject;
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
	this.selection = selection;
    }
}
