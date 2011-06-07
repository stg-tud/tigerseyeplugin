package learningtests.manifest;

import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Packer;
import java.util.jar.Pack200.Unpacker;
import java.util.zip.ZipEntry;

import javax.annotation.CheckForNull;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.osgi.launch.Equinox;
import org.eclipse.osgi.util.ManifestElement;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

public class ManifestReaderLearningTest {

	private Manifest manifest;

	enum AManifest {
		dsldefinitions("dsldefinitions_MANIFEST.MF"), logodsl(
				"logodsl_MANIFEST.MF"), simplesql("simplesql_MANIFEST.MF");

		public final String name;

		private AManifest(String name) {
			this.name = name;
		}

	}

	private InputStream getManifest(AManifest aman) {
		return ManifestReaderLearningTest.class
				.getResourceAsStream("resources/" + aman.name);
	}

	@Before
	public void bf() {
		manifest = new Manifest();
	}

	@Test
	public void readsimpleManifest() throws Exception {
		InputStream amanstream = getManifest(AManifest.dsldefinitions);
		String string = IOUtils.toString(amanstream);
		assertFalse(string.isEmpty());
		java.util.jar.Manifest manifest = new java.util.jar.Manifest(amanstream);
		Map<String, Attributes> entries = manifest.getEntries();
		assertThat(entries.size(), equalTo(0));
	}

	@Test
	public void osgidslDefinitionsManifestElement() throws Exception {
		InputStream amanstream = getManifest(AManifest.dsldefinitions);
		this.manifest.load(amanstream);
		Set entries = manifest.headers.entrySet();
		assertFalse(entries.isEmpty());
		for (Object object : entries) {
			System.out.println(object);
		}
		String classpath = manifest.getValue(Constants.BUNDLE_CLASSPATH);
		assertNull(classpath);
	}

	@Test
	public void getAvailableClasspathEntries() throws Exception {
		InputStream logostream = getManifest(AManifest.logodsl);
		manifest.load(logostream);
		String value = manifest.getValue(Constants.BUNDLE_CLASSPATH);
		assertNotNull(value);
		ManifestElement[] elements = manifest
				.getElements(Constants.BUNDLE_CLASSPATH);
		assertTrue(elements.length == 2);
		List<String> results = new ArrayList<String>();
		for (ManifestElement mel : elements) {
			printME(mel);
			results.add(mel.getValue());
		}

		assertTrue(results.contains("."));
		assertTrue(results.contains("lib/javalogo.jar"));
	}

	private void printME(ManifestElement mel) {
		System.out.println(ToStringBuilder.reflectionToString(mel));
	}

	static class Manifest {

		private Map headers;

		public void load(InputStream manifest) throws IOException,
				BundleException {
			headers = ManifestElement.parseBundleManifest(manifest, null);
		}

		/**
		 * @see org.eclipse.osgi.launch.Equinox#getValue(Map headers, String
		 *      key)
		 */
		public @CheckForNull
		String getValue(String key) {
			if (headers == null)
				return null;
			String headerSpec = (String) headers.get(key);
			if (headerSpec == null)
				return null;
			ManifestElement[] elements;
			try {
				elements = ManifestElement.parseHeader(key, headerSpec);
			} catch (BundleException e) {
				return null;
			}
			if (elements == null)
				return null;
			return elements[0].getValue();
		}

		public static final ManifestElement[] emptyel = new ManifestElement[0];

		/**
		 * @param key
		 * @return elements for the key, may return empty array if key not
		 *         specified
		 * @throws BundleException
		 */
		public ManifestElement[] getElements(String key) throws BundleException {
			if (headers == null)
				return emptyel;
			String headerSpec = (String) headers.get(key);
			if (headerSpec == null)
				return emptyel;
			ManifestElement[] elements;
			elements = ManifestElement.parseHeader(key, headerSpec);
			if (elements == null)
				return emptyel;
			return elements;
		}
	}

	@Ignore("no jar testing necessary")
	@Test
	public void testjar() throws Exception {

		URI somejar = ManifestReaderLearningTest.class.getResource(
				"mockito-all-1.8.5.jar").toURI();
		JarFile jarFile = new JarFile(new File(somejar));
		Enumeration<JarEntry> entries = jarFile.entries();
		// while (entries.hasMoreElements()) {
		// JarEntry jarEntry = (JarEntry) entries.nextElement();
		// System.out.println(jarEntry);
		// }

		// String manifest = "META-INF/MANIFEST.MF";
		// printentry(jarFile, manifest);

		Enumeration<URL> systemResources = ClassLoader.getSystemClassLoader()
				.getResources("*");
		while (systemResources.hasMoreElements()) {
			URL url = (URL) systemResources.nextElement();
			System.out.println(url);

		}
	}

	// private String unpack(File jararch) {
	//
	// try {
	// FileOutputStream fostream = new FileOutputStream("/tmp/test.jar");
	// JarOutputStream jostream = new JarOutputStream(fostream);
	// Unpacker unpacker = Pack200.newUnpacker();
	// // Call the unpacker
	// unpacker.unpack(jararch, jostream);
	// // Must explicitly close the output.
	//
	// jostream.
	//
	// jostream.close();
	// } catch (IOException ioe) {
	// ioe.printStackTrace();
	// }
	//
	// }

}
