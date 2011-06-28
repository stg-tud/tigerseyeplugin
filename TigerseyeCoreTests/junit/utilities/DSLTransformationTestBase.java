package utilities;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;

import de.tud.stg.tigerseye.test.transformation.utils.DefaultDSLTransformationTester;

public class DSLTransformationTestBase {
	
	private DefaultDSLTransformationTester dtt;
	private PrintStream original;
	private static final File generated_groovy_file_output_folder = DefaultDSLTransformationTester.GENERATED_OUTPUT_FOLDER;

	@Before
	public void setUp() throws Exception {
		dtt = createTransformationTester();
		original = null;
	}
	
	@After
	public void after() throws Exception{
		if(original != null){
			System.setOut(original);
		}
	}
	
	protected void redirectOutput(OutputStream redirectTo){
		original = System.out;
		System.setOut(new PrintStream(redirectTo));
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
