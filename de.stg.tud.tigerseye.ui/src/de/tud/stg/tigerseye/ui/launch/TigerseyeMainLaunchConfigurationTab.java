package de.tud.stg.tigerseye.ui.launch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaMainTab;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.dialogs.ListDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TigerseyeMainLaunchConfigurationTab extends JavaMainTab
	implements
	ILaunchConfigurationTab {

    private static final Logger logger = LoggerFactory
	    .getLogger(TigerseyeMainLaunchConfigurationTab.class);



    // FIXME move important logic to TigerseyeLaunchConfigurationDelegate to
    // separate UI from core logic.

    // ----------------------Probably useful methods for delegate
    /**
     * 
     * @see {@link AbstractJavaLaunchConfigurationDelegate#getJavaProject(org.eclipse.debug.core.ILaunchConfiguration)}
     * @param configuration
     * @return
     * @throws CoreException
     */
    // public IJavaProject getJavaProject(ILaunchConfiguration configuration)
    // throws CoreException {
    // String projectName = getJavaProjectName(configuration);
    // if (projectName != null) {
    // projectName = projectName.trim();
    // if (projectName.length() > 0) {
    // IProject project = ResourcesPlugin.getWorkspace().getRoot()
    // .getProject(projectName);
    // IJavaProject javaProject = JavaCore.create(project);
    // if (javaProject != null && javaProject.exists()) {
    // return javaProject;
    // }
    // }
    // }
    // return null;
    // }

    // public String getJavaProjectName(ILaunchConfiguration configuration)
    // throws CoreException {
    // return configuration.getAttribute(
    // IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
    // (String) null);
    // }
    // ______________________________________________________________________________________________________

    @Override
    public String getName() {
	return "TigerseyeMain";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jdt.debug.ui.launchConfigurations.JavaMainTab#
     * handleSearchButtonSelected() copied from AbstractGroovyLauncherTab
     */
    @Override
    protected void handleSearchButtonSelected() {
	IJavaProject javaProject = getJavaProject();
	/*
	 * Note that the set of available classes may be zero and hence the
	 * dialog will obviously not display any classes; in which case the
	 * project needs to be compiled.
	 */
	try {
	    final List<IType> availableClasses = findAllRunnableDSLs(javaProject);
	    if (availableClasses.size() == 0) {
		MessageDialog
.openWarning(getShell(), "No classes to run",
			"There are no compiled classes to run in this project");
		return;
	    }
	    ListDialog dialog = new ListDialog(getShell());
	    dialog.setBlockOnOpen(true);
	    dialog.setMessage("Select a class to run");
	    dialog.setTitle("Choose DSL Class");
	    dialog.setContentProvider(new ArrayContentProvider());
	    dialog.setLabelProvider(new LabelProvider());
	    dialog.setInput(availableClasses.toArray(new IType[availableClasses
		    .size()]));
	    if (dialog.open() == Window.CANCEL) {
		return;
	    }

	    Object[] results = dialog.getResult();
	    if (results == null || results.length == 0) {
		return;
	    }
	    if (results[0] instanceof IType) {
		fMainText.setText(((IType) results[0]).getFullyQualifiedName());
	    }

	} catch (Exception e) {
	    logger.error("Exception when launching " + javaProject, e);
	}
    }

    private List<IType> findAllRunnableDSLs(IJavaProject project)
	    throws JavaModelException {
	final List<IType> results = new ArrayList<IType>();
	IPackageFragmentRoot[] roots = project.getAllPackageFragmentRoots();
	for (IPackageFragmentRoot root : roots) {
	    if (!root.isReadOnly()) {
		IJavaElement[] children = root.getChildren();
		for (IJavaElement child : children) {
		    if (child.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
			ICompilationUnit[] units = ((IPackageFragment) child)
				.getCompilationUnits();
			for (ICompilationUnit unit : units) {
			    results.addAll(findAllRunnableTypes(unit));
			}
		    }
		}
	    }
	}
	return results;
    }

    private Collection<? extends IType> findAllRunnableTypes(
	    ICompilationUnit unit) throws JavaModelException {
	// List<IType> results = new ArrayList<IType>();
	IType[] types = unit.getAllTypes();
	// for (IType type : types) {
	// if (hasRunnableMain(type)) {
	// results.add(type);
	// }
	// }

	return Arrays.asList(types);
    }


}
