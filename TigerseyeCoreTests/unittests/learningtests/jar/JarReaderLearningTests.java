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

public class JarReaderLearningTests {

	URL jarTestFile = JarReaderLearningTests.class.getResource("resources/jartestbundle.jar");
	private File testfile;
	
	@Before
	public void bf () throws Exception{
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


	private void printJarEntry(JarFile jarFile, JarEntry pluginxml)
			throws IOException {
		String pluginxmlstr = IOUtils.toString(jarFile.getInputStream(pluginxml));
		System.out.println(pluginxmlstr);
	}


	private void printManifest(Manifest manifest) {
		Set<Entry<String,Attributes>> entrySet = manifest.getEntries().entrySet();
		
		System.out.println("size: " + entrySet.size());
		
		for (Entry<String, Attributes> entry : entrySet) {
			System.out.println(ToStringBuilder.reflectionToString(entry));
		}
	}
	
}
