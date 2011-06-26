package de.tud.stg.tigerseye.examples.mapdsl;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import jjtraveler.VisitFailure;

import org.apache.bsf.util.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.UnhandledException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import utilities.DSLTransformationTestBase;

import de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler.DSLResourceHandler;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.PrettyGroovyCodePrinter;
import de.tud.stg.tigerseye.examples.mapdsl.MapDSL;

public class MapDSLTest extends DSLTransformationTestBase {

	@Test
	public void testAMapDSLTransformationShorter() throws Exception {
		assertTransformedDSLEqualsExpected("MapDSLShorter",
				MapDSL.class);
	}

	@Ignore("fails because priorization of Number before String was removed")
	@Test
	public void testAMapDSLTransformation() throws Exception {
		assertTransformedDSLEqualsExpected("MapDSL", MapDSL.class);
	}

	@Ignore("fails because priorization of Number before String was removed")
	@Test
	public void testmultipleMapstatementstransformations() throws Exception {
		assertTransformedDSLEqualsExpected("MapDSLlonger",
				MapDSL.class);
	}

	@Ignore("fails because the semantic information is missing for variables; needs types inference to solve this problem.")
	@Test
	public void testMapDSLWithVariablesAsKeys() throws Exception {
		/*
		 * Fails because variables instead of the concrete type are passed for
		 * values
		 */
		assertTransformedDSLEqualsExpected(
				"MapDSLWithVariablesAsKeys", MapDSL.class);
	}

	@Ignore("Test needs different transforamtion procedure")
	@Test
	public void testMapDSLFileFormatTransformation() throws Exception {
		assertTransformedDSLEqualsExpected("MapDSLinDSLFileFormat",
				MapDSL.class);

		DSLResourceHandler drh = new DSLResourceHandler(FileType.DSL,
				new PrettyGroovyCodePrinter());
	}

}
