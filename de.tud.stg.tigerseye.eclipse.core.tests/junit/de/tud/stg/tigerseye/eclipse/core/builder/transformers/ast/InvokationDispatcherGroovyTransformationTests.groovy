package de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast;
import static org.junit.Assert.*;
import static de.tud.stg.tigerseye.eclipse.core.utils.CustomFESTAssertions.*;
import static TransformationUtils.*;

import java.io.ByteArrayOutputStream;

import org.apache.commons.lang.time.StopWatch;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utilities.LongrunningTest;
import utilities.SystemPropertyRule;
import utilities.TestUtils;

import aterm.ATerm;

import de.tud.stg.parlex.ast.IAbstractNode;
import de.tud.stg.parlex.core.Grammar;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.parser.earley.Chart;
import de.tud.stg.parlex.parser.earley.EarleyParser;
import de.tud.stg.popart.builder.test.dsls.SetDSL;
import de.tud.stg.tigerseye.dslsupport.DSL;
import de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler.EarleyParserConfiguration;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.Context;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast.InvokationDispatcherGroovyTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast.KeywordChainingTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.textual.BootStrapTransformation;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarBuilder;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.ATermBuilder;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.PrettyGroovyCodePrinter;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast.resources.RuleDSLInvokationTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast.resources.SetDSLForInvokationsTransformation;
import de.tud.stg.tigerseye.test.TestDSLTransformation;
import de.tud.stg.tigerseye.test.TransformationUtils;
import de.tud.stg.tigerseye.test.TestDSLTransformation.GrammarResult;

public class InvokationDispatcherGroovyTransformationTests {


	private static final Logger logger = LoggerFactory.getLogger(InvokationDispatcherGroovyTransformationTests.class);

	@Rule
	public SystemPropertyRule sysproprule = new SystemPropertyRule();
	
	

	@Before
	public void setUp() throws Exception {
	}


	@LongrunningTest(3976)
	@Test
	public void shouldDSLInvokeOneArityMethod() throws Exception {
		def clazz = SetDSLForInvokationsTransformation.class
		String input = """
		ablock{
			//some comment
		oneArity "input"
		}
		moreCode""";

		def output = performCustomTransformation(clazz, input);
		assertThat(output).isEqualToIgnoringWhitespace("""		ablock{
			//some comment
		DSLInvoker.eval(
SetDSLForInvokationsTransformation.class,
{oneArityMethod(
"input")})
		}
		moreCode""")
	}

	@Test
	public void shouldDSLInvokeZeroArityMethod() throws Exception {
		def clazz = SetDSLForInvokationsTransformation.class
		String input = """
		ablock{
			//some comment
		Get the Cake
		}
		moreCode"""

		def output = performCustomTransformation(clazz, input);
		assertThat(output).isEqualToIgnoringWhitespace """ablock{
			//some comment
		DSLInvoker.eval(
SetDSLForInvokationsTransformation.class,
{zeroArityMethod()})
		}
		moreCode"""
	}

	@Test
	public void shouldDSLInvokePlainZeroArityMethod() throws Exception {
		def clazz = SetDSLForInvokationsTransformation.class
		String input = """
		ablock{
			//some comment
		plainZeroArity
		}
		moreCode""";

		def output = performCustomTransformation(clazz, input);
		//		output = new BootStrapTransformation().transform(context, new StringBuffer(output))

		assertThat(output).isEqualToIgnoringWhitespace """ablock{
			//some comment
		DSLInvoker.eval(
SetDSLForInvokationsTransformation.class,
{plainZeroArity()})
		}
moreCode"""
	}




	@LongrunningTest(7674)
	@Test
	public void shouldTransformCombination() {
		def clazz = SetDSLForInvokationsTransformation.class
		String input = builderInput1;

		def output = performCustomTransformation(clazz, input);

		assertThat(output).isEqualToIgnoringWhitespace builderExpected1
	}
	
	@Test
	public void shouldLiteralWithDSLInvoke() {
		Class clazz = RuleDSLInvokationTransformation.class
		String input = """resultingPolicy"""
		String expected = 
"""DSLInvoker.eval(RuleDSLInvokationTransformation.class,{
createPolicyEmergent()})"""
		
		def output = performCustomTransformation(clazz, input);

		assertThat(output).isEqualToIgnoringWhitespace expected
	}
	
	@Test
	public void shouldDSLInvokeZeroArityMethodRuleDSL() {
		Class clazz = RuleDSLInvokationTransformation.class
		String input = """get the policy"""
		String expected =
"""DSLInvoker.eval(RuleDSLInvokationTransformation.class,{
createZeroArityMethod()})"""
		
		def output = performCustomTransformation(clazz, input);

		assertThat(output).isEqualToIgnoringWhitespace expected
	}
	
	private performCustomTransformation(Class<? extends DSL> clazz, String input){
		return TestDSLTransformation.performCustomTransformation(clazz, input,new PrettyGroovyCodePrinter(), new InvokationDispatcherGroovyTransformation())
	}
	

	@Test
	public void shouldNotTransformWithRedundantCloures() throws Exception {
		String input =
"""Policy (Id = "Service-Levels") {
	Rule {
		Trust = "High",
		Location= "Germany",
		Options = { "Pre-Paid", "Credit-Card", "Invoice" }
	}
	Rule {
		Trust = "Low",
		Options = { "Pre-Paid" }
	}
}
def p = resultingPolicy"""
String expected = """
DSLInvoker.eval(RuleDSLInvokationTransformation.class,{
	aPolicyWithId("Service-Levels",{
		DSLInvoker.eval(RuleDSLInvokationTransformation.class,{
			aRule([
				DSLInvoker.eval(RuleDSLInvokationTransformation.class,{
					aKeyValue(Trust,["High"] as String[])}),
				DSLInvoker.eval(RuleDSLInvokationTransformation.class,{
					aKeyValue(Location,["Germany"] as String[])}),
				DSLInvoker.eval(RuleDSLInvokationTransformation.class,{
					entryOptions(["Pre-Paid","Credit-Card","Invoice"] as String[])})
				] as Entry[])})
	 	DSLInvoker.eval(RuleDSLInvokationTransformation.class,{
			aRule([
				DSLInvoker.eval(RuleDSLInvokationTransformation.class,{
					aKeyValue(Trust,["Low"] as String[])}),
				DSLInvoker.eval(RuleDSLInvokationTransformation.class,{
						entryOptions(["Pre-Paid"] as String[])})] as Entry[])})})})
def p =
	DSLInvoker.eval(RuleDSLInvokationTransformation.class,{
		createPolicyEmergent()})
"""
		def out = performCustomTransformation(RuleDSLInvokationTransformation.class, input)
		
		assertThat(out).isEqualToIgnoringWhitespace(expected)	
	}

	String builderInput1 = """package de.tud.stg.tigerseye.examples.set
/**
 * Tigerseye language: de.tud.stg.tigerseye.examples.setdsl.SetDSL
 *
 * Declared keywords:
 *  Set asSet(MyList)
 *  Set intersection(Set, Set)
 *  Set union(Set, Set)
 *  
 *  	
 *  
 */

set(name:'SetTest'){

	/* Translation with a special character like ⋃ only seems to work for one application,
	 * when a second operation uses another special character it is not always translated correctly. 
	 */  
	
	Set b = { "2"} ⋂ { "6", "8", "2"}
	Set bres = { "2" }
	Get the Cake
	println b
	println bres
	
}"""

	String builderExpected1 = """package de.tud.stg.tigerseye.examples.set
/**
 * Tigerseye language: de.tud.stg.tigerseye.examples.setdsl.SetDSL
 *
 * Declared keywords:
 *  Set asSet(MyList)
 *  Set intersection(Set, Set)
 *  Set union(Set, Set)
 *  
 *  	
 *  
 */

set(name:'SetTest'){

	/* Translation with a special character DSLInvoker.eval(
SetDSLForInvokationsTransformation.class,
{union(
like,
only)}) seems to work for one application,
	 * when a second operation uses another special character it is not always translated correctly. 
	 */  
	
	Set b = DSLInvoker.eval(
SetDSLForInvokationsTransformation.class,
{intersection(
DSLInvoker.eval(
SetDSLForInvokationsTransformation.class,
{asSet(
DSLInvoker.eval(
SetDSLForInvokationsTransformation.class,
{singleElementedList(
"2")}))}),
DSLInvoker.eval(
SetDSLForInvokationsTransformation.class,
{asSet(
DSLInvoker.eval(
SetDSLForInvokationsTransformation.class,
{multiElementedList(
"6",
DSLInvoker.eval(
SetDSLForInvokationsTransformation.class,
{multiElementedList(
"8",
DSLInvoker.eval(
SetDSLForInvokationsTransformation.class,
{singleElementedList(
"2")}))}))}))}))})
	Set bres = DSLInvoker.eval(
SetDSLForInvokationsTransformation.class,
{asSet(
DSLInvoker.eval(
SetDSLForInvokationsTransformation.class,
{singleElementedList(
"2")}))})
	DSLInvoker.eval(
SetDSLForInvokationsTransformation.class,
{zeroArityMethod()})
	println b
	println bres
	
}"""
}
