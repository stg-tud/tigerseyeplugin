package de.tud.stg.tigerseye.examples.statemachine;

import static org.junit.Assert.*;

import java.io.File;
import java.io.InputStream;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import utilities.DSLTransformationTestBase;


import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.core.IRule;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarBuilder;
import de.tud.stg.tigerseye.examples.statemachine.StateMachineDSL;
import de.tud.stg.tigerseye.test.CodePrinterFactory;
import de.tud.stg.tigerseye.test.PrettyGroovyCodePrinterFactory;
import de.tud.stg.tigerseye.test.TestDSLTransformation;
import de.tud.stg.tigerseye.test.TestDSLTransformation.GrammarResult;
import de.tud.stg.tigerseye.test.TestUtils;
import de.tud.stg.tigerseye.test.transformation.utils.DefaultDSLTransformationTester;

public class StateMachineDSLTest extends DSLTransformationTestBase {


	@Test
	public void shouldNotTransformAnything() throws Exception {
		assertTransformedDSLEqualsExpectedUnchecked(
				"StateMachineDSL",
				StateMachineDSL.class);
	}
	
	private String loadResource(String name) throws Exception{
		InputStream resourceAsStream = getClass().getResourceAsStream("resources/"+name);
		return IOUtils.toString(resourceAsStream);
	}
	
//	@Ignore("no correct expected grammar definition available yet")
	@Test
	public void shouldProduceExpectedGrammar() throws Exception {
		Class<StateMachineDSL> reengclass = StateMachineDSL.class;
		GrammarResult reenggrammarbuilder = newGrammar(reengclass);
		String expected = loadResource("statemachinedsltostring.expected");
		String actual = reenggrammarbuilder.grammar.toString();
		System.out.println(actual);
		TestUtils.assertContainsAllLines(expected,actual);
	}
	
	@Test
	public void shouldProduceExpectedOutputForInput() throws Exception {
		GrammarResult newGrammar = newGrammar(StateMachineDSL.class);
		
		DefaultDSLTransformationTester tester = new DefaultDSLTransformationTester(getClass(), DefaultDSLTransformationTester.GENERATED_OUTPUT_FOLDER, "resources");
		
		tester.assertTransformedDSLEqualsExpectedUnchecked("StateMachineDSLSupportsRARRSyntax", StateMachineDSL.class);
	}


	private GrammarResult newGrammar(Class<?> ... classes) {
		
		GrammarBuilder grammarBuilder = new GrammarBuilder(TestUtils.getDefaultLookupTable());
		
		IGrammar<String> buildGrammar = grammarBuilder.buildGrammar(classes);
		
		GrammarResult grammarResult = new GrammarResult(buildGrammar, grammarBuilder.getMethodOptions(), classes);
		
		return grammarResult;
	}

}
