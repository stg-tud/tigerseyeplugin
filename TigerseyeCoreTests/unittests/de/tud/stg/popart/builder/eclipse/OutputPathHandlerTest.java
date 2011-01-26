package de.tud.stg.popart.builder.eclipse;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utilities.TestUtilities;
import de.tud.stg.popart.builder.transformers.FileType;
import de.tud.stg.tigerseye.core.OutputPathHandler;

public class OutputPathHandlerTest {
    private static final String OUTPUT_DIRECTORY = "src-popart";

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
	FileType filetype = FileType.getTypeForSrcResource(originalSrcRelPath.toString());
	IPath fileOutputPath = new OutputPathHandler(filetype, OUTPUT_DIRECTORY).getSrcRelativeOutputPath(originialPath);
	File generatedOutputFile = fileOutputPath.toFile();
	logger.debug("for path {} was generated {} ", originialPath, fileOutputPath);
	assertEquals("Generated name does not equal expected",
		expectedOutputPath, generatedOutputFile);
    }
    
    private static IPath actualOutputPath = null;
    
    @Test
    public void testProjRelativeOutputFile() throws Exception {
	//setup
	File origin = new File("/src/only/src/matters.any.thing");
	Path outputDirectory = new Path(OUTPUT_DIRECTORY);
		
	IProject project = mock(IProject.class);
	when(project.getFile(any(IPath.class))).thenAnswer(new Answer<IFile>() {

	    @Override
	    public IFile answer(InvocationOnMock invocation) throws Throwable {
		actualOutputPath = (IPath) invocation.getArguments()[0];
		return null;
	    }
	});	
	IResource resource = mock(IResource.class);
	when(resource.getProjectRelativePath()).thenReturn(new Path(origin.getName()));
	when(resource.getProject()).thenReturn(project);
	OutputPathHandler resourceHandler = mock(OutputPathHandler.class);
	when(resourceHandler.getProjectRelativeOutputFile(any(IResource.class))).thenCallRealMethod();
	when(resourceHandler.getProjectRelativePath(any(IPath.class))).thenReturn(outputDirectory.append(new Path("/forced/Return")));
	//execute
	resourceHandler.getProjectRelativeOutputFile(resource);
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
	String origin = new File("/src/only/src/matters.any.dsl").getPath();
	String expected = new File(new File(outputDir) ,"/only/src/matters.any.thing").getPath();
	
	Path projRelOriginPath = new Path(origin);	
	OutputPathHandler resourceHandler = new OutputPathHandler(FileType.POPART, OUTPUT_DIRECTORY);
	resourceHandler.setLocalOutputDirectoryName(outputDir);
	//execute
	IPath actual = resourceHandler.getProjectRelativePath(projRelOriginPath);
	//validate
	logger.debug("For origin {} generated {}", origin, actual.toFile());
	assertEquals(getRootFolder(new Path(expected).toFile()), getRootFolder(actual.toFile()));
    }

    
}
