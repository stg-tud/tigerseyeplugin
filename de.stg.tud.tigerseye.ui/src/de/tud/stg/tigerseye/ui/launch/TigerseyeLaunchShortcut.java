package de.tud.stg.tigerseye.ui.launch;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaApplicationLaunchShortcut;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.popart.builder.transformers.FileType;
import de.tud.stg.tigerseye.core.OutputPathHandler;
import de.tud.stg.tigerseye.launching.ITigerseyeLaunchConfigurationConstants;

public class TigerseyeLaunchShortcut extends JavaApplicationLaunchShortcut
	implements ILaunchShortcut {

    private static final Logger logger = LoggerFactory
	    .getLogger(TigerseyeLaunchShortcut.class);

    /*
     * create Configurations receives the type it handles from findTypes. So it
     * can be left as it is.
     * 
     * @Override protected ILaunchConfiguration createConfiguration(IType type)
     * {}
     */

    /**
     * This is mainly an adjusted version of
     * {@link JavaLaunchDelegate#searchAndLaunch(Object[], String, String, String)}
     * , since the original method expects an editor which understands
     * #getAdapter(IJavaElement.class). This could be a future improvement, in
     * which case the original method could be used again.
     * 
     * @param editor
     * @param mode
     */
    @Override
    public void launch(IEditorPart editor, String mode) {
	IEditorInput editorInput = editor.getEditorInput();
	IFile adapter = (IFile) editorInput.getAdapter(IFile.class);
	logger.debug("Called with editor:{}", editor);
	IType[] findTypes;
	try {
	    findTypes = findTypes(new Object[] { adapter }, PlatformUI
		    .getWorkbench().getProgressService());

	} catch (InterruptedException e) {
	    return;
	} catch (CoreException e) {
	    MessageDialog.openError(getShell(), "Launch Tigerseye Error",
		    e.getMessage());
	    return;
	}
	IType type = null;
	if (findTypes.length == 0) {
	    MessageDialog.openError(getShell(), "No launchable file found.",
		    getEditorEmptyMessage());
	} else if (findTypes.length > 1) {
	    type = chooseType(findTypes, "Select File to launch.");
	} else {
	    type = findTypes[0];
	}
	if (type != null) {
	    launch(type, mode);
	}
    }

    @Override
    protected IType[] findTypes(Object[] elements, IRunnableContext arg1)
	    throws InterruptedException, CoreException {
	logger.info("types to search for in array: {}", elements);
	if (elements.length < 1 || elements == null)
	    return new IType[0];

	ArrayList<IType> typesList = new ArrayList<IType>(elements.length);
	for (Object object : elements) {
	    if (object instanceof IFile) {
		IFile file = (IFile) object;
		String name = file.getName();
		FileType[] values = FileType.values();
		boolean endsWith = false;
		for (FileType fileType : values) {
		    endsWith = name.endsWith(fileType.srcFileEnding);
		    if (endsWith)
			break;
		}
		if (!endsWith) {
		    logger.info("unsupported file for tigerseye launch: {}",
			    file);
		    continue;
		}
		IFile outputFile = new OutputPathHandler().getOutputFile(file);
		if (outputFile.exists()) {
		    ICompilationUnit compilationUnit = JavaCore
			    .createCompilationUnitFrom(outputFile);
		    IType[] allTypes = compilationUnit.getAllTypes();
		    Collections.addAll(typesList, allTypes);
		}
	    }
	}
	return typesList.toArray(new IType[typesList.size()]);
    }

    @Override
    protected ILaunchConfigurationType getConfigurationType() {
	return DebugPlugin
		.getDefault()
		.getLaunchManager()
		.getLaunchConfigurationType(
			ITigerseyeLaunchConfigurationConstants.TIGERSEYE_LAUNCH_CONFIGURATION_TYPE);
    }

    @Override
    protected String getEditorEmptyMessage() {
	return "No type to launch found.";
    }

    @Override
    protected String getSelectionEmptyMessage() {
	return "No type to launch found.";
    }

    @Override
    protected String getTypeSelectionTitle() {
	return "More than one type can be launched, please choose which one to launch";
    }

}
