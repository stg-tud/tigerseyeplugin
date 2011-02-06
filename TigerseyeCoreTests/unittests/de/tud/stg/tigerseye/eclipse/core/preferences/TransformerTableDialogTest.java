/**
 * 
 */
package de.tud.stg.tigerseye.eclipse.core.preferences;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utilities.TestUtilities;
import de.tud.stg.popart.builder.transformers.FileType;
import de.tud.stg.popart.builder.transformers.TransformationType;
import de.tud.stg.tigerseye.eclipse.core.preferences.TableDialog.CheckedItem;

/**
 * @author Leo Roos
 * 
 */
public class TransformerTableDialogTest{
	
	static{
		TestUtilities.initLogger();
	}
	
	private static final Logger logger = LoggerFactory
			.getLogger(TransformerTableDialogTest.class);

	private TableDialog dialog;
	private boolean dialogOpen;


	private Shell testShell;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Display d = Display.getDefault();
		testShell = new Shell(d);
		testShell.setVisible(true);
		testShell.setActive();
		dialogOpen = false;
		dialog = new TableDialog(testShell, "Test Selection");
	}
	
	@After
	public void tearDown() throws Exception {
		testShell.dispose();
	}
	

	@Test(timeout=100)
	public void shouldOpenAndClose() throws Exception {
		asyncOpenDialog();
		startCloser();
	}

	private void startCloser() throws InterruptedException {
		Thread closer = new Closer();
		closer.start();
		closer.join();
	}

	private void asyncOpenDialog() {
		synchronized (dialog) {
			dialogOpen = true;
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					dialog.getShell().setActive();
					dialog.open();
				}
			});
		}
	}
	
	@Test
	public void shouldContainNoChangedElements() throws Exception {
		List<CheckedItem> cis = newList(ci(nullId,"TestItem", true),ci(nullId,"item2",false));
		
		dialog.setItems(cis);
		asyncOpenDialog();
		startCloser();		
		List<CheckedItem> items = dialog.getChangedItems();		
		assertEqualLists(Collections.emptyList(), items);
	}

	private <T>List<T> newList(T ... t) {
		List<T> newList =  new ArrayList<T>();
		Collections.addAll(newList, t);
		return newList;
	}	
	
	@Ignore("Needs manual intervention")
	@Test
	public void shouldShowCheckedTableOfElements() throws Exception {
		CheckedItem el = ci(nullId, "name", false);
		List<CheckedItem> cis = newList(el, ci(nullId, "other", false), ci(nullId, "second", false), ci(nullId, "third", false));		
		dialog.setItems(cis);
		dialog.open();
		
		List<CheckedItem> items = dialog.getChangedItems();
		List<CheckedItem> expectedList = newList(el);
		assertEqualLists(expectedList,items);
	}

	private void assertEqualLists(List<?> expectedList,List<?> actual) {
		boolean equalList = ListUtils.isEqualList(expectedList, actual);
		assertTrue("expected "+ expectedList +" but was " +actual,equalList);
	}

	private CheckedItem ci(TransformationType key, String string, boolean b) {
		CheckedItem ci = new CheckedItem(key,string,b);
		return ci;
	}

	private final class Closer extends Thread {
		private boolean closed = false;
	
		@Override
		public void run() {
			while (!closed) {					
				if (dialogOpen) {
					closed = dialog.close();
					if(closed)
						dialogOpen = false;
					else{
						logger.info("Failed to close dialog");
					}
				}
				try {
					sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static final TransformationType nullId = new id();
	
	private static class id implements TransformationType{

		@Override
		public String getIdentifer() {
			return "String";
		}

		@Override
		public FileType getTransformationCategory() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
