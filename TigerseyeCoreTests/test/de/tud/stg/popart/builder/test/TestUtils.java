package de.tud.stg.popart.builder.test;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import jjtraveler.VisitFailure;
import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Level;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aterm.ATerm;
import de.tud.stg.parlex.ast.IAbstractNode;
import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.parlex.lexer.ILexer;
import de.tud.stg.parlex.lexer.KeywordSensitiveLexer;
import de.tud.stg.parlex.lexer.KeywordSeperator;
import de.tud.stg.parlex.parser.earley.Chart;
import de.tud.stg.parlex.parser.earley.EarleyParser;
import de.tud.stg.popart.dslsupport.DSL;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.Context;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast.InvokationDispatcherTransformation;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast.KeywordChainingTransformation;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarBuilder;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.UnicodeLookupTable;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.ATermBuilder;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.PrettyGroovyCodePrinter;

public class TestUtils {

	private static final File inputDir = new File("test");

	private static final String generatedFilesFolder = "test/de/tud/stg/popart/builder/test/generated/";

	static {
		//Setting logger since logger is not initialized in test run configuration
		org.apache.log4j.Logger.getRootLogger().setLevel(Level.TRACE);
	}

	private static final Logger logger = LoggerFactory
			.getLogger(TestUtils.class);

	public static OutputStream out = System.out;

	public static void test(String file, Class<? extends DSL>... classes) {
		test(true, file, classes);
	}

	public static void setOutputStream(OutputStream out) {
		TestUtils.out = out;
	}

	public static void test(boolean validate, String file,
			Class<? extends DSL>... classes) {
		try {
			UnicodeLookupTable.setDefaultInstance(new UnicodeLookupTable(
					new FileInputStream("resources/MathClassEx-11.txt")));

			GrammarBuilder gb = new GrammarBuilder();
			IGrammar<String> grammar = gb.buildGrammar(classes);

			logger.info("= Grammar for classes: {}",Arrays.toString(classes));

			ILexer lexer = new KeywordSensitiveLexer(new KeywordSeperator());
			EarleyParser earleyParser = new EarleyParser(lexer, grammar);
			
			File inputFile = getInputFile(file);
			String sb = FileUtils.readFileToString(inputFile);
			logger.info("= Parsing Input File = {}", inputFile);			

			Chart chart = (Chart) earleyParser.parse(sb.trim());

			Context context = new Context(file);
			context.setGrammarBuilder(gb);
			for (Class<? extends DSL> clazz : classes) {
				context.addDSL(clazz.getSimpleName(), clazz);
			}

			IAbstractNode ast;
			ATermBuilder aTermBuilder;
			ATerm term;
			int cnt = 0;
			// do {
			ast = chart.getAST();
			chart.nextAmbiguity();
			cnt++;
			logger.info("= Ambiguity Index = {}",String.valueOf(cnt));

			aTermBuilder = new ATermBuilder(ast);
			term = aTermBuilder.getATerm();

			term = new KeywordChainingTransformation().transform(context, term);

			if (classes.length > 1) {
				// term = new ClosureResultTransformer().transform(context,
				// term);
				term = new InvokationDispatcherTransformation().transform(
						context, term);
			}

			// PrettyJavaCodePrinter prettyPrinter = new
			// PrettyJavaCodePrinter();
			PrettyGroovyCodePrinter prettyPrinter = new PrettyGroovyCodePrinter();

			term.accept(prettyPrinter);
			File testFile = getGeneratedFile(file);
			FileOutputStream out = new FileOutputStream(testFile);

			prettyPrinter.write(out);

			term.accept(prettyPrinter);


			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			prettyPrinter.write(TestUtils.out);
			prettyPrinter.write(baos);
			logger.info("Transformed written to file", testFile);
			// } while (chart.hasMoreAmbiguities());

		} catch (FileNotFoundException e) {
			throw new UnhandledException(e);
		} catch (IOException e) {
			throw new UnhandledException(e);
		} catch (VisitFailure e) {
			throw new UnhandledException(e);
		}

		if (validate) {
			validate(file);
		}
	}

	private static File getExpectedFile(String file) {
		return new File(inputDir,file + ".expected");
	}

	private static File getGeneratedFile(String file) {
		return new File(generatedFilesFolder, (file +".generated.groovy"));
	}

	private static File getInputFile(String file) {
		return new File(inputDir, file + ".input");
	}

	private static void validate(String output, String expected) {
		output = makeComparable(output);
		expected = makeComparable(expected);
		Assert.assertEquals(output, expected);
	}

	private static String makeComparable(String output) {
		return output.replaceAll("\\s", "");
	}

	private static void validate(String file) {
		try {
			String output  = FileUtils.readFileToString(getGeneratedFile(file));
			String expected = FileUtils.readFileToString(getExpectedFile(file));
			validate(expected, output);
		} catch (IOException e) {
			throw new UnhandledException(e);
		}
	
	}

	@Test
	public void testAccessibleInputFiles() throws Exception {

		FileOutputStream out = null;
		try {
			String string = new File(generatedFilesFolder
					+ "GroovyBigCombinedDSL.groovy").toString();
			out = new FileOutputStream(string);
			out.write(new byte[0]);
			assertNotNull(out);
		} finally {
			org.apache.commons.io.IOUtils.closeQuietly(out);
		}
	}

}
