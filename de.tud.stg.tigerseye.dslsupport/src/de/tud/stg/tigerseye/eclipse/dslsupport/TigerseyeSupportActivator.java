package de.tud.stg.tigerseye.eclipse.dslsupport;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Plugin;

public class TigerseyeSupportActivator extends Plugin {

	public static final String PLUGIN_ID = "de.tud.stg.tigerseye.eclipse.dslsupport";

	private static final String runtimeSupport = "lib/tigerseye-support.jar";

	private static TigerseyeSupportActivator plugin;

	public TigerseyeSupportActivator() {
		plugin = this;
	}

	public static TigerseyeSupportActivator getDefault() {
		return plugin;
	}

	/**
	 * @return the jar-file containing classes necessary to execute and develop
	 *         a Tigerseye DSL.
	 * @throws IOException
	 *             when expected support can not be found or problem occured while trying to resolve its location.
	 */
	public File getRuntimeSupportJar() throws IOException {
		File bundleFolder;
		bundleFolder = FileLocator.getBundleFile(getDefault().getBundle());
		File runtimeFolder = new File(bundleFolder, runtimeSupport);
		return runtimeFolder;
	}

}
