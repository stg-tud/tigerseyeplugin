package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.tigerseye.test.TransformationUtils.test;
import static org.junit.Assert.fail;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import utilities.LongrunningTest;
import utilities.SystemPropertyRule;

import de.tud.stg.popart.builder.test.dsls.BnfDSL;
import de.tud.stg.tigerseye.test.TestDSLTransformation;
import de.tud.stg.tigerseye.test.TransformationUtils;

public class TestBnfDSL {

	@Rule
	public SystemPropertyRule ptr = new SystemPropertyRule();

	@Test
	@LongrunningTest(24081)
	public void testBnfDSL() {
		test(false, "BnfDSLTest", TransformationUtils.dslSingle(BnfDSL.class));
	}
	
	private void assertInputExpected(String input, String expectedTransformation) throws Exception{
		TransformationUtils.assertExpectedForInputTransformation(input, expectedTransformation, BnfDSL.class);
	}
	
	
	@Test
	public void shouldTransformsSimpleExample1() throws Exception {
		assertInputExpected("math ::= { expression }", "syntax(\n" + 
				"[\n" + 
				"rule(\n" + 
				"math,\n" + 
				"expression(\n" + 
				"[\n" + 
				"termFromFactors(\n" + 
				"[\n" + 
				"factorFromExpressionInBraces(\n" + 
				"expression)\n" + 
				"] as Factor[])\n" + 
				"] as Term[]))\n" + 
				"] as Rule[])");
	}
	
	@Test
	public void shouldTransformsSimpleExample2() throws Exception {
		assertInputExpected("expression	::= plus | minus","syntax(\n" + 
				"[\n" + 
				"rule(\n" + 
				"expression,\n" + 
				"expression(\n" + 
				"[\n" + 
				"plus,\n" + 
				"minus\n" + 
				"] as Term[]))\n" + 
				"] as Rule[])");
	}
	
//	@Test
	public void shouldTransformsSimpleExample3() throws Exception {
		assertInputExpected("plus		::= number "+" number", "");
	}
	
//	@Test
	public void shouldTransformsSimpleExample4() throws Exception {
		assertInputExpected("minus		::= number \"-\" number", "");
	}
	
//	@Test
	public void shouldTransformsSimpleExample5() throws Exception {
		assertInputExpected("number		::= digit { digit }", "");
	}
	
	@Test
	public void shouldTransformsSimpleExampleIdentifier() throws Exception {
		assertInputExpected("identifier ::= expression", "syntax(\n" + 
				"[\n" + 
				"rule(\n" + 
				"identifier,\n" + 
				"expression)\n" + 
				"] as Rule[])");
	}
	
	@Test
	public void shouldTransformsSimpleExampleAssignment() throws Exception {
		assertInputExpected("a ::= b", "syntax(\n" + 
				"[\n" + 
				"rule(\n" + 
				"a,\n" + 
				"b)\n" + 
				"] as Rule[])");
	}
	
	
}
