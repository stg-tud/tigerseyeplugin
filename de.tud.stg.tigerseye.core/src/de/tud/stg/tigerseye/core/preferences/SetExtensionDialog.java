package de.tud.stg.tigerseye.core.preferences;

import javax.annotation.Nonnull;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Simple dialog to edit the extension of a configured DSL.
 * 
 * @author Leo Roos
 */
@Nonnull
public class SetExtensionDialog extends TrayDialog {

    private String extensionString;
    private final String title;

    public SetExtensionDialog(Shell shell, String extension, String title) {
	super(shell);
	this.extensionString = extension;
	this.title = title;
	setHelpAvailable(false);
    }

    private Text extensionText;
    private boolean changed = false;

    public String getExtensionString() {
	return extensionString;
    }

    public boolean isChanged() {
	return changed;
    }
    /**
     * Create contents of the dialog
     * 
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
	getShell().setText(title);
	Composite dialog = new Composite(parent, SWT.NONE);

	GridLayout layout = new GridLayout(2, false);
	layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 10;

	dialog.setLayout(layout);
	GridData dialogData = new GridData(SWT.FILL, SWT.FILL, true, true);
	dialog.setLayoutData(dialogData);

	Label label = new Label(dialog, SWT.NONE);
	label.setText("Extension:");

	this.extensionText = new Text(dialog, SWT.BORDER);
	GridData textData = new GridData(SWT.FILL, SWT.DEFAULT, false, true);
	textData.widthHint = 200;
	extensionText.setLayoutData(textData);
	extensionText.setSize(200, 16);
	extensionText.setText(this.extensionString);

	return dialog;
    }

    @Override
    protected void okPressed() {
	setExtensionString(this.extensionText.getText());
	super.okPressed();
    }

    private void setExtensionString(String newExt) {
	if (!newExt.equals(getExtensionString()))
	    this.changed = true;
	this.extensionString = newExt;
    }

}