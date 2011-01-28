package de.tud.stg.tigerseye.eclipse.core.preferences;

import java.util.List;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.popart.builder.eclipse.DSLBuilderHelper;
import de.tud.stg.popart.builder.eclipse.dialoge.TransformerConfigurationDialoge;
import de.tud.stg.popart.eclipse.LanguageProviderImpl;
import de.tud.stg.tigerseye.eclipse.core.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;

public class TransformationsPreferencePage extends PreferencePage implements
	IWorkbenchPreferencePage {

    private static final Logger logger = LoggerFactory
	    .getLogger(TransformationsPreferencePage.class);

    public TransformationsPreferencePage() {
	super("Builder Preference Page");
    }

    @Override
    public void init(IWorkbench workbench) {
	setPreferenceStore(TigerseyeCore.getPreferences());
	noDefaultAndApplyButton();
    }

    @Override
    protected Control createContents(Composite parent) {

	Composite prefPage = new Composite(parent, SWT.NONE);
	prefPage.setLayout(new GridLayout());
	Label instructionLabel = new Label(prefPage, SWT.LEAD);
	instructionLabel.setText("Configure Builder Transformations");
	instructionLabel.setEnabled(true);

	final Table languagesTable = getDSLTable(prefPage);
	fillDSLTableWithDSLs(languagesTable, new LanguageProviderImpl(TigerseyeCore.getPreferences())
		.getDSLDefinitions());

	final Button transformerButton = new Button(prefPage, SWT.PUSH);
	transformerButton.setText("Transformations");
	transformerButton.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		int selectionIndex = languagesTable.getSelectionIndex();
		if (selectionIndex < 0) {
		    logger.warn("No item was selected in table");

		}
		TableItem item = languagesTable.getItem(selectionIndex);
		new TransformerConfigurationDialoge(item.getText(0),
			getShell(), new DSLBuilderHelper(),
			getPreferenceStore());
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {
	    }
	});

	transformerButton.setEnabled(false);

	languagesTable.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		transformerButton.setEnabled(true);
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {
	    }
	});



	Button refreshTable= new Button(prefPage, SWT.PUSH);
	refreshTable.setText("Refresh Table");
	refreshTable.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		refreshLanguagesTableData(languagesTable, new LanguageProviderImpl(TigerseyeCore.getPreferences()).getDSLDefinitions());
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {
	    }
	});

	return prefPage;
    }

    private Table getDSLTable(Composite prefPage) {
	final Table languagesTable = new Table(prefPage, SWT.BORDER
		| SWT.H_SCROLL | SWT.V_SCROLL);

	final GridData gd_popartLanguagesTable = new GridData(SWT.FILL,
		SWT.FILL, true, false);
	gd_popartLanguagesTable.widthHint = 200;
	languagesTable.setLayoutData(gd_popartLanguagesTable);

	languagesTable.setLinesVisible(true);
	languagesTable.setHeaderVisible(true);

	int swtStyle = SWT.LEAD;
	final TableColumn dslExtensionNameCol = new TableColumn(languagesTable,
		swtStyle);
	dslExtensionNameCol.setWidth(120);
	dslExtensionNameCol.setText("DSL Name");

	final TableColumn interpreterClassCol = new TableColumn(languagesTable,
		swtStyle);
	interpreterClassCol.setWidth(221);
	interpreterClassCol.setText("Interpreter Class");
	return languagesTable;
    }

    private void fillDSLTableWithDSLs(Table languagesTable,
	    List<DSLDefinition> dsls) {
	for (DSLDefinition dsl : dsls) {
	    TableItem aDslItem = new TableItem(languagesTable, SWT.NONE);
	    aDslItem.setData(dsl.getLanguageKey());
	    aDslItem.setText(0, dsl.getDslName());
	    aDslItem.setText(1, dsl.getClassPath());
	}
    }

    private void refreshLanguagesTableData(Table languagesTable,
	    List<DSLDefinition> dsls) {
	languagesTable.removeAll();
	fillDSLTableWithDSLs(languagesTable, dsls);
    }

}
