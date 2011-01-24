package de.tud.stg.tigerseye;

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

	public static File getTigerseyeRuntimeLibraryFolder() {
		String runtimeJarsFolder = "runtimeJars";
		File bundleFolder;
		try {
			bundleFolder = FileLocator.getBundleFile(getDefault().getBundle());
		} catch (IOException e) {
			throw new UnhandledException(e);
		}
		File runtimeFolder = new File(bundleFolder, runtimeJarsFolder);
		if (!runtimeFolder.exists())
			throw new IllegalStateException(
					"Couldn't Tigerseye runtime jar folder" + runtimeJarsFolder);
		List<String> runtimeJars = new LinkedList<String>();
		// The minimal dependencies for a project of Tigerseye nature
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
