package de.tud.stg.tigerseye.test.transformation.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.Nullable;

import org.apache.bsf.util.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.UnhandledException;

import de.tud.stg.popart.dslsupport.DSL;

public class DefaultDSLTransformationTester{
	
	public static final File GENERATED_OUTPUT_FOLDER = new File("junit/generated/");

	private final Class<?> resourceLoader;
	private final DSLTransformationTester dtt;
	private final @Nullable File genOutputFolder;
	private final @Nullable String resourceSubPackage;
	
	public DefaultDSLTransformationTester(Class<?> resourceLoader,
			@Nullable File generatedOutputFolder,
			@Nullable String resourceSubPackage) {
		
		this.genOutputFolder = generatedOutputFolder;		
		
		this.resourceLoader = resourceLoader;
		this.resourceSubPackage = resourceSubPackage;
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
		if (genOutputFolder == null)
			throw new UnsupportedOperationException("no outputfolder defined");		
		String fileName = filePrefix + ".generated.groovy";
		return new File(genOutputFolder, fileName);
	}

	private String getResoucesLocation() {
		if (resourceSubPackage == null) {
			return "";
		} else {
			return resourceSubPackage+"/";
		}
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
		InputStream resourceAsStream = this.resourceLoader
				.getResourceAsStream(string);
		String stringFromReader = IOUtils
				.getStringFromReader(new InputStreamReader(resourceAsStream));
		return stringFromReader;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void assertTransformedDSLEqualsExpectedUnchecked(String filePrefix,
			Class... classes) throws Exception {
		this.dtt.assertTransformedDSLEqualsExpected(filePrefix, classes);
	}
	
	public void assertTransformedDSLEqualsExpected(
			String filePrefix, Class<? extends DSL>... classes)
			throws Exception {
		this.dtt.assertTransformedDSLEqualsExpectedUnchecked(filePrefix, classes);
	}
	

}
