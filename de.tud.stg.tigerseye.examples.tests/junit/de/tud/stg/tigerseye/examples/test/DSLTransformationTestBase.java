package de.tud.stg.tigerseye.examples.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.bsf.util.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.UnhandledException;
import org.junit.Before;

public class DSLTransformationTestBase {

	private static final String generated_groovy_file_output_folder = "junit/generated/";
	private DSLTransformationTester dtt;

	@Before
	public void setUp() throws Exception {
		dtt = new DSLTransformationTester(new DSLInputRetriever() {
			@Override
			public String getInputFor(String filePrefix) {
				try {
					return getInputDSLStream(filePrefix);
				} catch (IOException e) {
					throw new UnhandledException(e);
				}
			}
		}, new ExpectedRetriever() {
			@Override
			public String getExpectedFor(String filePrefix) {
				try {
					return getExpectedSring(filePrefix);
				} catch (IOException e) {
					throw new UnhandledException(e);
				}
			}
		}, new GeneratedTransformationWriter() {
			@Override
			public void write(String filePrefix, String content) {
				File outputFile = getOutputFile(filePrefix);
				try {
					FileUtils.writeStringToFile(outputFile, content);
				} catch (IOException e) {
					throw new UnhandledException(e);
				}
			}
		});
	}
	

	protected File getOutputFile(String filePrefix) {
		String gen = generated_groovy_file_output_folder + filePrefix + ".generated.groovy";
		return new File(gen);
	}

	private String getResoucesLocation() {
		return "resources/";
	}
	
	protected String getExpectedSring(String filePrefix) throws IOException {
		String resName = getResoucesLocation() + filePrefix + ".expected";
		return getRessourceString(resName);
	}
	
	protected String getInputDSLStream(String filePrefix) throws IOException {
		String string = getResoucesLocation() + filePrefix + ".input";
		String stringFromReader = getRessourceString(string);
		return stringFromReader;
	}

	private String getRessourceString(String string) throws IOException {
		InputStream resourceAsStream = DSLTransformationTestBase.class.getResourceAsStream(string);
		String stringFromReader = IOUtils
				.getStringFromReader(new InputStreamReader(resourceAsStream));
		return stringFromReader;
	}
	
	protected void assertTransformedDSLEqualsExpectedUnchecked(String string,
			Class ... classes) throws Exception {
		dtt.assertTransformedDSLEqualsExpected(string, classes);
	}

	
}
