package de.tud.stg.tigerseye.eclipse.core.codegeneration;

import static de.tud.stg.tigerseye.util.Utils.single;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.groovy.eclipse.preferences.GroovyPreferencePage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import de.tud.stg.tigerseye.dslsupport.DSL;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.Context;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast.InvokationDispatcherGroovyTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast.KeywordChainingTransformation;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarBuilder.DSLMethodDescription;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.ATermBuilder;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.CodePrinter;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.PrettyGroovyCodePrinter;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.MathDSL4GrammarBuilderTest;
import de.tud.stg.tigerseye.test.PrettyGroovyCodePrinterFactory;
import de.tud.stg.tigerseye.test.TestDSLTransformation;
import de.tud.stg.tigerseye.test.TransformationUtils;
import de.tud.stg.tigerseye.util.ListBuilder;

public class GrammarBuilderTest {
	
	
	private static final Logger logger = LoggerFactory.getLogger(GrammarBuilderTest.class);

	private GrammarBuilder gb;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		gb = new GrammarBuilder(TransformationUtils.getDefaultLookupTable());
	}

	@Test
	public void testToString() throws Exception {
		Class<MathDSL4GrammarBuilderTest> clazz = MathDSL4GrammarBuilderTest.class;
		
		IGrammar<String> grammar = gb.buildGrammar(clazz);
		String string = grammar.toString();
		
		
		ILexer lexer = new KeywordSensitiveLexer(new KeywordSeperator());
		EarleyParser earleyParser = new EarleyParser(lexer, grammar);

		InputStream loadFile = loadFile("MathDSLShort4GrammarBuilder.input");
		String input = IOUtils.toString(loadFile);
		
		Chart chart = (Chart) earleyParser.parse(input);


		// int cnt = 0;
		// do {
		IAbstractNode ast = chart.getAST();
		
		logger.info(chart.getAST().toString());
		// chart.nextAmbiguity();
		// cnt++;
		// logger.info("= Ambiguity Index = {}",String.valueOf(cnt));

		ATermBuilder aTermBuilder = new ATermBuilder(ast);
		ATerm term = aTermBuilder.getATerm();

		Map<String, DSLMethodDescription> moptions = gb.getMethodOptions();
		
		Context context = new Context("dummy");
		context.setDSLMethodDescriptions(moptions);

		term = new KeywordChainingTransformation().transform(context, term);

//		if (classes.length > 1) {
//			// term = new ClosureResultTransformer().transform(context,
//			// term);
//			term = new InvokationDispatcherTransformation().transform(moptions,
//					term);
//		}
		
		PrettyGroovyCodePrinter prettyPrinter = new PrettyGroovyCodePrinter();
		term.accept(prettyPrinter);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		prettyPrinter.write(out);

		
		logger.info(out.toString());
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
		logger.info(parse.toString());
		logger.info(parse.getAST().toString());
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
		String performTransformation = transformation.performTransformation(input, TransformationUtils.dslSingle(transformer));
		
		logger.info(parse.toString());
		logger.info(parse.getAST().toString());
//		assertEquals(null, expected);
		StringUtils.equalsIgnoringWhitspace(expected, performTransformation);
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
