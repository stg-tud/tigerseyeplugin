package de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.view;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

/**
 * Parametertable in OperationKeywordView
 * 
 * @author David Marx
 * @author Thorsten Peter
 */
public class ParameterTable extends Table {

	private TableViewer tableViewer;

	public ParameterTable(Composite parent, int style) {
		super(parent, style);

		tableViewer = new TableViewer(this);
		
		String[] titles = { "name", "type" };
		int[] bounds = { 100, 100 };

		for (int i = 0; i < titles.length; i++) {
			TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
			column.getColumn().setText(titles[i]);
			column.getColumn().setWidth(bounds[i]);
			column.getColumn().setResizable(true);
			column.getColumn().setMoveable(true);
			// enable editing support
			column.setEditingSupport(new ParameterEditingSupport(tableViewer, i));
		}
		
		setHeaderVisible(true);
		setLinesVisible(true);
		
	}

}
