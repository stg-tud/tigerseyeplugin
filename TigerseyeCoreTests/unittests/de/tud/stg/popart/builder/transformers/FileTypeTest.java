package de.tud.stg.popart.builder.transformers;

import static org.junit.Assert.*;

import org.junit.Test;

import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileTypeHelper;


public class FileTypeTest {

	@Test
	public void testDSLName() throws Exception {
		FileType type = FileTypeHelper
				.getTypeForSrcResource("/src/some/package/SimpleSet.set.dsl");
		FileType expected = FileType.TIGERSEYE;
		assertEquals(expected, type);
	}

	@Test
	public void testGroovyName() throws Exception {
		FileType type = FileTypeHelper
				.getTypeForSrcResource("/src/some/package/SimpleSet.groovy.dsl");
		FileType expected = FileType.GROOVY;
		assertEquals(expected, type);
	}

	@Test
	public void testJavaName() throws Exception {
		FileType type = FileTypeHelper
				.getTypeForSrcResource("/src/some/package/SimpleSet.java.dsl");
		FileType expected = FileType.JAVA;
		assertEquals(expected, type);
	}

	@Test
	public void testOutputDSLName() throws Exception {
		FileType type = FileTypeHelper
				.getTypeForOutputResource("/src/some/package/SimpleSet$anythingesle_dsl.groovy");
		FileType expected = FileType.TIGERSEYE;
		assertEquals(expected, type);
	}

	@Test
	public void testOutputGroovyNameWithSrc() throws Exception {
		FileType type = FileTypeHelper
				.getTypeForSrcResource("/src/some/package/SimpleSet.groovy");
		assertNull(type);
	}

	@Test
	public void testOutputGroovyName() throws Exception {
		FileType type = FileTypeHelper
				.getTypeForOutputResource("/src/some/package/SimpleSet.groovy");
		FileType expected = FileType.GROOVY;
		assertEquals(expected, type);
	}

	@Test
	public void testOutputJavaName() throws Exception {
		FileType type = FileTypeHelper
				.getTypeForSrcResource("/src/some/package/SimpleSet.java");
		assertNull(type);
	}

}
