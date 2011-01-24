package de.tud.stg.popart.eclipse.wizards.popartLanguageWizard.view;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartParameter;

/**
 * Editing support used by the parameter table in OperationKeywordView
 * 
 * @author David Marx
 * @author Thorsten Peter
 */
public class ParameterEditingSupport extends EditingSupport {

	private int column;
	private CellEditor editor;

	public ParameterEditingSupport(ColumnViewer viewer, int column) {
		super(viewer);

		// Create the correct editor based on the column index
		switch (column) {
		case 0:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
			break;
		case 1:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
			break;
		default:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
		}
		this.column = column;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected Object getValue(Object element) {
		PopartParameter parameter = (PopartParameter) element;

		switch (this.column) {
		case 0:
			return parameter.getName();
		case 1:
			return parameter.getType();
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		PopartParameter parameter = (PopartParameter) element;

		switch (this.column) {
		case 0:
			parameter.setName(String.valueOf(value));
			break;
		case 1:
			parameter.setType(String.valueOf(value));
			break;		
		}

		getViewer().update(element, null);
	}

}
