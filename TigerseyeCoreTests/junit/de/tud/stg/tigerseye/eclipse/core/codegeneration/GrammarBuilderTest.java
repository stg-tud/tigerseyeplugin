package de.tud.stg.tigerseye.eclipse.core.codegeneration;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.popart.builder.test.statemachine.StateMachineDSL;
import de.tud.stg.tigerseye.test.TestUtils;

public class GrammarBuilderTest {

	private GrammarBuilder gb;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		gb = new GrammarBuilder(TestUtils.getDefaultLookupTable());
	}

	@Test
	public void testGrammarBuilder() {
		IGrammar<String> buildGrammar = gb.buildGrammar(StateMachineDSL.class);
	}

//	@Test
//	public void testBuildGrammar() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testGetMethodProduction() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testGetOptions() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testGetPattern() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testGetStartRule() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testGetGrammar() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testGetMethodOptions() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testGetKeywords() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testGetDefaultOptions() {
//		fail("Not yet implemented"); // TODO
//	}

}
