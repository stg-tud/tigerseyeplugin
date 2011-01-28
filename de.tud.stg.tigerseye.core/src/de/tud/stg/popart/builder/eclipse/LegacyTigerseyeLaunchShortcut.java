package de.tud.stg.popart.builder.eclipse;

import org.codehaus.groovy.eclipse.launchers.AbstractGroovyLaunchShortcut;
import org.codehaus.groovy.eclipse.launchers.GroovyScriptLaunchShortcut;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.popart.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.OutputPathHandler;

public class LegacyTigerseyeLaunchShortcut extends AbstractGroovyLaunchShortcut
		implements ILaunchShortcut {

    private static final Logger logger = LoggerFactory
	    .getLogger(LegacyTigerseyeLaunchShortcut.class);

	@Override
	public void launch(IEditorPart editor, String mode) {
		editor.getEditorSite().getPage().saveEditor(editor, false);
		IEditorInput input = editor.getEditorInput();
		IFile file = (IFile) input.getAdapter(IFile.class);

		if (file != null) {
			launchTigerseye(file, mode);
		} else {
			cannotRunTigerseyeErrorMessageDialog("No runnable file provided from editor.");
		}
	}

	@Override
	public void launch(ISelection selection, String mode) {

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection struct = (IStructuredSelection) selection;
			Object obj = struct.getFirstElement();
			if (obj instanceof IAdaptable) {
				IFile file = (IFile) ((IAdaptable) obj).getAdapter(IFile.class);
				if (file != null) {
					launchTigerseye(file, mode);
					return;
				}
			}
		}
		cannotRunTigerseyeErrorMessageDialog("No Tigerseye file selected");
	}

	private void launchTigerseye(IFile file, String mode) {
		FileType filetype = FileType.getTypeForSrcResource(file.getName());
	if (filetype == null) {
	    cannotRunTigerseyeErrorMessageDialog("No filetype could be determined for "
		    + file);
	    return;
	}
	IFile delegatedFile = getDelegatedFile(file);
	if (delegatedFile == null) {
	    cannotRunTigerseyeErrorMessageDialog("Failed to determine output file name for "
		    + file + " and filetype " + filetype);
	    return;
	}

		if (!delegatedFile.exists()) {
			String errorMessage = "No runnable file for \""
					+ file.getProjectRelativePath()
					+ "\" was found. Try rebuilding.\n\n(Expected groovy file \""
					+ delegatedFile.getProjectRelativePath()
					+ "\" does not exist.)";
			cannotRunTigerseyeErrorMessageDialog(errorMessage);
			return;
		}

	ICompilationUnit compilationUnit = JavaCore
		.createCompilationUnitFrom(delegatedFile);

	// if (Filetype.JAVA.equals(filetype)) {
	//
	// // TODO DebugUITools.launch(configuration, mode);
	// ILaunchManager manager = DebugPlugin.getDefault()
	// .getLaunchManager();
	// ILaunchConfigurationType type = manager
	// .getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
	// // ILaunchConfiguration[] configurations =
	// // manager.getLaunchConfigurations(type);
	// // for (int i = 0; i < configurations.length; i++) {
	// // ILaunchConfiguration configuration = configurations[i];
	// // if (configuration.getName().equals("Start Tomcat")) {
	// // configuration.delete();
	// // break;
	// // }
	// // }
	// try {
	// ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(
	// null, "Start EDSL");
	// // compilationUnit.getPackageDeclaration(name)
	// workingCopy.setAttribute(
	// IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
	// "main.SimpleJavaTest2");
	// DebugUITools.launch(workingCopy, mode);
	// } catch (CoreException e) {
	// logger.error("Failed to start Application", e);
	// return;
	// }
	//
	// } else {
			super.launchGroovy(compilationUnit,
					compilationUnit.getJavaProject(), mode);
	// }
	}

	private void cannotRunTigerseyeErrorMessageDialog(String errorMessage) {
		MessageDialog.openError(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), "Can't run Tigerseye",
				errorMessage);
	}

    private
 IFile getDelegatedFile(IFile file) {

	IFile delegatedFile = new OutputPathHandler().getOutputFile(file);
		return delegatedFile;
	}

	@Override
	protected String classToRun() {
		// XXX Is it possible to implement another specific file to run instead
		// of overriding launchGroovy?
		return "groovy.ui.GroovyMain";
	}

	@Override
	protected ILaunchConfigurationType getGroovyLaunchConfigType() {
		return getLaunchManager().getLaunchConfigurationType(
				GroovyScriptLaunchShortcut.GROOVY_SCRIPT_LAUNCH_CONFIG_ID);
	}

	@Override
	protected String applicationOrConsole() {
		return "tigerseye";
	}

	@Override
	protected boolean canLaunchWithNoType() {
		return false;
	}
}
