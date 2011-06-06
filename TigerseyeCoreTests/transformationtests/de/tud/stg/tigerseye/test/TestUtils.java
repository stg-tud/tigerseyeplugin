package de.tud.stg.tigerseye.test;


import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import jjtraveler.VisitFailure;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.UnhandledException;
import org.junit.Test;

import de.tud.stg.popart.dslsupport.DSL;
import de.tud.stg.tigerseye.test.transformation.utils.DefaultDSLTransformationTester;


public class TestUtils {
	
	
	private static final String generatedFilesFolder = DefaultDSLTransformationTester.GENERATED_OUTPUT_FOLDER;

	private static DefaultDSLTransformationTester dtt = new DefaultDSLTransformationTester(TestUtils.class, new File(generatedFilesFolder), "resources" );
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

}