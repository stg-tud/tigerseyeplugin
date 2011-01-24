package de.tud.stg.popart.builder.transformers;

import static org.junit.Assert.*;

import org.junit.Test;


public class FileTypeTest {

	@Test
	public void testDSLName() throws Exception {
		Filetype type = Filetype
				.getTypeForSrcResource("/src/some/package/SimpleSet.set.dsl");
		Filetype expected = Filetype.POPART;
		assertEquals(expected, type);
	}

	@Test
	public void testGroovyName() throws Exception {
		Filetype type = Filetype
				.getTypeForSrcResource("/src/some/package/SimpleSet.groovy.dsl");
		Filetype expected = Filetype.GROOVY;
		assertEquals(expected, type);
	}

	@Test
	public void testJavaName() throws Exception {
		Filetype type = Filetype
				.getTypeForSrcResource("/src/some/package/SimpleSet.java.dsl");
		Filetype expected = Filetype.JAVA;
		assertEquals(expected, type);
	}

	@Test
	public void testOutputDSLName() throws Exception {
		Filetype type = Filetype
				.getTypeForOutputResource("/src/some/package/SimpleSet$anythingesle_dsl.groovy");
		Filetype expected = Filetype.POPART;
		assertEquals(expected, type);
	}

	@Test
	public void testOutputGroovyNameWithSrc() throws Exception {
		Filetype type = Filetype
				.getTypeForSrcResource("/src/some/package/SimpleSet.groovy");
		assertNull(type);
	}

	@Test
	public void testOutputGroovyName() throws Exception {
		Filetype type = Filetype
				.getTypeForOutputResource("/src/some/package/SimpleSet.groovy");
		Filetype expected = Filetype.GROOVY;
		assertEquals(expected, type);
	}

	@Test
	public void testOutputJavaName() throws Exception {
		Filetype type = Filetype
				.getTypeForSrcResource("/src/some/package/SimpleSet.java");
		assertNull(type);
	}

}
