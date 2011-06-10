package de.tud.stg.tigerseye.examples.test.statemachine;

import static org.junit.Assert.*;

import java.io.File;
import java.io.InputStream;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import utilities.DSLTransformationTestBase;

import com.example.fsm.StateMachineDSL;

import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.core.IRule;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarBuilder;
import de.tud.stg.tigerseye.test.CodePrinterFactory;
import de.tud.stg.tigerseye.test.PrettyGroovyCodePrinterFactory;
import de.tud.stg.tigerseye.test.TestDSLTransformation;
import de.tud.stg.tigerseye.test.TestDSLTransformation.GrammarResult;
import de.tud.stg.tigerseye.test.TestUtils;
import de.tud.stg.tigerseye.test.transformation.utils.DefaultDSLTransformationTester;

public class StateMachineDSLTest extends DSLTransformationTestBase {


	//@Ignore("Takes too long about 10 sec and fails currently; currently fails, some keywords are not converted to strings.")
	@Test
	public void shoudlTransformSelectFrom() throws Exception {
		assertTransformedDSLEqualsExpectedUnchecked(
				"StateMachineDSL",
				StateMachineDSL.class);
	}
	
	private String loadResource(String name) throws Exception{
		InputStream resourceAsStream = getClass().getResourceAsStream("resources/"+name);
		return IOUtils.toString(resourceAsStream);
	}
	
	@Test
	public void shouldProduceExpectedGrammar() throws Exception {
		Class<StateMachineDSL> reengclass = StateMachineDSL.class;
		GrammarResult reenggrammarbuilder = newGrammar(reengclass);
		String expected = loadResource("statemachinedsltostring.expected");
		String actual = reenggrammarbuilder.grammar.toString();
		TestUtils.assertContainsAllLines(expected,actual);
	}

//	@Ignore
//	@Test
//	public void shouldProduceExpectedOutput() throws Exception {
//
//		Class<de.tud.stg.tigerseye.transformingstatemachine.StateMachineDSL> reengclass = de.tud.stg.tigerseye.transformingstatemachine.StateMachineDSL.class;
//		GrammarResult reenggrammarbuilder = newGrammar(reengclass);
//		Class<de.tud.stg.popart.builder.test.statemachine.StateMachineDSL> popartclass = de.tud.stg.popart.builder.test.statemachine.StateMachineDSL.class;
//		GrammarResult popartgrammarbuilder = newGrammar(popartclass);
//
//		String testInput = loadResource("StateMachineDSL.input");
//		String reengTransformation = performTransformation(testInput, reenggrammarbuilder);
//		String popartTransformation = performTransformation(testInput, popartgrammarbuilder);
//		
//		TestUtils.assertContainsAllLines(reengTransformation, popartTransformation);
//	}
	
//	@Ignore("need an expected")
	@Test
	public void shouldProduceExpectedOutputForInput() throws Exception {
		GrammarResult newGrammar = newGrammar(StateMachineDSL.class);
		
		DefaultDSLTransformationTester tester = new DefaultDSLTransformationTester(getClass(), DefaultDSLTransformationTester.GENERATED_OUTPUT_FOLDER, "resources");
		
		//TODO add rarr DSLMethod annotations to StateMachineDSL
		tester.assertTransformedDSLEqualsExpectedUnchecked("StateMachineDSLSupportsRARRSyntax", StateMachineDSL.class);
	}

	private String performTransformation(String testInput,
			GrammarResult reenggrammarbuilder) throws Exception {
		return new TestDSLTransformation(new PrettyGroovyCodePrinterFactory()).performTransformation(testInput, reenggrammarbuilder);
	}

	private GrammarResult newGrammar(Class<?> ... classes) {
		
		GrammarBuilder grammarBuilder = new GrammarBuilder(TestUtils.getDefaultLookupTable());
		
		IGrammar<String> buildGrammar = grammarBuilder.buildGrammar(classes);
		
		GrammarResult grammarResult = new GrammarResult(buildGrammar, grammarBuilder.getMethodOptions(), classes);
		
		return grammarResult;
	}

	private void printNotContaindRulesIn(IGrammar<String> doesitcontainAll,
			IGrammar<String> shouldBeconttained) {
		Set<IRule<String>> rules = doesitcontainAll.getRules();
		for (IRule<String> iRule : rules) {
			boolean contains = shouldBeconttained.getRules().contains(iRule);
			if (!contains)
				System.out.println(doesitcontainAll.hashCode()
						+ " does not contain: " + iRule);
		}
	}

}
