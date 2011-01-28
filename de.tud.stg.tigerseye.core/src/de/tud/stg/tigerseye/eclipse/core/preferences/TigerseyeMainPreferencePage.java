package de.tud.stg.tigerseye.eclipse.core.preferences;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;

/**
 * Preference page root for all pages of the Tigerseye Plug-in. Provides core
 * preferences.
 * 
 * @author Leo Roos
 * 
 */
public class TigerseyeMainPreferencePage extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

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
	/*
	 * FIXME: Old output directories should be deleted if the name is
	 * changed and a complete rebuild of all Tigerseye projects performed.
	 */
	StringFieldEditor outPutDirEditor = new OutPutDirectoryFieldEditor(
		TigerseyePreferenceConstants.TIGERSEYE_OUTPUT_FOLDER_PATH,
		"&Output Directory",
		getFieldEditorParent());
	addField(outPutDirEditor);
	outPutDirEditor.setEnabled(false, getFieldEditorParent());
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
	    }
	    else {
		FileUtils.deleteDirectory(outPutDir);
	    }
	    return true;
	}
    }

}