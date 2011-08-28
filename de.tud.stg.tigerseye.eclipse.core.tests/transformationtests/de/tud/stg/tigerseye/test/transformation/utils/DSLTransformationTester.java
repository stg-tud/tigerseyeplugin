package de.tud.stg.tigerseye.test.transformation.utils;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import utilities.StringUtils;
import de.tud.stg.popart.dslsupport.DSL;
import de.tud.stg.tigerseye.test.PrettyGroovyCodePrinterFactory;
import de.tud.stg.tigerseye.test.TestDSLTransformation;

public class DSLTransformationTester {
	
	private @Nullable GeneratedTransformationWriter genWriter;
	private final ExpectedRetriever expectedRetriever;
	private final DSLInputRetriever inputRetriever;
 
	public DSLTransformationTester(DSLInputRetriever ir, ExpectedRetriever er) {
		this.inputRetriever = ir;
		this.expectedRetriever = er;
	}
	
	public DSLTransformationTester(DSLInputRetriever ir, ExpectedRetriever er, GeneratedTransformationWriter gtw) {
		this(ir,er);
		this.genWriter = gtw;
	}
	

	public void assertTransformedDSLEqualsExpectedUnchecked(String filePrefix,
			Class<? extends DSL>... classes) throws Exception {
		
		List<Class<? extends DSL>> asList =  (List<Class<? extends DSL>>) Arrays.asList(classes);
		assertTransformedDSLEqualsExpected(filePrefix, asList);
	}

	/**
	 * Takes a file {@code filePrefix.input} as input file, performs the
	 * transformation of its content and compares it to a file called
	 * {@code filePrefix.expected}. Their expected locations are defined by
	 * {@link #getInputDSL(String)} and {@link #getExpectedStream(String)}
	 * respectively. <br>
	 * To easier analyze the result the transformed files are also written to a
	 * file called {@code filePrefix.generated.groovy}. Its output location is
	 * defined by {@link #getOutputFile(String)}<br>
	 * 
	 * @param filePrefix The file name pattern to identify which class to load
	 * @param classes
	 *            The DSLs definitions used in {@code filePrefix}, the
	 *            transformation will be performed for a combination of all
	 *            classes passed.
	 */
	public void assertTransformedDSLEqualsExpected(
			String filePrefix, List<Class<? extends DSL>> classes)
			throws Exception {
		String transformation = new TestDSLTransformation(
				new PrettyGroovyCodePrinterFactory()).performTransformation(
				getInputDSL(filePrefix), classes);
	System.out.println(transformation);
		if(this.genWriter != null)
			this.genWriter.write(filePrefix, transformation);
	
		StringUtils.equalsIgnoringWhitspace(transformation, getExpectedStream(filePrefix));
	
	}
	

	private String getExpectedStream(String filePrefix) {
		return this.expectedRetriever.getExpectedFor(filePrefix);
	}

	private String getInputDSL(String filePrefix) {
		return this.inputRetriever.getInputFor(filePrefix);
	}

}
