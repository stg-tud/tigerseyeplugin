package de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast;
import static org.junit.Assert.*;
import static de.tud.stg.tigerseye.eclipse.core.utils.CustomFESTAssertions.*;
import static TransformationUtils.*;

import java.io.ByteArrayOutputStream;

import org.apache.commons.lang.time.StopWatch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast.InvokationDispatcherTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast.KeywordChainingTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.textual.BootStrapTransformation;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarBuilder;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.ATermBuilder;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.PrettyGroovyCodePrinter;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast.resources.SetDSLForInvokationsTransformation;
import de.tud.stg.tigerseye.test.TestDSLTransformation;
import de.tud.stg.tigerseye.test.TransformationUtils;
import de.tud.stg.tigerseye.test.TestDSLTransformation.GrammarResult;

public class InvokationDispatcherTransformationTests {


	private static final Logger logger = LoggerFactory.getLogger(InvokationDispatcherTransformationTests.class);

	def ult = TransformationUtils.getDefaultLookupTable()

	@Before
	public void setUp() throws Exception {
	}


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
		moreCode""";

		def output = performCustomTransformation(clazz, input);
		//		output = new BootStrapTransformation().transform(context, new StringBuffer(output))
		assertThat(output).isEqualToIgnoringWhitespace("""ablock{
			//some comment
		DSLInvoker.eval(
SetDSLForInvokationsTransformation.class,
{zeroArityMethod()})
		}
		moreCode""")
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

	private String performCustomTransformation(Class clazz, String input){

		GrammarResult gr =  newGrammar(clazz)

		IAbstractNode ast = getAST(input, clazz, gr.grammar)

		ATermBuilder aTermBuilder = new ATermBuilder(ast);
		ATerm term = aTermBuilder.getATerm();

		//performs keyword to methods transformation
		term = new KeywordChainingTransformation().transform(gr.moptions, term);

		//encloses DSL method calls into invocations
		term = new InvokationDispatcherTransformation().transform(gr.moptions, term);

		String output = TestDSLTransformation.aTermToString(term, new PrettyGroovyCodePrinter())
	}

	@Test
	public void shouldTransformCombination() {
		def clazz = SetDSLForInvokationsTransformation.class
		String input = builderInput1;

		def output = performCustomTransformation(clazz, input);

		assertThat(output).isEqualToIgnoringWhitespace builderExpected1
	}

	private IAbstractNode getAST(String input, Class clazz, Grammar grammar){

		EarleyParser earleyParser = new EarleyParserConfiguration().getDefaultEarleyParserConfiguration(grammar);

		Chart chart = (Chart) earleyParser.parse(input);

		Context context = new Context("dummyFileName");
		context.addDSL(clazz.getSimpleName(), clazz);

		IAbstractNode ast = chart.getAST();
		return ast
	}

	String builderInput1 = """package de.tud.stg.tigerseye.examples.set
/**
 * Tigerseye language: de.tud.stg.tigerseye.examples.setdsl.SetDSL
 *
 * Declared keywords:
 *  Set asSet(MyList)
 *  Object eval(HashMap, Closure)
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
 *  Object DSLInvoker.eval(
SetDSLForInvokationsTransformation.class,
{eval()})(HashMap, Closure)
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
