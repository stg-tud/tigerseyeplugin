package de.tud.stg.tigerseye.eclipse.core.preferences;

import java.util.Set;

import org.eclipse.jface.dialogs.DialogTray;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.api.ITransformationHandler;
import de.tud.stg.tigerseye.eclipse.core.api.Transformation;
import de.tud.stg.tigerseye.eclipse.core.api.TransformationType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ASTTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TextualTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TransformationHandler;

public class TransformationsTableDialog extends TableDialog {

    private static final int clientidstart = IDialogConstants.CLIENT_ID;

    public static final int TRAY_TOGGLE_ID = clientidstart + 1;
    public static final int TO_DEFAULT_ID = clientidstart + 2;

    private static final Logger logger = LoggerFactory
	    .getLogger(TransformationsTableDialog.class);

    private final boolean defaultCheckState;
    private TransformationHandlerTray tray;
    private CheckedItem checkedItem = new CheckedItem("", false);

    public class TransformationHandlerTray extends DialogTray {

	private Text infoArea;

	public TransformationHandlerTray() {
	}

	@Override
	protected Control createContents(Composite parent) {
	    Composite trayArea = new Composite(parent, SWT.NONE);
	    trayArea.setLayout(new GridLayout());
	    trayArea.setLayoutData(newFillGD());
	    infoArea = new Text(trayArea, SWT.MULTI | SWT.READ_ONLY
		    | SWT.WRAP | SWT.V_SCROLL);
	    GridData infoGD = newFillGD();
	    infoGD.widthHint = 200;
	    infoArea.setLayoutData(infoGD);
	    String content = getFormattedItem();
	    infoArea.setText(content);
	    // trayArea.pack();
	    return trayArea;
	}

	private GridData newFillGD() {
	    GridData layoutData2 = new GridData(SWT.LEFT, SWT.FILL, true, true);
	    return layoutData2;
	}

	private String getFormattedItem() {
	    Object data = checkedItem.data;
	    if (data instanceof TransformationHandler) {
		ITransformationHandler handler = (ITransformationHandler) data;
		String formatted = format(handler);
		return formatted;
	    } else
		return "<no data to display>";
	}

	private String format(ITransformationHandler handler) {
	    StringBuilder sb = new StringBuilder();
	    Transformation transformation = handler.getTransformation();
	    if (transformation instanceof TextualTransformation) {
		sb.append("Textual Transformation");
	    } else if (transformation instanceof ASTTransformation) {
		sb.append("AST Transformation");
	    } else {
		sb.append("Unknown Transformation Type");
	    }

	    sb.append("\n\nSupported FileTypes:\n");
	    Set<TransformationType> supportedFileTypes = handler
		    .getTransformation()
		    .getSupportedFileTypes();
	    for (TransformationType fileType : supportedFileTypes) {
		sb.append(fileType.toString()).append("\n");
	    }
	    sb.append("\nDescription:\n");
	    sb.append(handler.getTransformation().getDescription());
	    String formatted = sb.toString();
	    return formatted;
	}

	public void update() {
	    this.infoArea.setText(getFormattedItem());
	}

    }

    public TransformationsTableDialog(Shell shell, String title,
	    boolean defaultCheckState) {
	super(shell, title);
	this.defaultCheckState = defaultCheckState;
    }

    @Override
    protected void itemSelected(CheckedItem checkedItem) {
	this.checkedItem = checkedItem;
	logger.info("item selected: " + checkedItem);
	if (tray != null) {
	    tray.update();
	}
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
	// Default OK and Cancel Button copied from super method to replace "OK"
	// label with "Save"
	createButton(parent, IDialogConstants.OK_ID, "Save", true);
	createButton(parent, IDialogConstants.CANCEL_ID,
		IDialogConstants.CANCEL_LABEL, false);
	// Additionally create Default and toggle Tray button
	createButton(parent, TO_DEFAULT_ID, "To Default", false);

	Image toggleImage = PlatformUI.getWorkbench().getSharedImages()
		.getImage(ISharedImages.IMG_OBJS_INFO_TSK);
	createButton(parent, TRAY_TOGGLE_ID, "", false).setImage(toggleImage);

    }

    @Override
    protected void buttonPressed(int buttonId) {
	switch (buttonId) {
	case TO_DEFAULT_ID:
	    restoreDefaultsPressed();
	    return;
	case TRAY_TOGGLE_ID:
	    toggleTrayPressed();
	    return;
	}
	super.buttonPressed(buttonId);
    }

    private void toggleTrayPressed() {
	if (getTray() == null) {
	    this.tray = new TransformationHandlerTray();
	    openTray(tray);
	} else {
	    closeTray();
	    this.tray = null;
	}
    }

    private void restoreDefaultsPressed() {
	TableItem[] items = getTable().getItems();
	for (TableItem it : items) {
	    it.setChecked(defaultCheckState);
	}
    }

}
