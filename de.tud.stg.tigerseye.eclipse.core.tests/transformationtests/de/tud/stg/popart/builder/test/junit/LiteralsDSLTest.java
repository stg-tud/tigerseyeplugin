package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.tigerseye.eclipse.core.utils.CustomFESTAssertions.assertThat;

import java.io.FileNotFoundException;

import jjtraveler.VisitFailure;

import org.junit.ComparisonFailure;
import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.LiteralsDSL;
import de.tud.stg.tigerseye.test.TestDSLTransformation;

/**
 * Showcases some ambiguity problems
 * 
 * @author Leo_Roos
 *
 */
public class LiteralsDSLTest {

	@Test(expected = ComparisonFailure.class)
	public void shouldProcessBothLiteralsWithAmbiguityProblems() throws Exception {
		String[] inpExp = { "black\ngreen", "getBlack()\nmoreGreen()" };
		assertExpectedForInputTransformation(inpExp);
	}

	private void assertExpectedForInputTransformation(String[] inputExpectePair) throws FileNotFoundException,
			VisitFailure {
		TestDSLTransformation tdt = new TestDSLTransformation();
		String performTransformation = tdt.performTransformation(inputExpectePair[0], LiteralsDSL.class);
		assertThat(performTransformation).isEqualToIgnoringWhitespace(inputExpectePair[1]);
	}

	@Test
	public void shouldProcessNotAnnotatedBlackAsLiteral() throws Exception {
		String[] inpExp2 = { "black", "getBlack()" };
		assertExpectedForInputTransformation(inpExp2);
	}

	@Test(expected=ComparisonFailure.class)
	public void shouldProcessAnnotatedLiteralFunctionAsLiteral() throws Exception {
		String[] inpExp3 = { "green", "moreGreen()" };
		assertExpectedForInputTransformation(inpExp3);
	}

	@Test
	public void shouldProcessAnnotatedFunctionWithGetAsLiteralWithoutGet() throws Exception {
		String[] inpExp4 = { "annotatedLiteral", "getAnnotatedLiteral()" };
		assertExpectedForInputTransformation(inpExp4);
	}

	@Test
	public void shouldProcessOperationFunctionWithGetOpWithGet() throws Exception {
		String[] inpExp5 = { "getAnnotatedOperation", "getAnnotatedOperation()" };
		assertExpectedForInputTransformation(inpExp5);
	}

	@Test(expected=ComparisonFailure.class)
	public void shouldProcessAnnotatedOperationFunctionWithoutParameter_AmbiguityProb() throws Exception {
		// @DSLMethod(production="white") def moreWhite(){
		String[] inpExp6 = { "white", "moreWhite()" };
		assertExpectedForInputTransformation(inpExp6);
	}
	
	@Test
	public void shouldProcessAnnotatedOperationFunctionWithoutParameter_UseOfSemicolon() throws Exception {
		// @DSLMethod(production="white") def moreWhite(){
		String[] inpExp6 = { "white;", "moreWhite;" };
		assertExpectedForInputTransformation(inpExp6);
	}

	@Test
	public void shouldProcessOperationWithParameterSanityCheck() throws Exception {
		// @DSLMethod(production="para__p0")def moreParameter(int i){
		String[] inpExp = { "¶ 5;", "moreParameter(5);" };
		assertExpectedForInputTransformation(inpExp);
	}

	@Test
	public void shouldProcessOperationWithArrayAndSpecialChar() throws Exception {
		// def sum__p0(int[] inp){
		String[] inpExp = { "∑ 10, 10;", "sum__p0([10, 10] as int[]);" };
		assertExpectedForInputTransformation(inpExp);
	}

	@Test
	public void shouldProcessOperationWithTwoInputsWithoutAmbiguityProblemsIfUseOfSemicolon() throws Exception {
		// @DSLMethod(production="sum2__p0__p1")
		// def summes2(int i , int i2){
		String[] inpExp = { "sum2 10 10;", "summes2(10,10);" };
		assertExpectedForInputTransformation(inpExp);
	}

	@Test(expected = ComparisonFailure.class)
	public void shouldProcessOperationWithTwoInputsWithAmbiguityProblems() throws Exception {
		// @DSLMethod(production="sum2__p0__p1")
		// def summes2(int i , int i2){
		String[] inpExp = { "sum2 10 10", "summes2(10,10);" };
		assertExpectedForInputTransformation(inpExp);
		// if here it was unexpectedly successful
	}
}
