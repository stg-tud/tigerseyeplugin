package de.tud.stg.tigerseye.examples.test;

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
import org.junit.Before;
import org.junit.Test;

import de.tud.stg.popart.dslsupport.DSL;
import de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler.DSLResourceHandler;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.PrettyGroovyCodePrinter;
import de.tud.stg.tigerseye.examples.mapdsl.MapDSL;
import de.tud.stg.tigerseye.test.PrettyGroovyCodePrinterFactory;
import de.tud.stg.tigerseye.test.TestDSLTransformation;
import de.tud.stg.tigerseye.test.TestUtils;

public class MapDSLTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testAMapDSLTransformation() throws Exception {
		String filePrefix = "MapDSL";
		Class<? extends DSL>[] classes = new Class[] { MapDSL.class };
		
		assertTransformedForInputMatchesExpected(filePrefix, classes);
	}

	private void assertTransformedForInputMatchesExpected(String filePrefix,
			Class<? extends DSL> ... classes  ) throws IOException, VisitFailure,
			FileNotFoundException {
		String transformation = new TestDSLTransformation(
				new PrettyGroovyCodePrinterFactory()).performTransformation(
				getInputFileStream(filePrefix), classes);

		FileUtils.writeStringToFile(getOutputFile(filePrefix), transformation);
		
		TestUtils.equalsIgnoringWhitspace(transformation, IOUtils
				.getStringFromReader(new InputStreamReader(
						getExpectedInputStream(filePrefix))));
		
		
	}

	private InputStream getExpectedInputStream(String filePrefix) {
		return getClass().getResourceAsStream(
				getResoucesLocation() + filePrefix + ".expected");
	}

	private InputStream getInputFileStream(String filePrefix) {
		String string = getResoucesLocation() + filePrefix + ".input";
		InputStream resourceAsStream = getClass().getResourceAsStream(string);
		return resourceAsStream;
	}
	
	private File getOutputFile(String filePrefix){
		String gen = "generatedresources/" + filePrefix + ".generated.groovy";
		return new File(gen);
	}

	private String getResoucesLocation() {
		return "resources/";
	}
	
	@Test
	public void testmultipleMapstatementstransforamtions() throws Exception {
		assertTransformedForInputMatchesExpected("MapDSLlonger", MapDSL.class);
	}
	
	@Test
	public void testMapDSLFileFormatTransformation() throws Exception {
		assertTransformedForInputMatchesExpected("MapDSLinDSLFileFormat", MapDSL.class);
		
		DSLResourceHandler drh = new DSLResourceHandler(FileType.DSL, new PrettyGroovyCodePrinter());
		
	}
	

}
