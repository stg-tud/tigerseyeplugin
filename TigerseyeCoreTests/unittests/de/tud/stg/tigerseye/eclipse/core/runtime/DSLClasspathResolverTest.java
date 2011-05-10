package de.tud.stg.tigerseye.eclipse.core.runtime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

public class DSLClasspathResolverTest {

	@Mock(answer = Answers.RETURNS_SMART_NULLS)
	private Bundle bundleMock;
	@Mock
	private FileLocatorWrapper fileLocator;

	private DSLClasspathResolver resolver;

	enum Resources {
		defaultbundleclasspath("projectwithdefaultbundleclasspath"), //
		somebundleclasspath("projectwithspecialbundleclasspath"), //
		unknownformatfile("filewithunknownformat.undf"), //
		jarfile("jartestbundle.jar");

		public final String NAME;

		private Resources(String name) {
			this.NAME = name;
		}

		public File getFile() throws URISyntaxException {
			URI resource = DSLClasspathResolverTest.class.getResource(
					"resources/" + NAME).toURI();
			return new File(resource);
		}
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		resolver = new DSLClasspathResolver();

	}

	@Ignore("only works as plug-in run")
	@Test
	public void testReadManifestFromFolder() throws Exception {
		String aBundleIdentifier = "org.eclipse.core.runtime";
		String tigerBundle = "de.tud.stg.tigerseye";
		Bundle eclipseCoreBundle = Platform.getBundle(aBundleIdentifier);
		printVerbose(eclipseCoreBundle);
		Bundle tb = Platform.getBundle(tigerBundle);

		// String[] allContributions = ManifestElement.getArrayFromList((String)
		// tbh.getBundleData().getManifest().get(Constants.REGISTERED_POLICY));
		// System.out.println(allContributions);

		Dictionary<String, String> headers = (Dictionary<String, String>) tb
				.getHeaders();
		String tigerCP = headers.get(Constants.BUNDLE_CLASSPATH);
		String tigerseyeplugincp = "libs/aterm-java.jar,libs/jjtraveler.jar,libs/shared-objects.jar,.,libs/jsr305.jar,libs/popart.jar,libs/popartAnnotations.jar";
		assertThat(tigerCP, equalTo(tigerseyeplugincp));

		String location = eclipseCoreBundle.getLocation();
		String eclipseCoreBundleLocation = "reference:file:/home/leo/bin/eclipses/e36tigerseyefromscratch/plugins/org.eclipse.core.runtime_3.6.0.v20100505.jar";
		assertThat(location, equalTo(eclipseCoreBundleLocation));

		URL find = FileLocator.find(eclipseCoreBundle, new Path("/"), null);
		// printVerbose(find);
		assertEquals("bundleentry", find.getProtocol());

		File coreFile = FileLocator.getBundleFile(eclipseCoreBundle);
		assertTrue(coreFile.isFile() && coreFile.getName().endsWith("jar"));

		File tigerDir = FileLocator.getBundleFile(tb);
		assertTrue(tigerDir.isDirectory());

//		assertThat(Arrays.asList(resolver.resolveCPEntriesForBundle(tb)),
//				hasItems("aterm.jar"));

		// String eclipseCoreBundleToString =
		// "org.eclipse.osgi.framework.internal.core.BundleHost@6c97d3f0[proxy=org.eclipse.core.runtime; bundle-version=\"3.6.0.v20100505\",context=org.eclipse.osgi.framework.internal.core.BundleContextImpl@78c6cbc,fragments=<null>,framework=org.eclipse.osgi.framework.internal.core.Framework@411edf1c,state=32,stateChanging=<null>,bundledata=org.eclipse.core.runtime_3.6.0.v20100505,statechangeLock=java.lang.Object@5aba9dff,domain=<null>,manifestLocalization=org.eclipse.osgi.framework.internal.core.ManifestLocalization@11dafee2]\n";
		// String tigerseyeBundle =
		// "org.eclipse.osgi.framework.internal.core.BundleHost@290e1513[proxy=de.tud.stg.tigerseye; bundle-version=\"0.0.1.qualifier\",context=<null>,fragments=<null>,framework=org.eclipse.osgi.framework.internal.core.Framework@411edf1c,state=8,stateChanging=<null>,bundledata=de.tud.stg.tigerseye_0.0.1.qualifier,statechangeLock=java.lang.Object@46edc3f0,domain=<null>,manifestLocalization=org.eclipse.osgi.framework.internal.core.ManifestLocalization@23dae5f1]\n";
		//
		// String eclipseFindlocatorFounURL =
		// "java.net.URL@5169751d[protocol=bundleentry,host=153.fwk163209334,port=0,file=/,authority=153.fwk163209334:0,ref=<null>,hashCode=735458497]";

	}

	@Test
	public void getClasspathFromManifestIfAttributeExists() throws Exception {
		Hashtable<String, String> testData = new Hashtable<String, String>();
		String cpEntries = ".,libs/some.jar";
		testData.put(Constants.BUNDLE_CLASSPATH, cpEntries);
		when(bundleMock.getHeaders()).thenReturn(testData);
		Resources res = Resources.somebundleclasspath;
		mockResolverFor(res);
		
		File[] actualEntries = executeResolveWithBundleMock();
		
		File[] expectedFiles = getExpectedFilesFor(cpEntries, res);
		assertThat(Arrays.asList(expectedFiles), hasItems(actualEntries));
	}

	private File[] getExpectedFilesFor(String cpEntries, Resources res)
			throws URISyntaxException {
		String[] expected = cpEntries.split(",");
		File[] expectedFiles = new File[expected.length];
		for (int i = 0 ; i < expected.length; i ++) {
			expectedFiles[i] = new File(res.getFile(), expected[i]);
		}
		return expectedFiles;
	}

	private File[] executeResolveWithBundleMock() {
		return resolver.resolveCPEntriesForBundle(bundleMock);
	}

	private void mockResolverFor(Resources resource) throws Exception {
		when(fileLocator.getBundleFile(Mockito.any(Bundle.class))).thenReturn(
				resource.getFile());
		resolver = new DSLClasspathResolver(fileLocator);
	}

	@Test
	public void getDefaultClasspathIfNoAttributeInManifest() throws Exception {

		when(bundleMock.getHeaders()).thenReturn(
				new Hashtable<String, String>());

		Resources res = Resources.defaultbundleclasspath;
		mockResolverFor(res);
		File[] actualEntries = executeResolveWithBundleMock();

		assertThat(Arrays.asList(actualEntries), hasItems(getExpectedFilesFor(".", res)));
		assertThat(actualEntries.length, equalTo(1));
	}

	@Test
	public void testGetFileLocationIfJar() throws Exception {
		Resources jarfile = Resources.jarfile;
		mockResolverFor(jarfile);

		File[] resolveCPEntriesForBundle = executeResolveWithBundleMock();
		assertEquals("expected only jar file to be a classpath entry", 1,
				resolveCPEntriesForBundle.length);
		assertThat(resolveCPEntriesForBundle[0], equalTo(jarfile.getFile()));
	}

	@Test
	public void testGetFileLocationForUnknownResource() throws Exception {
		mockResolverFor(Resources.unknownformatfile);

		File[] resolveCPEntriesForBundle = executeResolveWithBundleMock();
		assertNull(resolveCPEntriesForBundle);
	}

	private void printVerbose(Object location) {
		String reflectionToString = ToStringBuilder
				.reflectionToString(location);
		System.out.println(reflectionToString);
	}

}
