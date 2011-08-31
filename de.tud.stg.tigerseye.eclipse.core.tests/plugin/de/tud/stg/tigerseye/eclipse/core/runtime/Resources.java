package de.tud.stg.tigerseye.eclipse.core.runtime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.test.TransformationUtils;

enum Resources {
	defaultbundleclasspath("projectwithdefaultbundleclasspath"), //
	somebundleclasspath("projectwithspecialbundleclasspath"), //
	unknownformatfile("filewithunknownformat.undf"), //
	jarfile("jartestbundle.jar"), //
	developmentProject("developmentprojectwithmetadata"), //
	;

	private static final String bundleRelativeResourcesPath = "resources/resolverTestsResources/";

	private static final Logger logger = LoggerFactory
			.getLogger(Resources.class);

	public final String NAME;

	private Resources(String name) {
		this.NAME = name;
	}

	public InputStream getURI() {
		String name2 = "resources/" + NAME;
		Class<DSLClasspathResolverTest> relRoot = DSLClasspathResolverTest.class;
		InputStream url = relRoot.getResourceAsStream(name2);
		if (url == null)
			throw new IllegalArgumentException("No resource " + name2
					+ " relative to " + relRoot.getCanonicalName() + " found");
		else
			return url;
	}

	public File getFileInPluginRun() throws Exception {
		assertPlatformRunning();
		Bundle bundle = Platform
				.getBundle(TransformationUtils.PLUGIN_ID_OF_CORE_TESTFRAGMENT);
		URL entry = bundle.getEntry(new File(bundleRelativeResourcesPath,
				this.NAME).getPath());
		URL fileURL = FileLocator.toFileURL(entry);
		return new File(fileURL.toURI());

	}

	public static void assertPlatformRunning() {
		boolean running = Platform.isRunning();
		if (!running)
			throw new IllegalStateException(
					"This method is only intended to be used in Plug-in running mode.");
	}
}