package de.tud.stg.tigerseye.eclipse;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.Plugin;

import de.tud.stg.tigerseye.eclipse.dslsupport.TigerseyeSupportActivator;

public class TigerseyeLibraryProvider extends Plugin {

	public static final String PLUGIN_ID = "de.tud.stg.tigerseye.eclipse";

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
		File runtimeSupportJar = TigerseyeSupportActivator.getDefault().getRuntimeSupportJar();
		return new File[]{runtimeSupportJar};
	}

}
