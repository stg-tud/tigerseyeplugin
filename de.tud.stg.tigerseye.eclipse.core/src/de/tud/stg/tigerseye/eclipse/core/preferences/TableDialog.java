package de.tud.stg.tigerseye.eclipse.core.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.CheckForNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * A Dialog showing a table with one column and a check box for each table item.
 * 
 * @author Leo Roos
 * 
 */
public class TableDialog extends TrayDialog {

    /**
     * The data model for this table. Holding a name that will be represented
     * and the initial check state of the table item represented by this object.
     * 
     * @author Leo Roos
     * 
     */
    public static class CheckedItem {

	/**
	 * Visible name in table
	 */
	public String name;
	/**
	 * Checked state in table
	 */
	public boolean checked;
	/**
	 * an optional data object, has no effect for the dialog
	 */
	public @CheckForNull
	Object data = null;

	public CheckedItem(Object data, String name, boolean checked) {
	    this.name = name;
	    this.checked = checked;
	    this.data = data;
	}

	public CheckedItem(String name, boolean checked) {
	    this.name = name;
	    this.checked = checked;
	}

	@Override
	public boolean equals(Object obj) {
	    if (obj instanceof CheckedItem) {
		CheckedItem rhs = (CheckedItem) obj;
		return new EqualsBuilder().append(name, rhs.name)
			.append(checked, rhs.checked).append(data, rhs.data)
			.isEquals();
	    }
	    return false;
	}

	@Override
	public int hashCode() {
	    return new HashCodeBuilder().append(checked).append(name)
		    .append(data).toHashCode();
	}

	@Override
	public String toString() {
	    return "CheckedItem[" + data + "," + name + "," + checked + "]";
	}

	public CheckedItem copy() {
	    CheckedItem checkedItem = new CheckedItem(data, name, checked);
	    return checkedItem;
	}
    }

    private List<CheckedItem> content = Collections.emptyList();
    private List<CheckedItem> changed = Collections.emptyList();
    private final String title;
    private Table table;
    private String shellTitle;

    public TableDialog(Shell shell, String title) {
	super(shell);
	this.title = title;
	setHelpAvailable(false);
	setBlockOnOpen(true);
	setShellStyle(SWT.TITLE | SWT.APPLICATION_MODAL);
    }

    public void setTitle(String windowtitle) {
	Shell shell = getShell();
	if (shell != null)
	    shell.setText(windowtitle);
	else {
	    this.shellTitle = windowtitle;
	}
    }

    @Override
    protected void configureShell(Shell newShell) {
	if (shellTitle != null)
	    newShell.setText(shellTitle);
	super.configureShell(newShell);
    }

    @Override
    protected boolean isResizable() {
	return true;
    }


    /**
     * @param cis
     *            uses a copy of the passed items
     */
    public void setItems(List<CheckedItem> cis) {
	ArrayList<CheckedItem> items = new ArrayList<CheckedItem>();
	for (CheckedItem checkedItem : cis) {
	    items.add(checkedItem);
	}
	this.content = items;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
	Composite dialogArea = new Composite(parent, SWT.NONE);
	dialogArea.setLayout(new GridLayout());
	GridData ariaGD = getFillGD();
	ariaGD.minimumHeight = 150;
	dialogArea.setLayoutData(ariaGD);
	table = new Table(dialogArea, SWT.CHECK);
	table.setLayoutData(getFillGD());
	table.setHeaderVisible(true);
	table.addSelectionListener(new SelectionAdapter() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		TableItem item = (TableItem) e.item;
		CheckedItem checkedItem = (CheckedItem) item.getData();
		itemSelected(checkedItem);
	    }

	});
	TableColumn singleCol = new TableColumn(table, SWT.LEFT);
	singleCol.setText(title);
	addTableItems(table);
	singleCol.pack();
	return dialogArea;
    }

    @Override
    protected Control getButtonBar() {

	return super.getButtonBar();
    }

    private GridData getFillGD() {
	GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
	return gd;
    }

    private void addTableItems(Table table) {
	for (CheckedItem ci : getContent()) {
	    TableItem tableItem = new TableItem(table, SWT.NONE);
	    tableItem.setData(ci);
	    tableItem.setText(ci.name);
	    tableItem.setChecked(ci.checked);
	}

    }

    private List<CheckedItem> getContent() {
	return content;
    }

    /**
     * @return the items where the check state changed
     */
    public List<CheckedItem> getChangedItems() {
	return this.changed;
    }

    @Override
    protected void okPressed() {
	ArrayList<CheckedItem> changedItems = new ArrayList<CheckedItem>();
	for (TableItem it : table.getItems()) {
	    CheckedItem data = (CheckedItem) it.getData();
	    if (data.checked != it.getChecked()) {
		data.checked = it.getChecked();
		changedItems.add(data);
	    }
	}
	this.changed = changedItems;
	super.okPressed();
    }

    /**
     * Get the underlying table of this Dialog
     * 
     * @return the table or <code>null</code> if it was not yet initialized
     */
    protected Table getTable() {
	return table;
    }

    /**
     * This method is called when an element in the table has been selected.
     * Subclasses may override, the default implementation does nothing.
     * 
     * @param checkedItem
     *            the selected item
     */
    protected void itemSelected(CheckedItem checkedItem) {
    }

}
