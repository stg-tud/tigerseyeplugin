package learningtests.jar;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JarReaderLearningTests {

	URL jarTestFile = JarReaderLearningTests.class.getResource("resources/jartestbundle.jar");
	private File testfile;

	@Before
	public void bf() throws Exception {
		testfile = new File(jarTestFile.toURI());
	}

	@Test
	public void readFiles() throws Exception {
		JarFile jarFile = new JarFile(testfile);
		Manifest manifest = jarFile.getManifest();
		printManifest(manifest);

		JarEntry entry = (JarEntry) jarFile.getEntry("plugin.xml");
		printJarEntry(jarFile, entry);

		JarEntry pluginxml = (JarEntry) jarFile.getEntry("META-INF/MANIFEST.MF");
		printJarEntry(jarFile, pluginxml);

	}

	private static final Logger logger = LoggerFactory.getLogger(JarReaderLearningTests.class);

	private void printJarEntry(JarFile jarFile, JarEntry pluginxml) throws IOException {
		String pluginxmlstr = IOUtils.toString(jarFile.getInputStream(pluginxml));
		logger.trace(pluginxmlstr);
	}

	private void printManifest(Manifest manifest) {
		Set<Entry<String, Attributes>> entrySet = manifest.getEntries().entrySet();

		logger.trace("size: " + entrySet.size());

		for (Entry<String, Attributes> entry : entrySet) {
			logger.trace(ToStringBuilder.reflectionToString(entry));
		}
	}

}
