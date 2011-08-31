package de.tud.stg.popart.builder.test.junit;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;

import jjtraveler.VisitFailure;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.UnhandledException;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aterm.ATerm;
import de.tud.stg.parlex.ast.IAbstractNode;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.parser.earley.Chart;
import de.tud.stg.parlex.parser.earley.EarleyParser;
import de.tud.stg.popart.builder.test.dsls.WordMachine;
import de.tud.stg.popart.builder.test.statemachine.fsm.EventProducer;
import de.tud.stg.popart.builder.test.statemachine.fsm.StateMachine;
import de.tud.stg.popart.builder.utils.DSLInvoker;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.Context;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast.KeywordChainingTransformation;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarBuilder;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.UnicodeLookupTable;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.ATermBuilder;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.PrettyGroovyCodePrinter;
import de.tud.stg.tigerseye.test.GroovyScript;
import de.tud.stg.tigerseye.test.TransformationUtils;

public class TestWordMachine {
private static final Logger logger = LoggerFactory.getLogger(TestWordMachine.class);


	public String testWordMaschine(InputStreamReader inputFile) {
		try {

			UnicodeLookupTable ult = TransformationUtils.getDefaultLookupTable();
			
			GrammarBuilder gb = new GrammarBuilder(ult);

			IGrammar<String> grammar = gb.buildGrammar(WordMachine.class);

			EarleyParser earleyParser = new EarleyParser(null, grammar);

			String sb = IOUtils.toString(inputFile);

			Chart chart = (Chart) earleyParser.parse(sb.trim());

			Context context = new Context("TestWordMachine");
			context.addDSL(WordMachine.class.getSimpleName(), WordMachine.class);

			IAbstractNode ast;
			ATermBuilder aTermBuilder;
			ATerm term;
			ast = chart.getAST();
			chart.nextAmbiguity();

			aTermBuilder = new ATermBuilder(ast);
			term = aTermBuilder.getATerm();

			term = new KeywordChainingTransformation().transform(gb.getMethodOptions(), term);

			PrettyGroovyCodePrinter prettyPrinter = new PrettyGroovyCodePrinter();

			term.accept(prettyPrinter);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			prettyPrinter.write(out);			
			return new String(out.toByteArray());
		} catch (FileNotFoundException e) {
			throw new UnhandledException(e);
		} catch (IOException e) {
			throw new UnhandledException(e);
		} catch (VisitFailure e) {
			throw new UnhandledException(e);
		}
	}

	@Ignore("Takes very long and fails, did the implementation change without adjusting the test?")
	@Test	
	public void testWordMachine() {

		String inputStateMachine = "WordMaschine_short.input";//args[0];
//		String inputStateMachine = "WordMaschine_short.input";//args[0];
//		String inputEvents = "InputEvent?";//args[1];
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();

//		TestUtils.setOutputStream(out);

		PrintWriter printWriter = new PrintWriter(out);

		StringBuilder sb = new StringBuilder();
		
		String header = "import " + DSLInvoker.class.getCanonicalName() + ";\n"// de.tud.stg.popart.builder.utils.DSLInvoker;\n"
				+ "import de.tud.stg.popart.builder.test.statemachine.fsm.*;\n"
				+ "import de.tud.stg.popart.builder.test.statemachine.*;\n"
				+ "import de.tud.stg.popart.builder.test.dsls.WordMachine;\n"
				+ "new DSLInvoker(WordMachine.class).eval() {\n";

		sb.append(header);

		
		InputStream loadTestResource = TransformationUtils.loadTestResource(inputStateMachine);
//		long start = System.nanoTime();
		String testWordMaschine = new TestWordMachine().testWordMaschine(new InputStreamReader(loadTestResource));
		
		sb.append(testWordMaschine);

//		long end = System.nanoTime();
		String footer = "\n}";
		
		sb.append(footer);

		// try {
		// in = new FileInputStream(inputStateMachine);
		// } catch (FileNotFoundException e1) {
		// logger.warn("Generated log statement",e1);
		// }

		GroovyScript groovyScript = new GroovyScript();
		groovyScript.setInput(sb.toString());
		StateMachine stateMachine = (StateMachine) groovyScript.execute();

//			new Thread(new EventProducer(new StringReader(inputEvents), stateMachine)).start();

		
		stateMachine.start();
		
		String[] events = {"start","stop","switchOff","toEnd"};
		
		for (String e : events) {			
			stateMachine.sendEvent(e);
		}			
		
//		long end2 = System.nanoTime();

		// logger.info();
		// logger.info("parsing: " + TimeUnit.NANOSECONDS.toSeconds(end - start));
		// logger.info("executing: " + TimeUnit.NANOSECONDS.toSeconds(end2 - end));
		// logger.info("total: " + TimeUnit.NANOSECONDS.toSeconds(end2 - start));
	}
}
