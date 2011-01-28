package de.tud.stg.popart.builder.eclipse.dialoge;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.eclipse.jdt.internal.ui.text.PreferencesAdapter;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.popart.builder.eclipse.ITransformerConfigurationListener;
import de.tud.stg.popart.builder.transformers.FileType;
import de.tud.stg.popart.builder.transformers.Transformation;
import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;
import de.tud.stg.tigerseye.eclipse.core.TransformationHandler;

public class TransformerConfigurationDialoge {
private static final Logger logger = LoggerFactory.getLogger(TransformerConfigurationDialoge.class);


    private final ITransformerConfigurationListener transformerConfigurationListener;
    private final Map<String, Map<String, Boolean>> storedConfiguration;
	private final String extension;


    public TransformerConfigurationDialoge(final String extension,
	    final Shell parent,
	    ITransformerConfigurationListener transformerListener,
	    IPreferenceStore store) {
	transformerConfigurationListener = transformerListener;
	storedConfiguration = PreferencesStoreUtils.getConfiguration(store);
	this.extension = extension;

	Shell shell = new Shell(parent);
	shell.setText("Transformer configuration for DSL extension "
		+ extension);
	shell.setSize(640, 480);
	shell.setLayout(new FillLayout(SWT.VERTICAL));

	TransformerConfigurationDialoge.this
		.createBuilderConfigurationTable(shell);
	shell.open();
    }

	private void createBuilderConfigurationTable(final Shell innerParent) {
		CTabFolder tabFolder = new CTabFolder(innerParent, SWT.TOP | SWT.MULTI | SWT.BORDER);

		CTabItem[] tabItem = new CTabItem[4];

		// Group g = new Group(innerGroup, SWT.NONE);
		// g.setText("Transformation Configuration");
		// g.setLayout(new GridLayout(2, false));

		final Table[] builderConfigurationTable = new Table[4];

		for (int i = 0; i < 4; i++) {
			tabItem[i] = new CTabItem(tabFolder, SWT.NONE);

			builderConfigurationTable[i] = new Table(tabFolder, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.MULTI
					| SWT.H_SCROLL);

			tabItem[i].setControl(builderConfigurationTable[i]);
		}

		tabItem[0].setText(extension);
		tabItem[0].setToolTipText("Transformers that can be used on this specific DSL (" + extension + ")");
		tabItem[1].setText(FileType.POPART.name());
		tabItem[1].setToolTipText("Transformers that can be used on pure popart files");
		tabItem[2].setText(FileType.GROOVY.name());
		tabItem[2].setToolTipText("Transformers that can be used on pure groovy files");
		tabItem[3].setText(FileType.JAVA.name());
		tabItem[3].setToolTipText("Transformers that can be used on pure java files");

	Map<FileType, Collection<TransformationHandler>> availableTransformationHandlers = transformerConfigurationListener
		.getAvailableTransformers();

	Map<String, Collection<String>> availableTransformers = getAvailableTransformersAsFiletypeNameToTransformerClassStringMap(availableTransformationHandlers);

		ArrayList<Collection<String>> transformers = new ArrayList<Collection<String>>(4);

		transformers.add(availableTransformers.get(FileType.DSL.name));
	// transformers.get(0).addAll(availableTransformers.get(extension));
	transformers.get(0).addAll(new ArrayList<String>());// Dummy to avoid
							    // null pointer

		transformers.add(availableTransformers.get(FileType.POPART.name));
		transformers.add(availableTransformers.get(FileType.GROOVY.name));
		transformers.add(availableTransformers.get(FileType.JAVA.name));

		String[] ext = { extension, FileType.POPART.name(), FileType.GROOVY.name(), FileType.JAVA.name() };

		for (int i = 0; i < 4; i++) {
			Map<String, Boolean> map = storedConfiguration.get(ext[i]);

			if (map == null) {
				map = new HashMap<String, Boolean>();
			}

			for (String s : transformers.get(i)) {
				TableItem item = new TableItem(builderConfigurationTable[i], SWT.NONE);
				item.setText(s);
				item.setData(s);
				Boolean checked = map.get(s);

				item.setChecked(checked != null ? checked : false);
			}
		}

		logger.trace("transformers for {} availabe: {}", extension,
				availableTransformers);

		final Text descriptionText = new Text(innerParent, SWT.WRAP | SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		descriptionText.setSize(400, 200);

		for (int i = 0; i < 4; i++) {
			builderConfigurationTable[i].addListener(SWT.Selection, new TableItemListener(tabItem[i].getText(),
					builderConfigurationTable[i], descriptionText));
		}

		Composite controlButtonGroup = new Composite(innerParent, SWT.NONE);
		controlButtonGroup.setLayout(new GridLayout(3, false));

		Button apply = new Button(controlButtonGroup, SWT.BORDER);
		apply.setText("Apply");
		apply.addSelectionListener(new StoreConfigurationListener(builderConfigurationTable, extension));

		Button ok = new Button(controlButtonGroup, SWT.BORDER);
		ok.setText("OK");
		ok.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				new StoreConfigurationListener(builderConfigurationTable,
						extension).widgetSelected(e);

				innerParent.close();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Button cancel = new Button(controlButtonGroup, SWT.BORDER);
		cancel.setText("Cancel");
		cancel.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				innerParent.close();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private Map<String, Collection<String>> getAvailableTransformersAsFiletypeNameToTransformerClassStringMap(
		Map<FileType, Collection<TransformationHandler>> availableTransformationHandlers) {
	    Map<String, Collection<String>> availableTransformers = new HashMap<String, Collection<String>>();
	    for (FileType ft : FileType.values()) {
	        Collection<TransformationHandler> collection = availableTransformationHandlers
	    	    .get(ft);
	        Collection<String> transformations = new ArrayList<String>();
	        for (TransformationHandler transformationHandler : collection) {
	    	transformations.add(transformationHandler.getTransformation()
	    		.getClass().getName());
	        }

	        availableTransformers.put(ft.name, transformations);
	    }
	    return availableTransformers;
	}

    private class TableItemListener implements Listener {

		private final Table builderConfigurationTable;
		private final String extension;
		private final Text text;

		public TableItemListener(String string, Table builderConfigurationTable, Text text) {
			extension = string;
			this.builderConfigurationTable = builderConfigurationTable;
			this.text = text;
		}

		@Override
		public void handleEvent(Event event) {
			if (event.detail == SWT.CHECK) {
				Table table = ((Table) event.widget);
				TableItem item = table.getItem(table.getSelectionIndex());
				String clazzName = (String) item.getData();
		transformerConfigurationListener.setEnabled(extension,
			clazzName,
						item.getChecked());

			} else if (event.detail == 0) {
				Table table = ((Table) event.widget);
				TableItem item = table.getItem(table.getSelectionIndex());
				if (item != null) {
					String text = transformerConfigurationListener
							.getInformation((String) item.getData());

					if (text == null) {
						text = "no description available";
					}

					this.text.setText(text);
				}
			}
		}
	}

    private class ItemMover extends SelectionAdapter {
		private final Table table;
		private final int modifier;

		public ItemMover(Table table, int modifier) {
			this.table = table;
			this.modifier = modifier;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			int index = table.getSelectionIndex();
			int newIndex = index + modifier * 1;

			if ((newIndex < 0) || (newIndex >= table.getItemCount())) {
				return;
			}

			TableItem i1 = table.getItem(index);
			Object tmp = i1.getData();
			String str = i1.getText();
			boolean isChecked = i1.getChecked();

			table.remove(index);

			TableItem item = new TableItem(table, SWT.NONE, newIndex);
			item.setData(tmp);
			item.setText(str);
			item.setChecked(isChecked);
			table.setSelection(newIndex);
		}
	}

    private class StoreConfigurationListener implements SelectionListener {

		private final Table[] table;
		private final String extension;

		public StoreConfigurationListener(Table[] builderConfigurationTable, String extension) {
			table = builderConfigurationTable;
			this.extension = extension;
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			String[] extensions = { extension, FileType.POPART.name(), FileType.GROOVY.name(),
					FileType.JAVA.name() };

	    IPreferenceStore prefs = TigerseyeCore.getPreferences();

			for (int j = 0; j < 4; j++) {
				Map<String, Boolean> map = new HashMap<String, Boolean>();

				for (int i = 0; i < table[j].getItemCount(); i++) {
					TableItem item = table[j].getItem(i);
					String clazzName = (String) item.getData();
					boolean checked = item.getChecked();

					map.put(clazzName, checked);
				}

				PreferencesStoreUtils.storeConfiguration(prefs, extensions[j], map);

				for (Entry<String, Boolean> entry : map.entrySet()) {
					transformerConfigurationListener.setEnabled(extensions[j], entry.getKey(), entry.getValue());
				}

				storedConfiguration.put(extensions[j], map);
			}
		}
	}

    //
    // public static void
    // setTransformationConfigurationListener(ITransformerConfigurationListener
    // listener) {
    // transformerConfigurationListener = listener;
    //
    // for (Entry<String, Map<String, Boolean>> e1 :
    // storedConfiguration.entrySet()) {
    // for (Entry<String, Boolean> e2 : e1.getValue().entrySet()) {
    // transformerConfigurationListener.setEnabled(e1.getKey(), e2.getKey(),
    // e2.getValue());
    // }
    // }
    // }

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);

	new TransformerConfigurationDialoge("math", shell,
		new ITransformerConfigurationListener() {

			@Override
			public void setEnabled(String extension, String transformer, boolean enabled) {
				// TODO Auto-generated method stub

			}

			@Override
			public String getInformation(String transformer) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
		    public Map<FileType, Collection<TransformationHandler>> getAvailableTransformers() {
			TransformationHandler[] java = getAsTransformationHandler(new String[] {
				"jgp1", "java1", "java2" });
				TransformationHandler[] groovy = getAsTransformationHandler(new String[] { "jgp1", "groovy1", "groovy2" });
				TransformationHandler[] popart = getAsTransformationHandler(new String[] { "jgp1", "popart1", "popart2" });
				TransformationHandler[] dsl = getAsTransformationHandler(new String[] { "dsl1", "dsl2", "math1" });

			Map<FileType, Collection<TransformationHandler>> map = new HashMap<FileType, Collection<TransformationHandler>>();
			map.put(FileType.JAVA, Arrays.asList(java));
			map.put(FileType.GROOVY, Arrays.asList(groovy));
			map.put(FileType.POPART, Arrays.asList(popart));
			map.put(FileType.DSL, Arrays.asList(dsl));
			// map.put(extension,
			// Collections
			// .unmodifiableCollection(new ArrayList<String>()));

				return map;
			}

		    private TransformationHandler[] getAsTransformationHandler(
			    String[] strings) {
			TransformationHandler[] ths = new TransformationHandler[strings.length];
			for (int i = 0; i < strings.length; i++) {
			    String string = strings[i];
			    ths[i] = new TransformationHandler(string,
				    new Transformation() {

					@Override
					public Set<FileType> getSupportedFileTypes() {
					    FileType[] values = FileType
						    .values();
					    Random r = new Random();
					    Set<FileType> sups = new HashSet<FileType>();
					    for (int i = 0; i < 3; i++) {
						int index = r.nextInt(4);
						sups.add(values[index]);
					    }
					    return sups;
					}

					@Override
					public String getDescription() {
					    return null;
					}
				    });
			}
			return ths;
		    }
		}, new PreferencesAdapter());

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
			    display.sleep();
			}
		}
		display.dispose();
	}
}
