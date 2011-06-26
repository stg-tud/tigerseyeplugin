package utilities;

import java.io.File;

import org.junit.Before;

import de.tud.stg.tigerseye.test.transformation.utils.DefaultDSLTransformationTester;

public class DSLTransformationTestBase {
	
	private DefaultDSLTransformationTester dtt;
	private static final File generated_groovy_file_output_folder = DefaultDSLTransformationTester.GENERATED_OUTPUT_FOLDER;

	@Before
	public void setUp() throws Exception {
		dtt = createTransformationTester();
	}

	protected DefaultDSLTransformationTester createTransformationTester() {
		return new DefaultDSLTransformationTester(
				getClass(), 
						generated_groovy_file_output_folder, "resources");
	}

	protected File getOutputFile(String filePrefix) {
		String gen = generated_groovy_file_output_folder + filePrefix
				+ ".generated.groovy";
		return new File(gen);
	}

	protected void assertTransformedDSLEqualsExpected(String string,
			Class... classes) throws Exception {
		dtt.assertTransformedDSLEqualsExpected(string, classes);
	}

}
