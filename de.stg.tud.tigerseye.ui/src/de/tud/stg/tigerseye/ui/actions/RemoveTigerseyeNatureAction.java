package de.tud.stg.tigerseye.ui.actions;

import javax.annotation.CheckForNull;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeRuntime;

public class RemoveTigerseyeNatureAction implements IObjectActionDelegate {

    private static final Logger logger = LoggerFactory
	    .getLogger(RemoveTigerseyeNatureAction.class);
    private ISelection selection;

    public RemoveTigerseyeNatureAction() {
    }

    @Override
    public void run(IAction action) {
	IProject projectForSelection = getProjectForSelection();
	if (projectForSelection == null) {
	    logger.info("Can not add nature on selection {}", selection);
	    return;
	}
	try {
	    TigerseyeRuntime.removeTigerseyeNature(projectForSelection);
	} catch (CoreException e) {
	    logger.error("Failed to remove nature", e);
	}
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
	this.selection = selection;
    }

    private @CheckForNull
    IProject getProjectForSelection() {
	if (selection == null)
	    return null;
	final IStructuredSelection s = (IStructuredSelection) selection;
	final Object selected = s.getFirstElement();
	IProject targetProject;
	if (selected instanceof IProject) {
	    targetProject = (IProject) selected;
	} else {
	    return null;
	}
	return targetProject;
    }

    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
    }


}
