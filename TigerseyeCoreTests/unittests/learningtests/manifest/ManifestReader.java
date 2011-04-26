package learningtests.manifest;

import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;

import javax.annotation.CheckForNull;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.osgi.launch.Equinox;
import org.eclipse.osgi.util.ManifestElement;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

public class ManifestReader {

	
	private Manifest manifest;


	enum AManifest {
		dsldefinitions("dsldefinitions_MANIFEST.MF"), logodsl("logodsl_MANIFEST.MF"), simplesql("simplesql_MANIFEST.MF");
		
		public final String name;
		
		private AManifest(String name) {
			this.name = name;
		}
		
	}
	
	private InputStream getManifest(AManifest aman){
		return ManifestReader.class.getResourceAsStream("resources/"+aman.name);
	}
	
	@Before
	public void bf(){
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
		ManifestElement[] elements = manifest.getElements(Constants.BUNDLE_CLASSPATH);
		assertTrue(elements.length == 2);
		for (ManifestElement mel : elements) {
			System.out.println("manifestel " + mel + " \totherinfo:" + ToStringBuilder.reflectionToString(mel));
		}
	}
	
	
	static class Manifest{

		private Map headers;

		public void load(InputStream manifest) throws IOException, BundleException {
				headers = ManifestElement.parseBundleManifest(manifest, null);
		}
		
		/**
		 * @see org.eclipse.osgi.launch.Equinox
		 */
		public @CheckForNull String getValue(String key) {
			if(headers == null)
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
		 * @return elements for the key, may return empty array if key not specified
		 * @throws BundleException 
		 */
		public ManifestElement[] getElements(String key) throws BundleException{
			if(headers == null)
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
	
}
