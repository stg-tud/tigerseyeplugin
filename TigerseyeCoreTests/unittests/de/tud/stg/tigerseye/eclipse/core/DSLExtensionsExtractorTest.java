package de.tud.stg.tigerseye.eclipse.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class DSLExtensionsExtractorTest {

	@Test
	public void testGetExtensionsForSrcName() throws Exception {
		String resourceSrcName = "MyDSL.set.sql.some.dsl";
		String[] exptected = { "set", "sql", "some" };
		testExpectedExtensionsExtracted(exptected, resourceSrcName);
	}

	@Test
	public void testGetExtensionsForSrcName2() throws Exception {
		String resourceSrcName = "MyDSL.set.sql.dsl";
		String[] exptected = { "set", "sql" };
		testExpectedExtensionsExtracted(exptected, resourceSrcName);
	}

	@Test
	public void testGetExtensionsForSrcNameIllegal() throws Exception {
		String resourceSrcName = "MyDSL.invalid.extension";
		testExpectedExtensionsExtracted(new String[0], resourceSrcName);
	}

	@Test
	public void testGetExtensionsForSrcName3() throws Exception {
		String resourceSrcName = "MyDSL.dsl";
		String[] exptected = {};
		testExpectedExtensionsExtracted(exptected, resourceSrcName);
	}

	@Test
	public void testGetExtensionsForSrcNameGroovy() throws Exception {
		String resourceSrcName = "MyDSL.groovy.dsl";
		String[] exptected = {};
		testExpectedExtensionsExtracted(exptected, resourceSrcName);
	}

	@Test
	public void testGetExtensionsForSrcNameJava() throws Exception {
		String resourceSrcName = "MyDSL.java.dsl";
		String[] exptected = {};
		testExpectedExtensionsExtracted(exptected, resourceSrcName);
	}

	@Test
	public void testGetExtensionsForOutputNameDSL() throws Exception {
		String resourceSrcName = "mynextdsl$_foreach_dsl.groovy";
		String[] exptected = { "foreach" };
		testExpectedExtensionsExtractedForOutput(exptected, resourceSrcName);
	}

	@Test
	public void testGetExtensionsForOutputNameDSL2() throws Exception {
		String resourceSrcName = "somesettest$_set_sql_dsl.groovy";
		String[] exptected = { "set", "sql" };
		testExpectedExtensionsExtractedForOutput(exptected, resourceSrcName);
	}

	@Test
	public void testGetExtensionsForOutputNameDSLillegal() throws Exception {
		String resourceSrcName = "somesettest$_set_sql_dsl.java";
		String[] exptected = {};
		testExpectedExtensionsExtractedForOutput(exptected, resourceSrcName);
	}

	@Test
	public void testGetExtensionsForOutputNameDSLillegal2() throws Exception {
		String resourceSrcName = "afjaaoszupo43hj6ö23n65";
		String[] exptected = {};
		testExpectedExtensionsExtractedForOutput(exptected, resourceSrcName);
	}

	private void testExpectedExtensionsExtractedForOutput(String[] exptected,
			String resourceSrcName) {
		DSLExtensionsExtractor extractor = new DSLExtensionsExtractor();
		String[] extensions = extractor
				.getExtensionsForOutputResource(resourceSrcName);
		assertContainsOnlyExpected(exptected, extensions);
	}

	private void testExpectedExtensionsExtracted(String[] exptected,
			String resourceSrcName) {
		DSLExtensionsExtractor extractor = new DSLExtensionsExtractor();
		String[] extensions = extractor
				.getExtensionsForSrcResource(resourceSrcName);
		assertContainsOnlyExpected(exptected, extensions);
	}

	private void assertContainsOnlyExpected(String[] exptected,
			String[] extensions) {
		List<String> asList = Arrays.asList(extensions);
		for (String string : exptected) {
			boolean contains = asList.contains(string);
			assertTrue("not all extensions found", contains);
		}
		assertEquals("actual extensions " + Arrays.toString(extensions),
				exptected.length, extensions.length);
	}
}
