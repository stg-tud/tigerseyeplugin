package de.tud.stg.tigerseye.examples.logo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import utilities.DSLTransformationTestBase;
import utilities.StringUtils;

import de.tud.stg.tigerseye.eclipse.core.builder.transformers.textual.KeywordTranslationTransformation;
import de.tud.stg.tigerseye.examples.logo.LogoDSL;
import de.tud.stg.tigerseye.test.PrettyGroovyCodePrinterFactory;
import de.tud.stg.tigerseye.test.TestDSLTransformation;
import de.tud.stg.tigerseye.test.TestDSLTransformation.GrammarResult;
import de.tud.stg.tigerseye.test.TestUtils;

public class LogoDSLTest extends DSLTransformationTestBase {

	@Test
	public void testJapaneseCharacters() throws Exception {
		String name = "logojapanese.input";
		String japLogoInput = loadResourceString(name);
		StringBuilder withTranslationPrefix = new StringBuilder();
		withTranslationPrefix
				.append("@Translation(file=\"")
				.append("/")
				.append(getClass().getPackage().getName()
						.replaceAll("\\.", "/")).append("/resources/")
				.append("translation.jpn\")").append("\n").append(japLogoInput);
		GrammarResult newGrammar = TestUtils.newGrammar(LogoDSL.class);
		//Execution
		//Must perform keyword translation before actual transformation
		KeywordTranslationTransformation ktt = new KeywordTranslationTransformation();
		StringBuffer sb = ktt.transform(newGrammar.generateContext(null),
				new StringBuffer(withTranslationPrefix.toString()));
		//simple transformation
		String output = new TestDSLTransformation(
				new PrettyGroovyCodePrinterFactory()).performTransformation(
				sb.toString(), newGrammar);
		//Verify
		String expected = loadResourceString("logojapanese.expected");
		StringUtils.equalsIgnoringWhitspace(output, expected);
	}

	private String loadResourceString(String name) throws IOException {
		return IOUtils.toString(loadResource(name));
	}

	private InputStreamReader loadResource(String name) {
		InputStream stream = LogoDSLTest.class.getResourceAsStream("resources/"
				+ name);
		return new InputStreamReader(stream);
	}

	/*
	 * 
	 * This test is rather useless since no translation is necessary for the
	 * Logo languages apart from the import of the DSLInvoker and
	 * InterpreterCombiner
	 */
	@Test
	public void testALogoLang() throws Exception {
		assertTransformedDSLEqualsExpected("logo1", LogoDSL.class);
	}

}
