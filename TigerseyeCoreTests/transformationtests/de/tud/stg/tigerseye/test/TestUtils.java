package de.tud.stg.tigerseye.test;


import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import jjtraveler.VisitFailure;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.UnhandledException;
import org.junit.Test;

import de.tud.stg.parlex.core.IGrammar;
import de.tud.stg.popart.dslsupport.DSL;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.GrammarBuilder;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.UnicodeLookupTable;
import de.tud.stg.tigerseye.test.TestDSLTransformation.GrammarResult;
import de.tud.stg.tigerseye.test.transformation.utils.DefaultDSLTransformationTester;


public class TestUtils {
	
	public static String PluginIDOfCoreTestFragment = "de.tud.stg.tigerseye.eclipse.core.tests"; 
	
	private static final String MATH_CLASS_EX_TXT = "MathClassEx-12.txt";

	private static final File generatedFilesFolder = DefaultDSLTransformationTester.GENERATED_OUTPUT_FOLDER;

	private static DefaultDSLTransformationTester dtt = new DefaultDSLTransformationTester(TestUtils.class, generatedFilesFolder, "resources" );
	public static OutputStream out = System.out;

	@SuppressWarnings("unchecked")
	public static void test(String file, @SuppressWarnings("rawtypes") Class ... classes) {
		test(true, file, classes);
	}

	public static void setOutputStream(OutputStream out) {
		TestUtils.out = out;
	}

	public static void test(boolean validate, String file,
			Class<? extends DSL>... classes) {
		try {

			dtt.assertTransformedDSLEqualsExpectedUnchecked(file, classes);
			
		} catch (FileNotFoundException e) {
			throw new UnhandledException(e);
		} catch (IOException e) {
			throw new UnhandledException(e);
		} catch (VisitFailure e) {
			throw new UnhandledException(e);
		} catch (Exception e) {
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
	
	public static GrammarResult newGrammar(Class<?>... classes) {
	
		GrammarBuilder grammarBuilder = new GrammarBuilder(
				getDefaultLookupTable());
	
		IGrammar<String> buildGrammar = grammarBuilder.buildGrammar(classes);
	
		GrammarResult grammarResult = new GrammarResult(buildGrammar,
				grammarBuilder.getMethodOptions(), classes);
	
		return grammarResult;
	}

	public static File[] getFilesRelativeToRoot(File resFile, String ... expected) {
		File[] expectedFiles = new File[expected.length];
		for (int i = 0 ; i < expected.length; i ++) {
			expectedFiles[i] = new File(resFile, expected[i]);
		}
		return expectedFiles;
	}

	public static void assertContainsAllLines(String doesContain, String isContained) {
		Scanner expScanner = new Scanner(isContained);
		while(expScanner.hasNextLine()){
			assertThat(doesContain, containsString(expScanner.nextLine()));
		}
	}

	private static Reader getMathClassEx11(){
		InputStream resourceAsStream = TestUtils.class.getResourceAsStream(MATH_CLASS_EX_TXT);
		try {
			InputStreamReader reader = new InputStreamReader(resourceAsStream, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(reader);
			return bufferedReader;
		} catch (UnsupportedEncodingException e) {
			throw new UnhandledException(e);
		} 
	}
	
	public static UnicodeLookupTable getDefaultLookupTable(){
		Reader reader = getMathClassEx11();
		UnicodeLookupTable ult = new UnicodeLookupTable().load(reader);
		return ult;
	}

	public static void assertContainsAllLinesMutually(String s1,
			String s2) {
		assertContainsAllLines(s1,s2);
		assertContainsAllLines(s2,s1);		
	}
	
	public static InputStream loadTestResource(String resourceName){
		InputStream resourceAsStream = TestUtils.class.getResourceAsStream("resources" + "/" + resourceName);
		return resourceAsStream;
	}

}
