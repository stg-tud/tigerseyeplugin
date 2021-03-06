package de.tud.stg.tigerseye.eclipse.core.preferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.UnhandledException;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.api.DSLTransformation;
import de.tud.stg.tigerseye.eclipse.core.api.FileTypeTransformation;
import de.tud.stg.tigerseye.eclipse.core.api.ILanguageProvider;
import de.tud.stg.tigerseye.eclipse.core.api.ITransformationHandler;
import de.tud.stg.tigerseye.eclipse.core.api.TransformationType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TransformationHandler;
import de.tud.stg.tigerseye.eclipse.core.preferences.TableDialog.CheckedItem;

public class TransformationsPreferencePage extends PreferencePage implements
	IWorkbenchPreferencePage {

    private static final Logger logger = LoggerFactory
	    .getLogger(TransformationsPreferencePage.class);
    private List<ITransformationHandler> configuredTransformations;
    private Table languagesTable;
    private ILanguageProvider languageProvider;
    private Table resourcesTable;

    public TransformationsPreferencePage() {
	super("Builder Preference Page");
    }

    @Override
    public void init(IWorkbench workbench) {
	noDefaultAndApplyButton();
	setPreferenceStore(TigerseyeCore.getPreferences());
	this.configuredTransformations = new ArrayList<ITransformationHandler>(TigerseyeCore
		.getTransformationProvider().getConfiguredTransformations());
	this.languageProvider = TigerseyeCore.getLanguageProvider();
    }

    private ILanguageProvider getLanguageProvider() {
	return languageProvider;
    }

    private List<ITransformationHandler> getConfiguredTransformations() {
	return configuredTransformations;
    }

    @Override
    protected Control createContents(Composite parent) {
	Composite prefPage = new Composite(parent, SWT.NONE);
	prefPage.setLayout(new GridLayout());

	Group dslSpecGroup = createHorizontalGrabbingGroup(prefPage,
		"DSL specific Transformations");
	this.languagesTable = createLanguagesTable(dslSpecGroup);
	createDSLSpecificTransformersButton(dslSpecGroup);

	Group ftSpecGroup = createHorizontalGrabbingGroup(prefPage,
		"Resource specific Transformations");
	this.resourcesTable = createResourcesTable(ftSpecGroup);
	createResourceSpecificTransformersButton(ftSpecGroup);

	return prefPage;
    }

    private void createResourceSpecificTransformersButton(Group ftSpecGroup) {
	final Button editResourcesButton = createPushButton(ftSpecGroup,
		"Edit Resource Specific Transformers.");
	editResourcesButton.setEnabled(false);

	this.resourcesTable.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		editResourcesButton.setEnabled(true);
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {
		openFileTypeTransformationDialog((FileType) e.item.getData());
	    }

	});

	editResourcesButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		TableItem[] selection = resourcesTable.getSelection();
		if (selection.length > 0)
		    openFileTypeTransformationDialog((FileType) selection[0]
			    .getData());
	    }

	});
    }

    private Table createResourcesTable(Group ftSpecGroup) {
	Table newTable = newTable(ftSpecGroup);

	String[] cols = { "ResourceType", "File Extension" };
	TableColumn[] tcols = createColumns(newTable, cols);
	for (FileType fileType : FileType.values()) {
	    TableItem item = new TableItem(newTable, SWT.NONE);
	    item.setData(fileType);
	    item.setText(new String[] { fileType.name, fileType.srcFileEnding });
	}
	for (TableColumn col : tcols) {
	    col.pack();
	}
	return newTable;
    }

    private TableColumn[] createColumns(Table newTable, String[] cols) {
	TableColumn[] newCols = new TableColumn[cols.length];
	for (int i = 0; i < cols.length; i++) {
	    TableColumn col = new TableColumn(newTable, SWT.LEAD);
	    col.setText(cols[i]);
	    newCols[i] = col;
	}
	return newCols;
    }

    private Group createHorizontalGrabbingGroup(Composite prefPage,
	    String message) {
	Group group = new Group(prefPage, SWT.SHADOW_ETCHED_IN);
	group.setFont(prefPage.getFont());
	group.setText(message);
	group.setLayout(new GridLayout());
	group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	return group;
    }

    private Table createLanguagesTable(Group dslSpecGroup) {
	final Table languagesTable = newTable(dslSpecGroup);
	TableColumn[] createColumns = createColumns(languagesTable,
		new String[] { "DSL Name", "Interpreter Class" });
	fillDSLTableWithDSLs(languagesTable, getLanguageProvider()
		.getDSLDefinitions());
	for (TableColumn tableColumn : createColumns) {
	    tableColumn.pack();
	}
	return languagesTable;
    }

    private Table newTable(Composite prefPage) {
	final Table languagesTable = new Table(prefPage, SWT.BORDER
		| SWT.H_SCROLL | SWT.V_SCROLL);

	final GridData gd_popartLanguagesTable = new GridData(SWT.FILL,
		SWT.FILL, true, false);
	gd_popartLanguagesTable.widthHint = 200;
	languagesTable.setLayoutData(gd_popartLanguagesTable);

	languagesTable.setLinesVisible(true);
	languagesTable.setHeaderVisible(true);
	return languagesTable;
    }

    private void fillDSLTableWithDSLs(Table languagesTable,
	    Collection<DSLDefinition> dsls) {
	for (DSLDefinition dsl : dsls) {
	    TableItem aDslItem = new TableItem(languagesTable, SWT.NONE);
	    aDslItem.setData(dsl);
	    aDslItem.setText(0, dsl.getDslName());
	    aDslItem.setText(1, dsl.getClassPath());
	}
    }

    private void createDSLSpecificTransformersButton(Composite prefPage) {
	final Button dSLSpecificButton = createPushButton(prefPage,
		"Edit DSL specific Transformers");
	dSLSpecificButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		TableItem[] selection = languagesTable.getSelection();
		if (selection.length > 0)
		    openDSLTransformationDialog((DSLDefinition) selection[0]
			    .getData());
	    }

	});
	dSLSpecificButton.setEnabled(false);
	languagesTable.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		dSLSpecificButton.setEnabled(true);
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {
		openDSLTransformationDialog((DSLDefinition) e.item.getData());
	    }

	});
    }

    private void openDSLTransformationDialog(DSLDefinition selectedDSL) {
	String title = "Transformations for '" + selectedDSL.getDslName() + "'";

	DSLTransformation dslTransformation = new DSLTransformation(selectedDSL, getPreferenceStore());

	List<CheckedItem> items = getTransformationsForFiletypeAssociatedToIdentity(dslTransformation);
	List<CheckedItem> changedItems = openDialogForItemsReturnChanged(items,
 title,
 dslTransformation);
	saveItemsFor(changedItems, dslTransformation);
    }

    private void openFileTypeTransformationDialog(FileType data) {
	String title = "Transformations for FileType'" + data.name + "'";
	openFileTypeTransformationDialog(data, title);
    }

    private void openFileTypeTransformationDialog(FileType fileType, String title) {
	List<ITransformationHandler> supportedTransformationsForFiletype = getSupportedTransformationsForFiletype(fileType);
	sortTransformationHandlerAfterBuildOrder(supportedTransformationsForFiletype);

	FileTypeTransformation fileTypeTransformation = new FileTypeTransformation(fileType, getPreferenceStore());

	List<CheckedItem> items = makeCheckItemsForTransformationsAssociatedToIdentity(
		supportedTransformationsForFiletype, fileTypeTransformation);
	List<CheckedItem> changedItems = openDialogForItemsReturnChanged(items, title, fileTypeTransformation);
	saveItemsFor(changedItems, fileTypeTransformation);
    }

    private void sortTransformationHandlerAfterBuildOrder(List<ITransformationHandler> transformationHandler) {
	Collections.sort(transformationHandler, new Comparator<ITransformationHandler>() {
	    @Override
	    public int compare(ITransformationHandler o1, ITransformationHandler o2) {
		return o1.getTransformation().getBuildOrderPriority() - o2.getTransformation().getBuildOrderPriority();
	    }
	});
    }

    private List<CheckedItem> openDialogForItemsReturnChanged(
List<CheckedItem> items, String title,
	    TransformationType tt) {
	TransformationsTableDialog transformerDialog = new TransformationsTableDialog(
getShell(), title, tt);
	transformerDialog.setTitle("Select Transformations");
	transformerDialog.setItems(items);
	transformerDialog.open();
	List<CheckedItem> changedItems = transformerDialog.getChangedItems();
	return changedItems;
    }

    private void saveItemsFor(List<CheckedItem> changedItems,
	    TransformationType toBeAssociatedTo) {
	logger.trace("About to save items {} for {}", new Object[] {
		changedItems, toBeAssociatedTo });
	for (CheckedItem checkedItem : changedItems) {
	    Object data = checkedItem.data;

	    if (data == null || !(data instanceof TransformationHandler))
		throw new UnhandledException(
			"Wrong configured. Expected TransformationHandler",
			null);
	    ITransformationHandler transformationHandler = (ITransformationHandler) data;
	    toBeAssociatedTo.setActiveStateFor(transformationHandler, checkedItem.checked);
	}
    }



    private List<CheckedItem> getTransformationsForFiletypeAssociatedToIdentity(TransformationType identitiy) {
	List<ITransformationHandler> supportedTransformers = getConfiguredTransformations();// getSupportedTransformationsForFiletype(identitiy);
	sortTransformationHandlerAfterBuildOrder(supportedTransformers);

	List<CheckedItem> items = makeCheckItemsForTransformationsAssociatedToIdentity(
		supportedTransformers, identitiy);
	return items;

    }

    private List<CheckedItem> makeCheckItemsForTransformationsAssociatedToIdentity(
	    Collection<ITransformationHandler> supportedTransformers,
	    TransformationType identity) {
	List<CheckedItem> items = new ArrayList<CheckedItem>();
	for (ITransformationHandler handler : supportedTransformers) {
	    boolean activeFor = identity.isActiveFor(handler);
	    items.add(new CheckedItem(handler, handler.getName(), activeFor));
	}
	return items;
    }

    private ArrayList<ITransformationHandler> getSupportedTransformationsForFiletype(
	    FileType type) {
	// ArrayList<ITransformationHandler> supportedTransformers = new
	// ArrayList<ITransformationHandler>();
	// for (ITransformationHandler handler : getConfiguredTransformations())
	// {
	// if (handler.supports(type)) {
	// supportedTransformers.add(handler);
	// }
	// }
	// return supportedTransformers;

	return new ArrayList<ITransformationHandler>(getConfiguredTransformations());

    }

    private ArrayList<ITransformationHandler> getSupportedTransformationsForFiletype(TransformationType type) {
	ArrayList<ITransformationHandler> supportedTransformers = new ArrayList<ITransformationHandler>();
	for (ITransformationHandler handler : getConfiguredTransformations()) {
	    if (type.isActiveFor(handler)) {
		supportedTransformers.add(handler);
	    }
	}
	return supportedTransformers;
    }

    private Button createPushButton(Composite prefPage, String javaButtonString) {
	final Button button = new Button(prefPage, SWT.PUSH);
	button.setText(javaButtonString);
	GridData gd = new GridData();
	gd.widthHint = 250;
	button.setLayoutData(gd);
	return button;
    }

}
