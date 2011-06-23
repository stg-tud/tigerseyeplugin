package de.tud.stg.tigerseye.eclipse.core.preferences;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;
import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeRuntime;
import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeRuntimeConstants;

/**
 * Preference page root for all pages of the Tigerseye Plug-in. Provides core
 * preferences.
 * 
 * @author Leo Roos
 * 
 */
public class TigerseyeMainPreferencePage extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

    private static final Logger logger = LoggerFactory
	    .getLogger(TigerseyeMainPreferencePage.class);

    private String originalOutputFolder;
    private StringFieldEditor outPutDirEditor;

    public TigerseyeMainPreferencePage() {
	super(GRID);
	setDescription("Tigerseye Plug-in core preferecnes.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    @Override
    public void init(IWorkbench workbench) {
	setPreferenceStore(TigerseyeCore.getPreferences());
    }

    @Override
    public void createFieldEditors() {
	String outputFolderProperty = TigerseyePreferenceConstants.TIGERSEYE_OUTPUT_FOLDER_PATH_KEY;
	outPutDirEditor = new OutPutDirectoryFieldEditor(outputFolderProperty,
		"&Output Directory", getFieldEditorParent());
	originalOutputFolder = TigerseyeRuntime.getOutputDirectoryPath();
	addField(outPutDirEditor);
	// outPutDirEditor.setEnabled(false, getFieldEditorParent());
    }

    @Override
    public boolean performOk() {
	boolean performOk = super.performOk();
	if (!performOk)
	    return performOk;
	String newDir = this.outPutDirEditor.getStringValue();
	IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
		.getProjects();
	Path oldSrc = new Path(originalOutputFolder);
	Path newSrc = new Path(newDir);
	for (IProject proj : projects) {
	    unsetOldSetNewSourceFolder(oldSrc, newSrc, proj);
	}
	return true;
    }

    private void unsetOldSetNewSourceFolder(Path oldSrc, Path newSrc,
	    IProject proj) {
	try {
	    if (proj.isOpen()
		    && proj.hasNature(TigerseyeRuntimeConstants.TIGERSEYE_NATURE_ID)) {
		IJavaProject jp = JavaCore.create(proj);
		TigerseyeRuntime.removeSourceFolder(jp, proj.getFolder(oldSrc));
		TigerseyeRuntime.setSourceFolder(jp, proj.getFolder(newSrc));
	    }
	} catch (CoreException e) {
	    logger.warn(
		    "Failed to set new output source folder path. Old was: {} new would have be {}",
		    new Object[] { oldSrc, newSrc }, e);
	}
    }

    /**
     * Validates the given output directory name. An actual directory is created
     * in the temporary folder to determine whether the current operating system
     * supports the specified name.
     * 
     * @author Leo Roos
     * 
     */
    private static final class OutPutDirectoryFieldEditor extends
	    StringFieldEditor {

	private final String TMP_DIR = System.getProperty("java.io.tmpdir");

	public OutPutDirectoryFieldEditor(String name, String labelText,
		Composite parent) {
	    super(name, labelText, parent);
	    setValidateStrategy(StringFieldEditor.VALIDATE_ON_FOCUS_LOST);
	}

	@Override
	protected boolean doCheckState() {
	    boolean valid = false;
	    String outputDirName = getStringValue();
	    try {
		valid = checkDirectoryNameValid(outputDirName);
	    } catch (IOException e) {
		setErrorMessage("Exception: " + e.getMessage());
	    } catch (URISyntaxException e) {
		setErrorMessage("Exception: " + e.getMessage());
	    }
	    return valid;
	}

	private boolean checkDirectoryNameValid(String outputDirName)
		throws IOException, URISyntaxException {
	    // Add further undesired characters between the square brackets
	    Pattern compile = Pattern.compile("[/\\\\]");
	    Matcher matcher = compile.matcher(outputDirName);
	    boolean find = matcher.find();
	    if (find) {
		setErrorMessage("Name has invalid format.");
		return false;
	    }

	    File outPutDir = new File(TMP_DIR, outputDirName);
	    boolean mkdir = outPutDir.mkdir();
	    if (!mkdir) {
		setErrorMessage("Invalid name: " + "'" + outputDirName + "'");
		return false;
	    } else {
		FileUtils.deleteDirectory(outPutDir);
	    }
	    return true;
	}
    }

}