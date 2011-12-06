package de.tud.stg.tigerseye.ui.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.api.NoLegalPropertyFoundException;
import de.tud.stg.tigerseye.eclipse.core.preferences.PreferenceDSL;

public class TigerseyeUIPreferencePage extends PreferencePage
	implements
	IWorkbenchPreferencePage {

    private final List<UiDSL> dsls = new ArrayList<UiDSL>();
    private final List<FieldEditor> fields = new ArrayList<FieldEditor>();
    private Table dslTable;

    @Override
    public void init(IWorkbench workbench) {
	setPreferenceStore(TigerseyeCore.getPreferences());
	setDescription("Tigerseye Editor preferences.");
	for (DSLDefinition dsl : TigerseyeCore.getLanguageProvider()
		.getDSLDefinitions()) {
	    this.dsls.add(new UiDSL(dsl, getPreferenceStore()));
	}
    }

    public List<UiDSL> getDsls() {
	return dsls;
    }

    @Override
    protected Control createContents(Composite parent) {
	final Composite page = new Composite(parent, SWT.NONE);
	page.setLayout(new GridLayout());

	BooleanFieldEditor enableKeywordColoring = new BooleanFieldEditor(
		TigerseyeUIPreferenceConstants.TIGERSEYE_EDITOR_HIGHLIGHT_KEYWORDS_ENABLED,
		"Enable Tigerseye keyword coloring", page);
	addField(enableKeywordColoring);

	createDSLColorTable(page);
	fillTable();
	packTable();

	Button editButton = new Button(page, SWT.PUSH);
	editButton.setText("Change Color");
	editButtonOnSelectionBehavior(editButton);

	return page;
    }

    private void editButtonOnSelectionBehavior(Button editButton) {
	editButton.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		TableItem[] selection = getTable().getSelection();
		if (selection.length < 1)
		    return;
		TableItem selectedItem = selection[0];
		UiDSL itemDsl = (UiDSL) selectedItem.getData();
		RGB newRGB = colorSelectionFor(itemDsl.getRgb());
		itemDsl.setRgb(newRGB);
		selectedItem.setBackground(1, new Color(null, newRGB));
	    }

	    private RGB colorSelectionFor(RGB oldRgb) {
		ColorDialog selector = new ColorDialog(getShell());
		selector.setRGB(oldRgb);
		RGB newRgb = selector.open();
		if (newRgb == null) {
		    newRgb = oldRgb;
		}
		return newRgb;
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {
	    }
	});
    }

    public Table getTable() {
	return dslTable;
    }

    private void addField(BooleanFieldEditor field) {
	field.setPreferenceStore(getPreferenceStore());
	field.load();
	this.fields.add(field);
    }

    private void packTable() {
	for (TableColumn col : getTable().getColumns()) {
	    col.pack();
	}
    }

    private void fillTable() {
	for (UiDSL dsl : getDsls()) {
	    TableItem dslItem = new TableItem(getTable(), SWT.NONE);
	    dslItem.setText(0, dsl.getDsl().getDslName());
	    dslItem.setBackground(1, new Color(null, dsl.getRgb()));
	    dslItem.setData(dsl);
	}
    }

    private void refreshDSLTable() {
	getTable().removeAll();
	fillTable();
    }

    private void createDSLColorTable(final Composite page) {
	final Table dslTable = new Table(page, SWT.BORDER | SWT.FULL_SELECTION);
	final GridData gd_popartLanguagesTable = new GridData(SWT.FILL,
		SWT.FILL, true, false);
	dslTable.setLinesVisible(true);
	dslTable.setHeaderVisible(true);
	gd_popartLanguagesTable.widthHint = 200;
	dslTable.setLayoutData(gd_popartLanguagesTable);

	String[] tableHeader = new String[] { "DSL Name", "Color" };
	for (String string : tableHeader) {
	    TableColumn col = new TableColumn(dslTable, SWT.NONE);
	    col.setText(string);
	}
	this.dslTable = dslTable;
    }

    @Override
    public boolean performOk() {
	for (UiDSL dsl : getDsls()) {
	    if (dsl.needsStoring()) {
		dsl.store();
	    }
	}
	for (FieldEditor field : fields) {
	    field.store();
	}
	return super.performOk();
    }

    @Override
    protected void performDefaults() {
	for (UiDSL dsl : getDsls()) {
	    dsl.loadDefault();
	}
	refreshDSLTable();
	for (FieldEditor field : fields) {
	    field.loadDefault();
	}
	super.performDefaults();
    }

    private static class UiDSL extends PreferenceDSL {

	private RGB rgb;

	public UiDSL(DSLDefinition dsl, IPreferenceStore store) {
	    super(dsl, store);
	    try {
		this.rgb = getDsl().getValue(DSLUIKey.COLOR);
	    } catch (NoLegalPropertyFoundException e) {
		this.rgb = DSLUIKey.getAndSetDefaultColor(store, dsl);
	    }
	}

	public void setRgb(RGB rgb) {
	    if (rgb.equals(this.rgb))
		return;
	    this.rgb = rgb;
	    setNeedsStoring();
	}

	public RGB getRgb() {
	    return rgb;
	}

	public void loadDefault() {
	    RGB default1 = DSLUIKey.getDefaultColor(getStore());
	    setRgb(default1);
	}

	@Override
	public void store() {
	    if (!needsStoring())
		return;
	    super.store();
	    getDsl().setValue(DSLUIKey.COLOR, getRgb());
	}

    }

}
