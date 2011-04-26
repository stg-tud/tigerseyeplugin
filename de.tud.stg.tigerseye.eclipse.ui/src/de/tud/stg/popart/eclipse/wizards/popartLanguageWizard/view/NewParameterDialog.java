package de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.view;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartParameter;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartParameterKeyword;

/**
 * Dialog for creating a new parameter
 * 
 * @author David Marx
 * @author Thorsten Peter
 */
public class NewParameterDialog extends TitleAreaDialog {

	private Text name;
	private Text type;
	private PopartParameterKeyword target;

	public NewParameterDialog(Shell parentShell, PopartParameterKeyword target) {
		super(parentShell);
		this.target = target;
	}

	protected Control createDialogArea(Composite parent) {

		setTitle("Add a new Parameter");
		setMessage("Please enter name and type of the parameter",
				IMessageProvider.INFORMATION);
		
		Composite composite = new Composite(parent,SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.horizontalSpacing = 10;		
		composite.setLayout(gridLayout);
		
		
		new Label(composite, SWT.NONE).setText("Name:");
		name = new Text(composite, SWT.BORDER);
				
		new Label(composite, SWT.NONE).setText("Type:");
		type = new Text(composite, SWT.BORDER);
		
		((GridLayout)parent.getLayout()).numColumns = 1;
		
		Control contents = super.createDialogArea(parent);
	    
		return contents;
	    
	}
	
	public void buttonPressed(int buttonId) {		
		if (buttonId==IDialogConstants.OK_ID) {
			target.addParameter(new PopartParameter(name.getText(),type.getText()));			
		}
		super.buttonPressed(buttonId);
	}

	
}
