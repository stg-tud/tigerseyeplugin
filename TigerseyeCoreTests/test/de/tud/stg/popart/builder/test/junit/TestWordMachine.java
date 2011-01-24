package de.tud.stg.popart.builder.test.junit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import jjtraveler.VisitFailure;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aterm.ATerm;
import de.tud.stg.parlex.ast.IAbstractNode;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.parser.earley.Chart;
import de.tud.stg.parlex.parser.earley.EarleyParser;
import de.tud.stg.popart.builder.core.GrammarBuilder;
import de.tud.stg.popart.builder.core.UnicodeLookupTable;
import de.tud.stg.popart.builder.core.aterm.ATermBuilder;
import de.tud.stg.popart.builder.core.aterm.PrettyGroovyCodePrinter;
import de.tud.stg.popart.builder.test.GroovyScript;
import de.tud.stg.popart.builder.test.TestUtils;
import de.tud.stg.popart.builder.test.dsls.WordMachine;
import de.tud.stg.popart.builder.test.statemachine.fsm.EventProducer;
import de.tud.stg.popart.builder.test.statemachine.fsm.StateMachine;
import de.tud.stg.popart.builder.transformers.Context;
import de.tud.stg.popart.builder.transformers.ast.KeywordChainingTransformation;

public class TestWordMachine {
private static final Logger logger = LoggerFactory.getLogger(TestWordMachine.class);


	public void testWordMaschine(String inputFile, OutputStream out) {
		try {
			UnicodeLookupTable.setDefaultInstance(new UnicodeLookupTable(new FileInputStream(
					"resources/MathClassEx-11.txt")));

			GrammarBuilder gb = new GrammarBuilder();

			IGrammar<String> grammar = gb.buildGrammar(WordMachine.class);

			EarleyParser earleyParser = new EarleyParser(null, grammar);

			String sb = FileUtils.readFileToString(new File(inputFile));

			Chart chart = (Chart) earleyParser.parse(sb.trim());

			Context context = new Context(inputFile);
			context.setGrammarBuilder(gb);
			context.addDSL(WordMachine.class.getSimpleName(), WordMachine.class);

			IAbstractNode ast;
			ATermBuilder aTermBuilder;
			ATerm term;
			ast = chart.getAST();
			chart.nextAmbiguity();

			aTermBuilder = new ATermBuilder(ast);
			term = aTermBuilder.getATerm();

			term = new KeywordChainingTransformation().transform(context, term);

			PrettyGroovyCodePrinter prettyPrinter = new PrettyGroovyCodePrinter();

			term.accept(prettyPrinter);
			prettyPrinter.write(out);

		} catch (FileNotFoundException e) {
			logger.warn("Generated log statement",e);
		} catch (IOException e) {
			logger.warn("Generated log statement",e);
		} catch (VisitFailure e) {
			logger.warn("Generated log statement",e);
		}
	}

	public static void main(String[] args) {

		String inputStateMachine = "test/WordMaschine.input";//args[0];
		String inputEvents = "InputEvent?";//args[1];

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStream out = baos;

		TestUtils.setOutputStream(out);

		PrintWriter printWriter = new PrintWriter(out);

		String header = "import de.tud.stg.popart.builder.utils.DSLInvoker;\n"
				+ "import de.tud.stg.popart.builder.test.statemachine.fsm.*;\n"
				+ "import de.tud.stg.popart.builder.test.statemachine.*;\n"
				+ "import de.tud.stg.popart.builder.test.dsls.WordMachine;\n"
				+ "new DSLInvoker(WordMachine.class).eval() {\n";

		printWriter.write(header);
		printWriter.flush();

		long start = System.nanoTime();
		new TestWordMachine().testWordMaschine(inputStateMachine, out);

		long end = System.nanoTime();
		String footer = "\n}";

		printWriter.write(footer);
		printWriter.flush();

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

		InputStream in = bais;
		// try {
		// in = new FileInputStream(inputStateMachine);
		// } catch (FileNotFoundException e1) {
		// logger.warn("Generated log statement",e1);
		// }

		GroovyScript groovyScript = new GroovyScript();
		groovyScript.setInput(in);
		StateMachine stateMachine = (StateMachine) groovyScript.execute();

		try {
			new Thread(new EventProducer(new FileReader(inputEvents), stateMachine)).start();
		} catch (FileNotFoundException e) {
			logger.warn("Generated log statement",e);
		}

		stateMachine.start();

		long end2 = System.nanoTime();

		// logger.info();
		// logger.info("parsing: " + TimeUnit.NANOSECONDS.toSeconds(end - start));
		// logger.info("executing: " + TimeUnit.NANOSECONDS.toSeconds(end2 - end));
		// logger.info("total: " + TimeUnit.NANOSECONDS.toSeconds(end2 - start));
	}
}
