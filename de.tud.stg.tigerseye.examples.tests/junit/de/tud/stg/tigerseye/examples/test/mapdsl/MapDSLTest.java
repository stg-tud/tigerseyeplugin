package de.tud.stg.tigerseye.examples.test.mapdsl;

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

import de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler.DSLResourceHandler;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.PrettyGroovyCodePrinter;
import de.tud.stg.tigerseye.examples.mapdsl.MapDSL;
import de.tud.stg.tigerseye.examples.test.DSLTransformationTestBase;

public class MapDSLTest extends DSLTransformationTestBase{

	

	@Test
	public void testAMapDSLTransformation() throws Exception {
		assertTransformedDSLEqualsExpectedUnchecked("MapDSL", MapDSL.class);
	}

	@Test
	public void testmultipleMapstatementstransforamtions() throws Exception {
		assertTransformedDSLEqualsExpectedUnchecked("MapDSLlonger",
				MapDSL.class);
	}

	@Ignore("fails because the semantic information is missing for variables; needs types inference to solve this problem.")
	@Test
	public void testMapDSLWithVariablesAsKeys() throws Exception {
		/*
		 * Fails because variables instead of the concrete type are passed for
		 * values
		 */
		assertTransformedDSLEqualsExpectedUnchecked(
				"MapDSLWithVariablesAsKeys", MapDSL.class);
	}
	
	@Ignore("Test needs different transforamtion procedure")
	@Test
	public void testMapDSLFileFormatTransformation() throws Exception {
		assertTransformedDSLEqualsExpectedUnchecked("MapDSLinDSLFileFormat",
				MapDSL.class);

		DSLResourceHandler drh = new DSLResourceHandler(FileType.DSL,
				new PrettyGroovyCodePrinter());
	}

}
