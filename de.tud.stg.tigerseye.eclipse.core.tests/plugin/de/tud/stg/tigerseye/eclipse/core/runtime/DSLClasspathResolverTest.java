package de.tud.stg.tigerseye.eclipse.core.runtime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.sound.midi.SysexMessage;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utilities.PluginTest;
import utilities.PluginTestRule;

import de.tud.stg.tigerseye.eclipse.TigerseyeLibraryProvider;
import de.tud.stg.tigerseye.test.TransformationUtils;


/**
 * Run as plug-in tests
 * 
 * @author Leo Roos
 *
 */
public class DSLClasspathResolverTest {
	
	@Rule
	public PluginTestRule ptr = new PluginTestRule();

	
	@Mock(answer = Answers.RETURNS_SMART_NULLS)
	private Bundle bundleMock;
	@Mock
	private FileLocatorWrapper fileLocator;

	private BundleClasspathResolver resolver;

	@Before
	public void setUp() throws Exception {
//		assertPlatformRunning();
		MockitoAnnotations.initMocks(this);
		resolver = new BundleClasspathResolver();

	}

	@Test
	@PluginTest
	public void testReadManifestFromFolderSmokeTest() throws Exception {		
		String aBundleIdentifier = "org.eclipse.core.runtime";
		String tigerBundle = TigerseyeLibraryProvider.PLUGIN_ID;
		Bundle eclipseCoreBundle = Platform.getBundle(aBundleIdentifier);
		printVerbose(eclipseCoreBundle);
		Bundle tb = Platform.getBundle(tigerBundle);

		@SuppressWarnings("unchecked")
		Dictionary<String, String> headers = (Dictionary<String, String>) tb
				.getHeaders();
		String tigerCP = headers.get(Constants.BUNDLE_CLASSPATH);
		assertFalse(tigerCP.trim().isEmpty());

		URL find = FileLocator.find(eclipseCoreBundle, new Path("/"), null);
		assertEquals("bundleentry", find.getProtocol());
		
		File bundleFile = FileLocator.getBundleFile(tb);
		assertTrue(bundleFile.isDirectory());

	}

	private void warnTestIgnored() {
		String ignoredMethod = "_UNKNOWN_METHOD_NAME_";
		Exception e = new Exception();
		StackTraceElement[] stackTrace = e.getStackTrace();
		if(stackTrace.length > 1){
			StackTraceElement callingMethod = stackTrace[1];
			ignoredMethod = callingMethod.toString();
		}
		System.err.println("IGNORING test method:" + ignoredMethod);
	}

	@Test
	@PluginTest
	public void getClasspathFromManifestIfAttributeExists() throws Exception {

		Hashtable<String, String> testData = new Hashtable<String, String>();
		String cpEntries = ".,libs/some.jar";
		testData.put(Constants.BUNDLE_CLASSPATH, cpEntries);
		when(bundleMock.getHeaders()).thenReturn(testData);
		Resources res = Resources.somebundleclasspath;
		mockResolverForBundleResource(res);
		
		File[] actualEntries = executeResolveWithBundleMock();
		
		File[] expectedFiles = getExpectedFilesFor(cpEntries, res);
		assertThat(Arrays.asList(expectedFiles), hasItems(actualEntries));
	}

	private File[] getExpectedFilesFor(String cpEntries, Resources res)
			throws Exception {
		File resFile = res.getFileInPluginRun();
		String[] expected = cpEntries.split(",");		
		return TransformationUtils.getFilesRelativeToRoot(resFile, expected);
	}

	private File[] executeResolveWithBundleMock() {
		return resolver.resolveCPEntriesForBundle(bundleMock);
	}

	private void mockResolverForBundleResource(Resources resource) throws Exception {
		when(fileLocator.getBundleFile(Mockito.any(Bundle.class))).thenReturn(
				resource.getFileInPluginRun());
		resolver = new BundleClasspathResolver(fileLocator);
	}

	@Test
	@PluginTest
	public void getDefaultClasspathIfNoAttributeInManifest() throws Exception {
		when(bundleMock.getHeaders()).thenReturn(
				new Hashtable<String, String>());

		Resources res = Resources.defaultbundleclasspath;
		mockResolverForBundleResource(res);
		File[] actualEntries = executeResolveWithBundleMock();

		assertThat(Arrays.asList(actualEntries), hasItems(getExpectedFilesFor(".", res)));
		assertThat(actualEntries.length, equalTo(1));
	}

	@Test
	@PluginTest
	public void testGetFileLocationIfJar() throws Exception {

		Resources jarfile = Resources.jarfile;
		mockResolverForBundleResource(jarfile);

		File[] resolveCPEntriesForBundle = executeResolveWithBundleMock();
		assertEquals("expected only jar file to be a classpath entry", 1,
				resolveCPEntriesForBundle.length);
		assertThat(resolveCPEntriesForBundle[0], equalTo(jarfile.getFileInPluginRun()));
	}
	
	@Test
	@PluginTest
	public void testGetFileLocationForUnknownResource() throws Exception {
		mockResolverForBundleResource(Resources.unknownformatfile);

		File[] resolveCPEntriesForBundle = executeResolveWithBundleMock();
		assertNull(resolveCPEntriesForBundle);
	}
	
	@Test
	@PluginTest
	public void testBundleNotAccessible() throws Exception {
		when(fileLocator.getBundleFile(Mockito.any(Bundle.class))).thenThrow(new IOException());
		resolver = new BundleClasspathResolver(fileLocator);		
		
		File[] executeResolveWithBundleMock = executeResolveWithBundleMock();
		
		assertNull("exepected result to be null since file could not be returned",executeResolveWithBundleMock);
	}

	private void printVerbose(Object location) {
		String reflectionToString = ToStringBuilder
				.reflectionToString(location);
		System.out.println(reflectionToString);
	}

}
