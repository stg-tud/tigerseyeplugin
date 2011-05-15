package de.tud.stg.tigerseye.ui.launch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.codehaus.groovy.eclipse.launchers.AbstractGroovyLauncherTab;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.debug.ui.launcher.SharedJavaMainTab;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.TigerseyeCoreActivator;
import de.tud.stg.tigerseye.eclipse.core.TigerseyeImage;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileTypeHelper;
import de.tud.stg.tigerseye.eclipse.core.launching.ITigerseyeLaunchConfigurationConstants;
import de.tud.stg.tigerseye.eclipse.core.utils.OutputPathHandler;

/**
 * The Tigerseye launch configuration tab in the Launch Configurations Dialog
 * for the launch configuration type Tigerseye. <br />
 * The Tab reuses mainly the JavaMainTab but adjusts function of the search for
 * Main type button.
 * 
 * @author Leo Roos
 * 
 */
public class TigerseyeMainLaunchConfigurationTab extends SharedJavaMainTab
	implements ILaunchConfigurationTab {

    private static final Logger logger = LoggerFactory
	    .getLogger(TigerseyeMainLaunchConfigurationTab.class);
    private Text tigerDsltext;

    @Override
    public String getName() {
	return "TigerseyeMain";
    }

    @Override
    public void createControl(Composite parent) {
	Composite dialog = new Composite(parent, SWT.NONE);
	dialog.setFont(parent.getFont());
	GridLayout gl = new GridLayout(1, false);
	dialog.setLayout(gl);
	GridData gd = getVerticalFillGD();
	dialog.setLayoutData(gd);

	createProjectEditor(dialog);
	createMainTypeEditor(dialog, "&Executed Type:");

	Group correspondingDSLGroup = new Group(dialog, SWT.NONE);
	correspondingDSLGroup.setText("Corresponding Tigerseye File:");
	correspondingDSLGroup.setFont(dialog.getFont());
	correspondingDSLGroup.setLayout(new GridLayout());
	correspondingDSLGroup.setLayoutData(getVerticalFillGD());
	tigerDsltext = new Text(correspondingDSLGroup, SWT.READ_ONLY
		| SWT.BORDER);
	tigerDsltext
		.setLayoutData(getVerticalFillGD());
	tigerDsltext
		.setToolTipText("The corresponding Tigerseye DSL currently chosen as Main class or none if no corresponding file exists");
	setControl(dialog);
	PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), "");
    }

    private GridData getVerticalFillGD() {
	return new GridData(SWT.FILL, SWT.TOP,
		true, false);
    }



    @Override
    public void initializeFrom(ILaunchConfiguration config) {
	super.initializeFrom(config);
	try {
	    String attribute = config.getAttribute(
		    IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
		    (String) null);
	    if (attribute != null) {
		IType findType = getJavaProject().findType(attribute);
		String stringForCorrespondingDSL = getStringForCorrespondingDSL(findType);
		tigerDsltext.setText(stringForCorrespondingDSL);
	    }
	} catch (CoreException e) {
	    // Can be safely ignored
	}
	updateMainTypeFromConfig(config);
    }

    /**
     * 
     * This method is an adjusted version of
     * {@link AbstractGroovyLauncherTab#handleSearchButtonSelected()}
     */
    @SuppressWarnings("restriction")
    @Override
    protected void handleSearchButtonSelected() {
	/*
	 * Using _getJavaProject()_ and _fMainText_ from Superclass though the
	 * access is discouraged. Alternative would be to duplicate the
	 * functionality.
	 */
	IJavaProject javaProject = getJavaProject();
	List<IType> availableClasses = new LinkedList<IType>();
	try {
	    availableClasses = findAllRunnableDSLs(javaProject);
	} catch (JavaModelException e) {
	    logger.info("Seach failed for ", e);
	}
	if (availableClasses.size() == 0) {
	    MessageDialog.openWarning(getShell(), "No classes to run",
		    "There are no compiled classes to run in this project");
	    return;
	}
	ListDialog dialog = new ListDialog(getShell());
	dialog.setBlockOnOpen(true);
	dialog.setMessage("Select a class to run");
	dialog.setTitle("Choose Tigerseye file or GroovyStarter");
	dialog.setContentProvider(new ArrayContentProvider());
	dialog.setLabelProvider(new TigerseyesDSLLabelProvider());
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
	    IType result = (IType) results[0];
	    fMainText.setText(result.getFullyQualifiedName());
	    tigerDsltext.setText(getStringForCorrespondingDSL(result));
	}
    }

    private @Nonnull
    String getStringForCorrespondingDSL(IType result) {
	String tigerTextString = "";
	IFile tigerFile = extractCorrespondingDSLFile(result);
	if (tigerFile != null && tigerFile.exists())
	    tigerTextString = tigerFile.getProjectRelativePath()
		    .removeFirstSegments(1).toString();
	return tigerTextString;
    }

    /**
     * @see org.codehaus.groovy.eclipse.core.model.GroovyProjectFaced#findAllRunnableTypes()
     */
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
	/*
	 * Provide easy possibility to use the GroovyStarter to start Tigerseye
	 * transformed Groovy script instead of directly launching it.
	 */
	IType groovyStarter = project
		.findType(ITigerseyeLaunchConfigurationConstants.GROOVY_STARTER_TYPE_ID);
	results.add(groovyStarter);
	return results;
    }

    private Collection<? extends IType> findAllRunnableTypes(
	    ICompilationUnit unit) throws JavaModelException {
	IType[] types = unit.getAllTypes();
	List<IType> results = new ArrayList<IType>(types.length);
	for (IType t : types) {
	    boolean exists = existsCorrespondingDSLFile(t);
	    if (exists)
		results.add(t);
	}
	return Arrays.asList(types);
    }

    private boolean existsCorrespondingDSLFile(IType t) {
	IFile correspondingDSLFile = extractCorrespondingDSLFile(t);
	if (correspondingDSLFile == null)
	    return false;
	return correspondingDSLFile.exists();
    }

    /**
     * Handle-only. This method returns the file name if one would exist, it
     * does not guarantee that is indeed available.
     * 
     * @param t
     * @return
     */
    private @CheckForNull
    IFile extractCorrespondingDSLFile(IType t) {
	if (t == null)
	    return null;
	IResource resource = t.getResource();
	if (resource == null)
	    return null;
	IPath fullPath = resource.getProjectRelativePath();
	String nameToAnalyze = fullPath.lastSegment().toString();
	if (nameToAnalyze.endsWith(".class")) {
	    if (nameToAnalyze.contains("$_") && nameToAnalyze.contains("dsl")) {
		nameToAnalyze = nameToAnalyze.replaceAll("\\.class",
			"\\.groovy");
	    } else {
		nameToAnalyze = nameToAnalyze.replaceAll("\\.class", "\\.java");
	    }
	}

	String srcName = new OutputPathHandler()
		.getSourceNameForOutputName(nameToAnalyze);
	IPath outFileWithSrcFileName = fullPath.removeLastSegments(1).append(
		srcName);
	// FIXME should be dynamically searching for source folders
	IPath srcFile = new Path("src").append(outFileWithSrcFileName
		.removeFirstSegments(1));
	IFile correspondingDSLFile = resource.getProject().getFile(srcFile);
	return correspondingDSLFile;
    }

    /**
     * Renders ITypes which originated from
     * 
     * @author Leo Roos
     * 
     */
    class TigerseyesDSLLabelProvider extends JavaElementLabelProvider {
	@Override
	public Image getImage(Object element) {
	    if (element instanceof IType) {
		IResource r = ((IType) element).getResource();
		if (r instanceof IFile) {
		    IFile file = (IFile) r;
		    FileType fileType = FileTypeHelper.getTypeForOutputResource(file
			    .getName());
		    if (fileType != null)
			return TigerseyeCoreActivator.getTigerseyeImage(
				TigerseyeImage.FileTypeTigerseye).createImage();

		}
	    }
	    return super.getImage(element);
	}

	@Override
	public String getText(Object element) {
	    if (element instanceof IType) {
		IFile dslFile = extractCorrespondingDSLFile((IType) element);
		if (dslFile != null && dslFile.exists())
		    return dslFile.getProjectRelativePath()
			    .removeFirstSegments(1).toString();
	    }
	    return super.getText(element);
	}

    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy config) {

	config.setAttribute(
		IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, fProjText
			.getText().trim());
	config.setAttribute(
		IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
		fMainText.getText().trim());
	mapResources(config);
	/**
	 * Inform TigerseyeLaunchDelegate that user made adjustments and
	 * therefore the default groovy launch configuration should not be
	 * chosen.
	 */
	config.setAttribute(
		ITigerseyeLaunchConfigurationConstants.ATTR_IS_DEFAULT_GROOVY_LAUNCH,
		false);

    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy config) {
	config.setAttribute(
		ITigerseyeLaunchConfigurationConstants.ATTR_IS_DEFAULT_GROOVY_LAUNCH,
		true);
	IJavaElement javaElement = getContext();
	if (javaElement != null) {
	    initializeJavaProject(javaElement, config);
	} else {
	    config.setAttribute(
		    IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
	}
	initializeMainTypeAndName(javaElement, config);
    }

}
