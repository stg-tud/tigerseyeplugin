package de.tud.stg.tigerseye.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.utils.OutputPathHandler;
import de.tud.stg.tigerseye.ui.TigerseyeUIActivator;

public class SwitchBetweenTranformerTransformedFile extends AbstractHandler
	implements IHandler {
    private static final Logger logger = LoggerFactory
	    .getLogger(SwitchBetweenTranformerTransformedFile.class);

    public SwitchBetweenTranformerTransformedFile() {
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
	IFile file = null;
	ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
	if (currentSelection instanceof StructuredSelection) {
	    Object firstElement = ((StructuredSelection) currentSelection)
		    .getFirstElement();
	    if (firstElement instanceof IFile)
		file = (IFile) firstElement;
	} else {
	    IEditorPart activeEditor = HandlerUtil.getActiveEditor(event);
	    if (activeEditor != null)
		file = ResourceUtil.getFile(activeEditor.getEditorInput());
	}

	if (file == null) {
	    logger.info("could not extract file information for event: {}",
		    event);
	    return null;
	}

	try {

	    IFile outputFile = new OutputPathHandler().getOutputFile(file);
	    if (outputFile == null)
		logger.error("Failed to determine output file name for " + file);

	    if (!outputFile.exists()) {
		errorDialogResourceDoesNotExist(
			HandlerUtil.getActiveShell(event), outputFile
				.getFullPath().toString());
		return null;
	    }
	    IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindow(event)
		    .getActivePage();
	    if (page != null) {
		IDE.openEditor(page, outputFile);
	    } else {
		logger.debug("Failed to open file. WorkbenchPage was {}.", page);
	    }
	} catch (PartInitException e) {
	    logger.error("Failed to open editor.", e);
	}
	return null;
    }

    private void errorDialogResourceDoesNotExist(Shell shell,
	    String resourceName) {
	ErrorDialog.openError(shell, "Failed to open resource.", "",
		new Status(IStatus.ERROR, TigerseyeUIActivator.PLUGIN_ID,
			"Resource does not exist \n\n " + resourceName));
    }

}
