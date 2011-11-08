package learningtests.fileaccess;

import static de.tud.stg.tigerseye.eclipse.core.utils.CustomFESTAssertions.assertThat;
import static org.fest.assertions.Assertions.assertThat;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.UnhandledException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.junit.Rule;
import org.junit.Test;
import org.osgi.framework.Bundle;

import utilities.PluginTest;
import utilities.PluginTestRule;

/**
 * This source will only compile with UTF-8 encoding
 * 
 * @author Leo_Roos
 * 
 */
public class ReadingEncodingdependendUnicode {

	@Rule
	public PluginTestRule ptr = new PluginTestRule();

	private static final String LOGOJAPANESE_INPUT_RESOURCE_NAME = "logojapanese.input";

	private InputStream getResourceInputStream(String name) {
		InputStream resourceAsStream = ReadingEncodingdependendUnicode.class.getResourceAsStream("resources/" + name);
		assertThat(resourceAsStream).isNotNull();
		return resourceAsStream;
	}

	private String getResource(String name, String charset) {
		InputStream resourceInputStream = getResourceInputStream(name);
		try {
			InputStreamReader reader = new InputStreamReader(resourceInputStream, charset);
			return IOUtils.toString(reader);
		} catch (Exception e) {
			throw new UnhandledException("", e);
		}
	}

	private static String expectedLogoJapaneseInput = "	オンワード 90\n" //
			+ "	ひだり 90\n" //
			+ "	オンワード 90\n" //
			+ "	ひだり 90\n" //
			+ "	オンワード 90\n"//
			+ "	ひだり 90\n" //
			+ "	オンワード 90\n"//
			+ "	ひだり 90";

	@Test
	public void shouldBeCorrectForUTF8() throws Exception {
		assertThat(getResource(LOGOJAPANESE_INPUT_RESOURCE_NAME, "UTF-8")).isEqualTo(expectedLogoJapaneseInput);
	}

	@Test
	public void shouldnotbeequalforUTF16() throws Exception {
		assertThat(getResource(LOGOJAPANESE_INPUT_RESOURCE_NAME, CharEncoding.UTF_16)).isNotEqualTo(
				expectedLogoJapaneseInput);
	}

	@Test
	public void shouldnotbeequalforUTF16BE() throws Exception {
		assertThat(getResource(LOGOJAPANESE_INPUT_RESOURCE_NAME, CharEncoding.UTF_16BE)).isNotEqualTo(
				expectedLogoJapaneseInput);
	}

	@Test
	public void shouldnotbeequalforISO88591() throws Exception {
		assertThat(getResource(LOGOJAPANESE_INPUT_RESOURCE_NAME, CharEncoding.ISO_8859_1)).isNotEqualTo(
				expectedLogoJapaneseInput);
	}

}
