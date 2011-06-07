package de.tud.stg.tigerseye.eclipse.core.utils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utilities.TestUtilities;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.utils.OutputPathHandler;

public class OutputPathHandlerTest {
    private static final String OUTPUT_DIRECTORY = "src-testoutput";

    static {
	TestUtilities.initLogger();
    }
    
    @Before
    public void before(){
	actualOutputPath = null;
    }

    private static final Logger logger = LoggerFactory
	    .getLogger(OutputPathHandlerTest.class);

    @Test
    public void testGetFileOutputPathSetDsl() {
	File originalSrcProjRelPath = new File(
		"some/package/SimpleSet.set.dsl");
    	final File expectedOutputPat = new File(
		"some/package/"
			+ "SimpleSet$_set_dsl.groovy");
	testCorrectConversion(originalSrcProjRelPath, expectedOutputPat);
    }

    @Test
    public void testGetFileOutputPathGroovyfile() {
	File originalSrcProjRelPath = new File(
		"some/package/SimpleSet.groovy.dsl");
	final File expectedOutputPat = new File(
		"some/package/"
			+ "SimpleSet.groovy");
	testCorrectConversion(originalSrcProjRelPath, expectedOutputPat);
    }

    @Test
    public void testGetFileOutputPathJavaFile() {
	File originalSrcProjRelPath = new File("SimpleSet."+FileType.JAVA.srcFileEnding);
	final File expectedOutputPat = new File(
		"SimpleSet." + FileType.JAVA.outputFileEnding);
	testCorrectConversion(originalSrcProjRelPath, expectedOutputPat);
    }

    private void testCorrectConversion(File originalSrcRelPath,
	    final File expectedOutputPath) {
	
	Path originialPath = new Path(originalSrcRelPath.getPath());	
	IPath fileOutputPath = new OutputPathHandler(OUTPUT_DIRECTORY).getSrcRelativeOutputPath(originialPath);
	File generatedOutputFile = fileOutputPath.toFile();
	logger.debug("for path {} was generated {} ", originialPath, fileOutputPath);
	assertEquals("Generated name does not equal expected",
		expectedOutputPath, generatedOutputFile);
    }
    
    private static IPath actualOutputPath = null;
    
    private IFile getFileMock(IPath path, IProject project){
    	IFile resource = mock(IFile.class);
    	when(resource.getProjectRelativePath()).thenReturn(path);
    	when(resource.getProject()).thenReturn(project);
    	return resource;
    }
    
    @Test
    public void testOutputFile() throws Exception {
	//setup
	File origin = new File("/src/only/src/matters.sql."+FileType.TIGERSEYE.srcFileEnding);
		
	final IProject project = mock(IProject.class);
	when(project.getFile(any(IPath.class))).thenAnswer(new Answer<IFile>() {

	    @Override
	    public IFile answer(InvocationOnMock invocation) throws Throwable {
		actualOutputPath = (IPath) invocation.getArguments()[0];
		return getFileMock(actualOutputPath, project);
	    }
	});	
	IFile resource = getFileMock(new Path(origin.getPath()), project);
	OutputPathHandler resourceHandler = new OutputPathHandler(OUTPUT_DIRECTORY);
//	when(resourceHandler.getProjectRelativePath(any(IPath.class))).thenReturn(outputDirectory.append(new Path("/forced/Return")));
	//execute
	resourceHandler.getOutputFile(resource);
	//validate
	logger.debug("For origin {} generated {}", origin, actualOutputPath.toFile());
	assertEquals(new File(OUTPUT_DIRECTORY), getRootFolder(actualOutputPath.toFile()));
    }

    private static File getRootFolder(File path) {
	File rootFolder = path;
	File parentFile = path.getParentFile();
	while(parentFile != null){
	    rootFolder = parentFile;
	    parentFile = rootFolder.getParentFile();
	}
	return rootFolder;
    }
    
    public static void main(String[] args) {
	File rootFolder = getRootFolder(new Path("src/only/src/matters.any.dsl").toFile());
	System.out.println(rootFolder);
	
	File rootFolder2 = getRootFolder(new File("other/path/without/"));
	System.out.println(rootFolder2);
    }
    
    @Test
    public void testProjRelativeOutputPath() throws Exception {
	//setup
	String outputDir = "src-tigerseye";
	String origin = new File("/only/src/matters.any.dsl").getPath();
	String expected = new File("/only/src/matters$_any_dsl.groovy").getPath();
	
	Path projRelOriginPath = new Path(origin);	
	OutputPathHandler resourceHandler = new OutputPathHandler(OUTPUT_DIRECTORY);
	resourceHandler.setLocalOutputDirectoryName(outputDir);
	//execute
	IPath actual = resourceHandler.getSrcRelativeOutputPath(projRelOriginPath);
	//validate
	logger.debug("For origin {} generated {}", origin, actual.toFile());
	assertEquals(new File(expected), actual.toFile());
    }
    
    @Test
	public void testGetSrcNameForOutputNameJava() throws Exception {		    
    	String outputName = "myjavafile.java";
    	String expected = "myjavafile.java.dsl";    	    	
		assertCorrectOutputforSourceName( outputName, expected);
	}
    
    @Test
	public void testGetSrcNameForOutputNamePureGroovy() throws Exception {		    
    	String outputName = "myjavafile.groovy";
    	String expected = "myjavafile.groovy.dsl";    	    	
		assertCorrectOutputforSourceName(outputName, expected);
	}
    
    @Test
	public void testGetSrcNameForOutputNameDsl1() throws Exception {		    
    	String outputName = "myjavafile$_sql_dsl.groovy";
    	String expected = "myjavafile.sql.dsl";    	    	
		assertCorrectOutputforSourceName(outputName, expected);
	}
    
    @Test
	public void testGetSrcNameForOutputNameDsl2() throws Exception {		    
    	String outputName = "myjavafile$_sql_set_cond_foreach_dsl.groovy";
    	String expected = "myjavafile.sql.set.cond.foreach.dsl";    	    	
		assertCorrectOutputforSourceName(outputName, expected);
	}
    
    @Test
	public void testGetSrcNameForOutputNameIllegaluseofdsl() throws Exception {		    
    	String outputName = "myjavafile$_sql_set_cond_foreach_dsl.groovy.notexistent";    	    	
		assertCorrectOutputforSourceName(outputName, null);
	}
    
    @Test
	public void testGetSrcNameForOutputNameIllegalusedslformat() throws Exception {		    
    	String outputName = "myjavafile$_sql_set_cond_foreach.dsl.groovy";	    	
		assertCorrectOutputforSourceName( outputName, null);
	}
    
    @Ignore("Not sure if this should be treated as illegal format")
    @Test(expected=IllegalArgumentException.class)
	public void testGetSrcNameForOutputNameIllegalformat2() throws Exception {		    
    	String outputName = "myjavafile$sql_set_cond_foreach_dsl.groovy";	    	
		assertCorrectOutputforSourceName(outputName, "");
	}

	private void assertCorrectOutputforSourceName(String outputName, String expected) {
		OutputPathHandler outputPathHandler = new OutputPathHandler(OUTPUT_DIRECTORY);
    	String actual = outputPathHandler.getSourceNameForOutputName(outputName);
    	assertEquals(expected, actual);
	}

    
}
