package de.tud.stg.tigerseye.test;


import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import jjtraveler.VisitFailure;
import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.popart.dslsupport.DSL;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.UnicodeLookupTable;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.PrettyGroovyCodePrinter;

public class TestUtils {
	
	
	private static final Logger logger = LoggerFactory
			.getLogger(TestUtils.class);

	private static final String srcRootFolder = "transformationtests";

	private static final File inputDir = new File(srcRootFolder);

	private static final String generatedFilesFolder = srcRootFolder + "/generated/";

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

			File inputFile = getInputFile(file);
			File testFile = getGeneratedFile(file);
			
			isPerformingExpectedTransformation(inputFile, testFile,
					classes);

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
	


	private static void isPerformingExpectedTransformation(
			File inputFile, File testFile,
			Class<? extends DSL>... classes) throws IOException, VisitFailure {
		
		FileInputStream fileInputStream = new FileInputStream(inputFile);
		String transformation = new TestDSLTransformation(new PrettyGroovyCodePrinterFactory()).performTransformation(fileInputStream, classes);
		logger.debug("transformed code: {}", transformation);
		FileUtils.writeStringToFile(testFile, transformation);		
		logger.info("Transformed written to file", testFile);
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

	public static void equalsIgnoringWhitspace(String output, String expected) {
		output = removeWhitespaces(output);
		expected = removeWhitespaces(expected);
		Assert.assertEquals(output, expected);
	}
	
	public static void equalsLinewiseIgnoringWhitespace(String stringa, String stringb){
		List<String> linesa = toComparableList(stringa);
		List<String> linesb = toComparableList(stringb);
		
		assertSeparateleyContained(linesa, linesb);
		assertSeparateleyContained(linesb, linesa);
		
	}

	private static List<String> toComparableList(String stringa) {
		return filterWhitespaceLines(asLines(stringa));
	}

	private static List<String> filterWhitespaceLines(List<String> asLines) {
		List<String> resultLs = new ArrayList<String>(asLines.size());
		for (String string : asLines) {
			String noWhitespace = removeWhitespaces(string);
			if(!noWhitespace.isEmpty())
				resultLs.add(noWhitespace);
		}		
		return resultLs;
	}

	private static void assertSeparateleyContained(List<String> containsAllOf,
			List<String> contained) {
		for (String string : contained) {			
			assertTrue("not contained: ["+string+"]",containsAllOf.contains(string));
		}
	}

	private static String[] splitLines(String stringa) {
		String[] split = stringa.split("\\n");
		return split;
	}

	private static List<String> asLines(String stringa) {
		List<String> asList = Arrays.asList(splitLines(stringa));
		return asList;
	}

	private static String removeWhitespaces(String astr) {
		return astr.replaceAll("\\s", "");
	}

	private static void validate(String file) {
		try {
			String output  = FileUtils.readFileToString(getGeneratedFile(file));
			String expected = FileUtils.readFileToString(getExpectedFile(file));
//			equalsIgnoringWhitspace(expected, output);
			equalsIgnoringWhitspace(expected, output);
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
			IOUtils.closeQuietly(out);
		}
	}

}
