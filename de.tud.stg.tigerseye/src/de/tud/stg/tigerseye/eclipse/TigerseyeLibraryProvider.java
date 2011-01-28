package de.tud.stg.tigerseye.eclipse;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.UnhandledException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Plugin;

//FIXME refactoring tests
public class TigerseyeLibraryProvider extends Plugin {

	public static final String PLUGIN_ID = "de.tud.stg.tigerseye";

	private static TigerseyeLibraryProvider plugin;

	public TigerseyeLibraryProvider() {
		plugin = this;
	}

	public static TigerseyeLibraryProvider getDefault() {
		return plugin;
	}

	/**
	 * @return the folder containing the minimal dependencies for a project with Tigerseye nature.
	 * @throws IOException
	 */
	public static File getTigerseyeRuntimeLibraryFolder() throws IOException {
		String runtimeJarsFolder = "runtimeJars";
		File bundleFolder;
			bundleFolder = FileLocator.getBundleFile(getDefault().getBundle());
		File runtimeFolder = new File(bundleFolder, runtimeJarsFolder);
		if (!runtimeFolder.exists())
			throw new IllegalStateException(
					"Expected Tigerseye runtime folder does not exist." + runtimeJarsFolder);
		List<String> runtimeJars = new LinkedList<String>();
		Collections.addAll(runtimeJars, "edslNature.jar",
				"popartAnnotations.jar", "popart.jar");
		String[] listFiles = runtimeFolder.list();
		List<String> runtimeFolderActualContent = Arrays.asList(listFiles);
		boolean containsAll = runtimeFolderActualContent
				.containsAll(runtimeJars);
		if (!containsAll)
			throw new IllegalStateException("Expected to find " + runtimeJars
					+ " but found " + runtimeFolderActualContent);
		return runtimeFolder;
	}

}
