package de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.view;

import java.util.Iterator;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartParameter;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartParameterKeyword;
import de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.model.PopartLanguageModel;

/**
 * View for a operation keyword in the PopartLanguageWizard
 * 
 * @author David Marx
 * @author Thorsten Peter
 */
public class OperationKeywordView extends Composite implements DisposeListener {

	private Text returnType;
	private PopartOperationKeyword keyword;
	private TableViewer viewer;
	private Button breakpointpossible;

	public OperationKeywordView(Composite parent, int style,
			PopartOperationKeyword keyword) {
		super(parent, style);

		this.keyword = keyword;
		
		// Set the layout
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginWidth = 10;
		setLayout(gridLayout);

		new Label(this, SWT.NONE).setText("Name: ");

		new Label(this, SWT.NONE).setText(keyword.getName());

		new Label(this, SWT.NONE).setText("Return type: ");
		returnType = new Text(this, SWT.SINGLE | SWT.BORDER);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		returnType.setLayoutData(gridData);
		if (keyword.getReturnType() != null) {
			returnType.setText(keyword.getReturnType());			
		}
		GridData data = new GridData(90,12);
		returnType.setLayoutData(data);

		new Label(this,SWT.NONE).setText("Breakpoint possible: ");
		breakpointpossible = new Button(this,SWT.CHECK);
		breakpointpossible.setSelection(keyword.isBreakpointPossible());
		breakpointpossible.addSelectionListener(new SelectionListener() {

		      public void widgetSelected(SelectionEvent event) {
		        getKeyword().setBreakpointPossible(true);
		      }

		      public void widgetDefaultSelected(SelectionEvent event) {
		    	  getKeyword().setBreakpointPossible(false);
		      }
		    });
		
		Label parameterLabel = new Label(this, SWT.NONE);
		parameterLabel.setText("Parameter: ");
		gridData = new GridData();
		gridData.verticalAlignment = GridData.BEGINNING;
		parameterLabel.setLayoutData(gridData);

		createViewer(this);
		gridData = new GridData(145, 200);
		viewer.getTable().setLayoutData(gridData);

		new Label(this, SWT.NONE);

		Composite buttons = new Composite(this, SWT.NONE);
		RowLayout rowLayout = new RowLayout();		
		rowLayout.marginLeft = 0;
		rowLayout.marginTop = 0;
		buttons.setLayout(rowLayout);
		RowData rowData = new RowData(25, 25);

		Button add = new Button(buttons, SWT.NONE);
		add.setLayoutData(rowData);
		add.setText("+");
		add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				NewParameterDialog dialog = new NewParameterDialog(Display
						.getCurrent().getActiveShell(),getKeyword());
				dialog.open();				
				getParameterViewer().refresh(true);				
			}
		});

		Button remove = new Button(buttons, SWT.NONE);
		remove.setLayoutData(rowData);
		remove.setText("-");
		remove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				IStructuredSelection selection = (IStructuredSelection) getParameterViewer()
						.getSelection();
				for (Iterator<PopartParameter> iterator = selection.iterator(); iterator
						.hasNext();) {
					PopartParameter parameter = iterator.next();
					getKeyword().removeParamerer(parameter);
				}
				getParameterViewer().refresh(true);
			}

		});

		
		
		addDisposeListener(this);
	}

	private StructuredViewer getParameterViewer() {
		return viewer;
	}

	private void createViewer(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		createColumns(parent);
		viewer.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {				
				return keyword.getParameter();
			}

			public void dispose() {

			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {				
			}
		});
		viewer.setLabelProvider(new ITableLabelProvider() {

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				PopartParameter p = (PopartParameter) element;
				switch (columnIndex) {
					case 0:
						return p.getName();
					case 1:
						return p.getType();
				}
				return "";
			}

			public void addListener(ILabelProviderListener listener) {
				
			}

			public void dispose() {

			}

			public boolean isLabelProperty(Object element, String property) {
			
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
				
			}

		});


		// Get the content for the viewer, setInput will call getElements in the
		// contentProvider
		viewer.setInput(keyword.getParameter());

		// Layout the viewer
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		viewer.getControl().setLayoutData(gridData);
		
	}

	public TableViewer getViewer() {
		return viewer;
	}

	// This will create the columns for the table
	private void createColumns(final Composite parent) {
		String[] titles = { "name", "type" };
		int[] bounds = { 80, 80 };

		for (int i = 0; i < titles.length; i++) {
			final TableViewerColumn viewerColumn = new TableViewerColumn(
					viewer, SWT.NONE);
			final TableColumn column = viewerColumn.getColumn();
			column.setText(titles[i]);
			column.setWidth(bounds[i]);
			column.setResizable(true);
			column.setMoveable(false);

			viewerColumn.setEditingSupport(new ParameterEditingSupport(viewer,i));
		}
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

	}

	protected PopartOperationKeyword getKeyword() {
		return keyword;
	}

	public void widgetDisposed(DisposeEvent arg0) {
		keyword.setReturnType(returnType.getText());		
		keyword.setBreakpointPossible(breakpointpossible.getSelection());
	}

}
