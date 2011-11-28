package de.tud.stg.popart.builder.test.junit;
import static de.tud.stg.tigerseye.eclipse.core.utils.CustomFESTAssertions.*;
import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import org.junit.ComparisonFailure;
import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.LogoDSLForTransformationTest;
import de.tud.stg.tigerseye.eclipse.core.api.Transformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ASTTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast.InvokationDispatcherGroovyTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast.InvokationDispatcherJavaTransformation;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.PrettyGroovyCodePrinter;
import de.tud.stg.tigerseye.test.TestDSLTransformation;

public class LogoDSLTransformationTest {

	@Test
	public void shouldNotTransformWithInvokationIfNoInvokationDispatcherSet() throws Exception {		
		String input = """
forward 10
backward 20"""
		String expected = """forward(10) backward 20"""
		assertInputIsExpected(input, expected)
	}
	
	def transform(String input, ASTTransformation ... ts){
		Class c = LogoDSLForTransformationTest.class;
		TestDSLTransformation.performCustomTransformation(c, input, new PrettyGroovyCodePrinter(), ts)
	}
	
	@Test
	public void shouldTransformGroovyValidlyWithInvokationTransformation() throws Exception {
		String input = """
		forward 10
		backward 20"""
		//still adds parenthesis to backward although method expects an argument
		String expected = """
DSLInvoker.eval(
LogoDSLForTransformationTest.class,
{forward(10)}) 
		DSLInvoker.eval(
LogoDSLForTransformationTest.class,
{backward()}) 20"""
		assertInputIsExpected(input,expected, new InvokationDispatcherGroovyTransformation())
	}
	
	@Test
	public void shouldTransformOnlyAnnotatedMethodAsExpectedNotGroovySyntax() throws Exception {		
		String input = """
		forward 10;
		backward 20;"""
		String expected = """DSLInvoker.getDSL(
LogoDSLForTransformationTest.class).forward(10);
		DSLInvoker.getDSL(
LogoDSLForTransformationTest.class).backward 20;"""
		assertInputIsExpected(input,expected, new InvokationDispatcherJavaTransformation())
	}
	
	@Test
	public void shouldNotTransformMethodWithZeroArityWithParenthesis() throws Exception {
		String input = "methodWithoutParameter"
		String expected = "DSLInvoker.eval(LogoDSLForTransformationTest.class,{methodWithoutParameter()})"
		assertInputIsExpected(input,expected, new InvokationDispatcherGroovyTransformation())
	}
	
	@Test//(expected=ComparisonFailure.class)
	public void shouldNotTransformMethodWithOneArityWithParenthesisAndParameterForSpecialGroovySyntax() throws Exception {
		//public void methodWithOneParameter(int one) {
		String input = "methodWithOneParameter 12"
		String expected = "DSLInvoker.eval(LogoDSLForTransformationTest.class,{methodWithOneParameter()}) 12"
		assertInputIsExpected(input,expected, new InvokationDispatcherGroovyTransformation())
	}
	
	@Test
	public void shouldNotTransformMethodWithOneArityWithParenthesisAndParameter() throws Exception {
		//public void methodWithOneParameter(int one) {
		String input = "methodWithOneParameter(12)"
		String expected = "DSLInvoker.eval(LogoDSLForTransformationTest.class,{methodWithOneParameter()}) (12)"
		assertInputIsExpected(input,expected, new InvokationDispatcherGroovyTransformation())
	}
	
	@Test//(expected=ComparisonFailure.class)
	public void shouldNotTransformMethodWithTwoArityWithParenthesisAndParameterForSpecialGroovySyntax() throws Exception {
//	public void methodWithTowParameter(int one, int two) {
		String input = "methodWithTowParameter 12, 14"
		String expected = "DSLInvoker.eval(LogoDSLForTransformationTest.class,{methodWithTowParameter()}) 12, 14"
		assertInputIsExpected(input,expected, new InvokationDispatcherGroovyTransformation())
	}
	
	@Test
	public void shouldTransformMethodWithTwoArityWithParenthesisAndParameter() throws Exception {
		//	public void methodWithTowParameter(int one, int two) {
				String input = "methodWithTowParameter(12, 14)"
				String expected = "DSLInvoker.eval(LogoDSLForTransformationTest.class,{methodWithTowParameter()}) (12, 14)"
				assertInputIsExpected(input,expected, new InvokationDispatcherGroovyTransformation())
			}
	
	def assertInputIsExpected(String input, String expected, ASTTransformation ... asts){
		def out = transform(input, asts)
		assertThat(out) .isEqualToIgnoringWhitespace expected
	}
	
	
}
