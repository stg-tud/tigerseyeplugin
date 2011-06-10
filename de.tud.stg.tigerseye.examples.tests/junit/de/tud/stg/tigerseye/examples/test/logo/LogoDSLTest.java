package de.tud.stg.tigerseye.examples.test.logo;

import org.junit.Test;

import utilities.DSLTransformationTestBase;

import de.tud.stg.tigerseye.examples.logo.LogoDSL;

public class LogoDSLTest extends DSLTransformationTestBase {

	
//	@Ignore("needs additional translation loading setup")
	@Test
	public void testJapaneseCharacters() throws Exception {
		assertTransformedDSLEqualsExpectedUnchecked("logojapanese", LogoDSL.class);
	}
	
	/*
	 * 
	 * This test is rather useless since no translation is necessary for the Logo languages
	 * apart from the import of the DSLInvoker and InterpreterCombiner 
	 */ 
	@Test
	public void testALogoLang() throws Exception {
		assertTransformedDSLEqualsExpectedUnchecked("logo1", LogoDSL.class);		
	}
	
}
