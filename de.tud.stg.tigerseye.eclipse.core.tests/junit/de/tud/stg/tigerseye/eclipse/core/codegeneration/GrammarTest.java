package de.tud.stg.tigerseye.eclipse.core.codegeneration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.popart.builder.test.dsls.MathDSL;
import de.tud.stg.popart.dslsupport.DSL;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.MathDSL4GrammarBuilderTest;
import de.tud.stg.tigerseye.test.TransformationUtils;

public class GrammarTest {

	private static final Logger logger = LoggerFactory.getLogger(GrammarTest.class);

	private Class<? extends DSL> classForTest = MathDSL4GrammarBuilderTest.class;
	private IGrammar<String> grammar;

	@Before
	public void setUp() throws Exception {
		grammar = new GrammarBuilder(TransformationUtils.getDefaultLookupTable()).buildGrammar(classForTest);
	}

	@Test
	public void testEqualsHashcode() throws Exception {

		IGrammar<String> grammar2 = new GrammarBuilder(TransformationUtils.getDefaultLookupTable())
				.buildGrammar(classForTest);
		assertEquals(grammar, grammar2);
		assertEquals(grammar.hashCode(), grammar2.hashCode());
		assertNotSame(grammar, grammar2);
		assertSame(grammar, grammar);
		assertEquals(grammar, grammar);
	}

	@Test
	public void testEqualsHashcodeNotEqual() throws Exception {
		IGrammar<String> grammarDifferent = new GrammarBuilder(TransformationUtils.getDefaultLookupTable())
				.buildGrammar(classForTest);
		grammarDifferent.addRule(grammarDifferent.getStartRule());
		assertFalse(grammar.equals(grammarDifferent));
	}

	@Ignore("bad test rests specific numberings")
	@Test
	public void testPopartStateMachineGrammarToString() throws Exception {
		Class<? extends DSL> classForTest = de.tud.stg.popart.builder.test.statemachine.StateMachineDSL.class;
		String expected = loadresource("popartstatemachinedsltostring.expected");
		assertProducedEqualsExpectedGrammar(classForTest, expected);
	}

	private void assertProducedEqualsExpectedGrammar(Class<? extends DSL> classForTest, String expected) {
		IGrammar<String> grammar = newGrammar(classForTest);
		logger.info(grammar.toString());
		String actual = removeMethodOrderSpecificStrings(grammar.toString());
		expected = removeMethodOrderSpecificStrings(expected);
		TransformationUtils.assertContainsAllLines(actual, expected);
	}

	Pattern compile = Pattern.compile("(M\\d+)(?=\\(.*\\))");

	private String removeMethodOrderSpecificStrings(String string) {
		Matcher matcher = compile.matcher(string);
		String replaceAll = matcher.replaceAll("M\\\\d+");

		return replaceAll;
	}

	private IGrammar<String> newGrammar(Class<? extends DSL> classForTest) {
		return TransformationUtils.newGrammar(classForTest).grammar;
	}

	private String loadresource(String name) throws IOException {
		InputStream resourceAsStream = GrammarTest.class.getResourceAsStream("resources/" + name);
		String expected = IOUtils.toString(resourceAsStream);
		return expected;
	}

	@Ignore("bad test. rests on specific numberings")
	@Test
	public void testMathDSLGrammarToSTring() throws Exception {
		assertProducedEqualsExpectedGrammar(MathDSL.class, loadresource("mathdslgrammartostring.expected"));
	}
}
