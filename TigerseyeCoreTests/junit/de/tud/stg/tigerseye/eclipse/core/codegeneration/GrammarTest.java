package de.tud.stg.tigerseye.eclipse.core.codegeneration;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.popart.builder.test.dsls.MathDSL;
import de.tud.stg.popart.dslsupport.DSL;
import de.tud.stg.tigerseye.test.TestUtils;

public class GrammarTest {

	private Class<? extends DSL> classForTest = de.tud.stg.popart.builder.test.statemachine.StateMachineDSL.class;
	private IGrammar<String> grammar;
	
	@Before
	public void setUp() throws Exception {
		grammar = new GrammarBuilder(TestUtils.getDefaultLookupTable()).buildGrammar(classForTest);
	}
	
	@Test
	public void testEqualsHashcode() throws Exception {
		
		IGrammar<String> grammar2 = new GrammarBuilder(TestUtils.getDefaultLookupTable()).buildGrammar(classForTest);
		assertEquals(grammar, grammar2);
		assertEquals(grammar.hashCode(),grammar2.hashCode());
		assertNotSame(grammar, grammar2);
		assertSame(grammar, grammar);
		assertEquals(grammar,grammar);
	}
	
	@Test
	public void testEqualsHashcodeNotEqual() throws Exception {
		IGrammar<String> grammarDifferent = new GrammarBuilder(TestUtils.getDefaultLookupTable()).buildGrammar(classForTest);
		grammarDifferent.addRule(grammarDifferent.getStartRule());
		assertFalse(grammar.equals(grammarDifferent));
	}
	
	@Test
	public void testPopartStateMachineGrammarToString() throws Exception {
		Class<? extends DSL> classForTest = de.tud.stg.popart.builder.test.statemachine.StateMachineDSL.class;
		String expected = loadresource("popartstatemachinedsltostring.expected");
		assertProducedEqualsExpectedGrammar(classForTest, expected);		
	}

	private void assertProducedEqualsExpectedGrammar(
			Class<? extends DSL> classForTest, String expected) {
		IGrammar<String> grammar = newGrammar(classForTest);
		TestUtils.assertContainsAllLines(grammar.toString(), expected);
	}

	private IGrammar<String> newGrammar(Class<? extends DSL> classForTest) {
		return new GrammarBuilder(TestUtils.getDefaultLookupTable()).buildGrammar(classForTest);
	}

	private String loadresource(String name) throws IOException {
		InputStream resourceAsStream = GrammarTest.class.getResourceAsStream("resources/" + name);
		String expected = IOUtils.toString(resourceAsStream);
		return expected;
	}
	
	@Test
	public void testMathDSLGrammarToSTring() throws Exception {
		assertProducedEqualsExpectedGrammar(MathDSL.class, loadresource("mathdslgrammartostring.expected"));
	}
}
