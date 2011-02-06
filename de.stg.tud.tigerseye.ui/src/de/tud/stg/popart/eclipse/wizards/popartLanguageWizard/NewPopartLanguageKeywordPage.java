package de.tud.stg.popart.eclipse.wizards.popartLanguageWizard;
import java.util.Iterator;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartLiteralKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartStructuredElementKeyword;
import de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.PopartLanguageModel;
import de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.view.LengthValidator;
import de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.view.LiteralKeywordView;
import de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.view.OperationKeywordView;
import de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.view.StructuredElementKeywordView;
import de.tud.stg.tigerseye.eclipse.core.TigerseyeCoreActivator;
import de.tud.stg.tigerseye.eclipse.core.TigerseyeImage;
/**
 * Second Page in PopartLanguageDefinition
 * 
 * @author David Marx
 * @author Thorsten Peter
 */
public class NewPopartLanguageKeywordPage extends WizardPage {
private static final Logger logger = LoggerFactory.getLogger(NewPopartLanguageKeywordPage.class);

	private ListViewer viewer;
	private Composite composite;
	private Composite currentKeywordView;
	
	protected NewPopartLanguageKeywordPage(String pageName) {
		super(pageName);			
	}
	
	
	@Override
	public void createControl(Composite parent) {

		composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);

		new Label(composite, SWT.NONE).setText("Keywords:");
		new Label(composite, SWT.NONE).setText("");

		Composite left = new Composite(composite, SWT.NONE);
		createLeft(left);

		// set the composite as the control for this page
		setControl(composite);
		
		

	}
	
    @Override
    public Image getImage() {
	return TigerseyeCoreActivator.getIcon(TigerseyeImage.FileTypeTigerseye64)
		.createImage();
    }

	private void createLeft(Composite left) {

		
		RowLayout rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		left.setLayout(rowLayout);

		// add table view
		createViewer(left);
		RowData rowData = new RowData(118, 250);
		viewer.getList().setLayoutData(rowData);
	
		// the buttons
		Button addLiteral = new Button(left, SWT.NONE);
		Button addOperation = new Button(left, SWT.NONE);
		Button addStructeredElement = new Button(left, SWT.NONE);
		Button removeKeyword = new Button(left, SWT.NONE);
		
		rowData = new RowData(140, 25);
		addLiteral.setLayoutData(rowData);
		addOperation.setLayoutData(rowData);
		addStructeredElement.setLayoutData(rowData);
		removeKeyword.setLayoutData(rowData);
		
		addLiteral.setText("Add Literal");
		addOperation.setText("Add Operation");
		addStructeredElement.setText("Add Structured Element");
		removeKeyword.setText("Remove Keyword(s)");

		addLiteral.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				InputDialog dlg = new InputDialog(Display.getCurrent()
						.getActiveShell(), "New Literal", "Enter a name.", "",
						new LengthValidator());
				if (dlg.open() == Window.OK) {
					PopartLanguageModel.getInstance().addKeyword(
							new PopartLiteralKeyword(dlg.getValue()));
				}
			}
		});
		addOperation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				InputDialog dlg = new InputDialog(Display.getCurrent()
						.getActiveShell(), "New Operation", "Enter a name.",
						"", new LengthValidator());
				if (dlg.open() == Window.OK) {
					PopartLanguageModel.getInstance().addKeyword(
							new PopartOperationKeyword(dlg.getValue()));
				}
			}
		});
		addStructeredElement.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				InputDialog dlg = new InputDialog(Display.getCurrent()
						.getActiveShell(), "New Structured Element",
						"Enter a name.", "", new LengthValidator());
				if (dlg.open() == Window.OK) {
					PopartLanguageModel.getInstance().addKeyword(
							new PopartStructuredElementKeyword(dlg.getValue()));
				}
			}
		});
		removeKeyword.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				IStructuredSelection selection = (IStructuredSelection) viewer
						.getSelection();
				if (selection.size()>0) {
					currentKeywordView.dispose();
				}
				for (Iterator<PopartKeyword> iterator = selection.iterator(); iterator
						.hasNext();) {
					PopartKeyword keyword = iterator.next();
					PopartLanguageModel.getInstance().removeKeyword(keyword);
					logger.info(keyword.toString());
				}
				
				viewer.refresh(true);
			}
		});

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) viewer
						.getSelection();
				if (selection.size() == 1) {
					PopartKeyword keyword = (PopartKeyword) selection.getFirstElement();
					viewKeyword(keyword);
				}
			}
		});
	}

	private void viewKeyword(PopartKeyword keyword) {

		disposeCurrentView();

		// else create one
		currentKeywordView = createViewForKeyword(keyword);
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;
		currentKeywordView.setLayoutData(gridData);

		composite.layout();
	}

	public void disposeCurrentView() {
		if (currentKeywordView != null) {
			currentKeywordView.dispose();
		}
	}
	
	private Composite createViewForKeyword(PopartKeyword keyword) {
		Composite v = null;
		if (keyword instanceof PopartLiteralKeyword) {
			v = new LiteralKeywordView(composite, SWT.NONE,
					(PopartLiteralKeyword) keyword);
		}
		if (keyword instanceof PopartOperationKeyword) {
			v = new OperationKeywordView(composite, SWT.NONE,
					(PopartOperationKeyword) keyword);
		}
		if (keyword instanceof PopartStructuredElementKeyword) {
			v = new StructuredElementKeywordView(composite, SWT.NONE,
					(PopartStructuredElementKeyword) keyword);
		}

		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		v.setLayoutData(data);

		return v;
	}

	private void createViewer(Composite parent) {		
		viewer = new ListViewer(parent);		
		viewer.setContentProvider(new PopartLanguageModelContentProvider());
		viewer.setInput(PopartLanguageModel.getInstance());
		viewer.setLabelProvider(new PopartLanguageModelLabelProvider());
		ViewerSorter sorter = new ViewerSorter() {
			@Override
			public int compare(Viewer viewer, Object o1, Object o2) {
				PopartKeyword k1 = (PopartKeyword) o1;
				PopartKeyword k2 = (PopartKeyword) o2;
				if (k1.compareTo(k2) == 0) {
					return k1.getName().compareTo(k2.getName());
				} else {
					return (k1).compareTo(k2);
				}
			}
		};
		viewer.setSorter(sorter);
	}

}