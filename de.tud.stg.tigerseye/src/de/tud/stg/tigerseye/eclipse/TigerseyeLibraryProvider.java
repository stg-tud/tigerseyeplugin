package de.tud.stg.tigerseye.eclipse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Plugin;

import de.tud.stg.tigerseye.eclipse.dslsupport.TigerseyeSupportActivator;

//FIXME refactoring and tests
public class TigerseyeLibraryProvider extends Plugin {

	public static final String PLUGIN_ID = "de.tud.stg.tigerseye";
	//FIXME(Leo Roos): remove when tested
	private static final String[] minimalConfiguration = {/* "edslNature.jar",
			"popartAnnotations.jar",*/ /*"popart.jar"*/ };

	private static TigerseyeLibraryProvider plugin;

	public TigerseyeLibraryProvider() {
		plugin = this;
	}

	public static TigerseyeLibraryProvider getDefault() {
		return plugin;
	}

	/**
	 * @return the Files representing the minimal dependencies for a project with the
	 *         Tigerseye nature.
	 * @throws IOException if a problem occurred while resolving the locations of the runtime libraries. 
	 */
	public static File[] getTigerseyeRuntimeLibraries() throws IOException {
//		String runtimeJarsFolder = "runtimeJars";
//		File bundleFolder = FileLocator.getBundleFile(getDefault().getBundle());
//		File runtimeFolder = new File(bundleFolder, runtimeJarsFolder);
//		if (!runtimeFolder.exists())
//			throw new IllegalStateException(
//					"Expected Tigerseye runtime folder does not exist."
//							+ runtimeJarsFolder);
//		checkMinimalConfiguration(runtimeFolder);
		List<File> result = new ArrayList<File>();
//		for (String fileName : minimalConfiguration) {
//			result.add(new File(runtimeFolder, fileName));
//		}
		File runtimeSupportJar = TigerseyeSupportActivator.getDefault().getRuntimeSupportJar();
		result.add(runtimeSupportJar);
		return result.toArray(new File[0]);
	}

	private static void checkMinimalConfiguration(File runtimeFolder) {
		List<String> runtimeJars = new LinkedList<String>();
		Collections.addAll(runtimeJars, minimalConfiguration);
		String[] listFiles = runtimeFolder.list();
		List<String> runtimeFolderActualContent = Arrays.asList(listFiles);
		boolean containsAll = runtimeFolderActualContent
				.containsAll(runtimeJars);
		if (!containsAll)
			throw new IllegalStateException("Expected to find " + runtimeJars
					+ " but found " + runtimeFolderActualContent);
	}

}
