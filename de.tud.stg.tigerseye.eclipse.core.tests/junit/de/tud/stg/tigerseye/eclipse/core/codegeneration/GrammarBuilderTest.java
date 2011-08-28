package de.tud.stg.tigerseye.eclipse.core.codegeneration;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.groovy.eclipse.preferences.GroovyPreferencePage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.StringUtils;

import aterm.ATerm;

import de.tud.stg.parlex.ast.IAbstractNode;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.lexer.ILexer;
import de.tud.stg.parlex.lexer.KeywordSensitiveLexer;
import de.tud.stg.parlex.lexer.KeywordSeperator;
import de.tud.stg.parlex.parser.IChart;
import de.tud.stg.parlex.parser.earley.Chart;
import de.tud.stg.parlex.parser.earley.EarleyParser;
import de.tud.stg.popart.builder.test.dsls.MathDSL;
import de.tud.stg.popart.builder.test.statemachine.StateMachineDSL;
import de.tud.stg.popart.dslsupport.DSL;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.Context;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast.InvokationDispatcherTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast.KeywordChainingTransformation;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarBuilder.MethodOptions;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.ATermBuilder;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.CodePrinter;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.PrettyGroovyCodePrinter;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.MathDSL4GrammarBuilderTest;
import de.tud.stg.tigerseye.test.PrettyGroovyCodePrinterFactory;
import de.tud.stg.tigerseye.test.TestDSLTransformation;
import de.tud.stg.tigerseye.test.TestUtils;
import de.tud.stg.tigerseye.util.ListBuilder;

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
	public void testToString() throws Exception {
		Class[] classes =  { MathDSL4GrammarBuilderTest.class };
		IGrammar<String> grammar = gb.buildGrammar(classes);
		String string = grammar.toString();
		
		
		ILexer lexer = new KeywordSensitiveLexer(new KeywordSeperator());
		EarleyParser earleyParser = new EarleyParser(lexer, grammar);

		InputStream loadFile = loadFile("MathDSLShort4GrammarBuilder.input");
		String input = IOUtils.toString(loadFile);
		
		Chart chart = (Chart) earleyParser.parse(input);


		// int cnt = 0;
		// do {
		IAbstractNode ast = chart.getAST();
		
		System.out.println(chart.getAST());
		// chart.nextAmbiguity();
		// cnt++;
		// logger.info("= Ambiguity Index = {}",String.valueOf(cnt));

		ATermBuilder aTermBuilder = new ATermBuilder(ast);
		ATerm term = aTermBuilder.getATerm();

		Map<String, MethodOptions> moptions = gb.getMethodOptions();

		term = new KeywordChainingTransformation().transform(moptions, term);

		if (classes.length > 1) {
			// term = new ClosureResultTransformer().transform(context,
			// term);
			term = new InvokationDispatcherTransformation().transform(moptions,
					term);
		}
		PrettyGroovyCodePrinter prettyPrinter = new PrettyGroovyCodePrinter();
		term.accept(prettyPrinter);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		prettyPrinter.write(out);

		
		System.out.println(out);
//		return new String(out.toByteArray());
	}
	
	private InputStream loadFile(String name){
		InputStream resourceAsStream = GrammarBuilderTest.class.getResourceAsStream("resources/" + name);
		return resourceAsStream;
	}
	
	@Test
	public void testSpecificNotWorkingTransformation() throws Exception {
		String input = "int x = 10;\n" + 
				"	∑ x, 10";
		String expected = "int x = 10 ;\n" + 
				" 	sum__p0(\n" + 
				"		[x,\n" + 
				"		10] as int[])";
		Class<MathDSL> transformer = MathDSL.class;
		IGrammar<String> buildGrammar = gb.buildGrammar(transformer);
		EarleyParser ep = new EarleyParser(buildGrammar);
		IChart parse = ep.parse(input);
		System.out.println(parse);
		System.out.println(parse.getAST());
	}
	
	@Test
	public void testSpecificWorkingTransformation() throws Exception {
		String input = "∑ 10, 10 ;";//TODO fails without semicolon
		String expected = "sum__p0(	[10, 10] as int[]);";
		Class<? extends DSL> transformer = MathDSL.class;
		IGrammar<String> buildGrammar = gb.buildGrammar(transformer);
		EarleyParser ep = new EarleyParser(buildGrammar);
		IChart parse = ep.parse(input);
		
		TestDSLTransformation transformation = new TestDSLTransformation(new PrettyGroovyCodePrinterFactory());
		String performTransformation = transformation.performTransformation(input, TestUtils.dslSingle(transformer));
		
		System.out.println(parse);
		System.out.println(parse.getAST());
//		assertEquals(null, expected);
		StringUtils.equalsIgnoringWhitspace(performTransformation, expected);
	}
	
	
	/*
	 * Extracting Grammars using Reflection
	 */
	
	
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
