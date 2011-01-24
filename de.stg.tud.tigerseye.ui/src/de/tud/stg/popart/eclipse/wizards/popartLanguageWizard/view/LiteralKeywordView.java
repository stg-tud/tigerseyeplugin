package de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.view;

import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartLiteralKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartStructuredElementKeyword;
import de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.PopartLanguageModel;

/**
 * View for a literal keyword in the PopartLanguageWizard
 * 
 * @author David Marx
 * @author Thorsten Peter
 */
public class LiteralKeywordView extends Composite implements DisposeListener {
	
	private Text type;
	private PopartLiteralKeyword keyword;
		
	public LiteralKeywordView(Composite parent, int style, PopartLiteralKeyword keyword) {
		super(parent, style);
		this.keyword = keyword;
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginWidth = 10;
		setLayout(gridLayout);
				
		new Label(this,SWT.NONE).setText("Name: ");		
		new Label(this,SWT.NONE).setText(keyword.getName());	
		
		new Label(this,SWT.NONE).setText("Type: ");
		type = new Text (this,SWT.SINGLE | SWT.BORDER);
		
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		type.setLayoutData(data);

		
		if (keyword.getType()!=null) {
			type.setText(keyword.getType());			
		}	
		// setText resizes textfield ....
		data = new GridData(90,12);
		type.setLayoutData(data);
		
		addDisposeListener(this);
		
	}
	
	public void widgetDisposed(DisposeEvent arg0) {
		keyword.setType(type.getText());			
	}
	
	

}
