package de.tud.stg.popart.builder.eclipse;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;

import java.io.File;

import mocks.EclipseMock;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.tud.stg.tigerseye.core.preferences.TigerseyePreferenceInitializer;

@Ignore("No longer up to date with current implementation")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ResourcesPlugin.class })
public class PopartLauncherTest {
 
    private LegacyTigerseyeLaunchShortcut launcher;
    public IResource testfile;
    private EclipseMock eclipseMock;

    private File testProjectRoot = new File("unittests/" 
	    + LegacyTigerseyeLaunchShortcut.class.getPackage().getName()
		    .replaceAll("\\.", "/") + "/resources/");
    private File expectedOutputSrcFolder = new File(testProjectRoot,
	    TigerseyePreferenceInitializer.DEFAULT_OUTPUT_DIRECTORY_NAME);
    private File originalSrcFolder = new File(testProjectRoot, "src");
    private String expectedProcessedFileName;

    
    
    @Before
    public void before() throws Exception {
	// only the implemented method launchGroovy is to be tested
	launcher = mock(LegacyTigerseyeLaunchShortcut.class);
//	doCallRealMethod().when(launcher).launch(
//		any(ICompilationUnit.class), any(IJavaProject.class),
//		anyString());
	//intercept call to doCallSuperLaunchGroovy and verify correct arguments
	doAnswer(new Answer<Object>() {
	    @Override
	    public Object answer(InvocationOnMock invocation) throws Throwable {
		IFile unit = (IFile) invocation.getArguments()[0];
		File relativeTargetFile = new File(unit.getName());
		File rootFile = new File(getRelatvieRootFolder(relativeTargetFile));
		assertEquals("new src folder different from expected",
			expectedOutputSrcFolder.getName(), rootFile.getName());
		String actualtargetFile = relativeTargetFile.getName();
		assertEquals(expectedProcessedFileName, actualtargetFile);
		File resolvableTargetFile = new File(testProjectRoot, relativeTargetFile.getPath());
		assertTrue("targetFile cannot be found" + resolvableTargetFile, resolvableTargetFile.exists());
		return null;
	    }

	}).when(launcher).classToRun();
	eclipseMock = new EclipseMock();
	expectedProcessedFileName = null;
    }

    private void runShortCutTest(File originalRelativeFile, File expectedRelativeFile)
	    throws Exception {
	expectedProcessedFileName = expectedRelativeFile.getName();
	
	File resource = new File(originalSrcFolder, originalRelativeFile.getPath());
	System.out.println("Found resource: " + resource + " "
		+ resource.exists());

	File resourceFile = new File(resource.toURI());

	IFile iFile = eclipseMock.mockFileWithContents(resourceFile
		.getAbsolutePath());
	String projRelPath = originalSrcFolder.getName() + File.separator
		+ originalRelativeFile.getPath();
	System.out.println("ProjRelPath: " + projRelPath);
	Mockito.when(iFile.getProjectRelativePath()).thenReturn(
		new Path(projRelPath));
	IProject iProject = Mockito.mock(IProject.class);
	Mockito.when(iProject.getFile(any(IPath.class))).thenAnswer(
		new Answer<IFile>() {

		    @Override
		    public IFile answer(InvocationOnMock invocation)
			    throws Throwable {
			IPath path = (IPath) invocation.getArguments()[0];
			File file = path.toFile();
			System.out.println("IFile for : " + file);
			IFile mockEmptyFile = eclipseMock.mockEmptyFile(file
				.getAbsolutePath());
			return mockEmptyFile;
		    }

		});
	Mockito.when(iFile.getProject()).thenReturn(iProject);

	ICompilationUnit unit = eclipseMock.mockCompilationUnit(iFile);
	IJavaProject jp = eclipseMock.mockJavaProject();

//	launcher.launchGroovy(unit, jp, "run");
    }

    
    @Test
    public void testPlainJavaFile() throws Exception {
	File file = new File("JavaOrigin.java");
	runShortCutTest(file, file);
    }
    
    @Test
    public void testPlainFile() throws Exception {
	//Should cancel execution since file has no known file extension
	File file = new File("file");
	runShortCutTest(file, file);
//	Mockito.verify(launcher, Mockito.never()).doCallSuperLaunchGroovy(any(IFile.class),
//		any(IJavaProject.class), anyString());
    }
    
    @Test
    public void testDSLFile() throws Exception {
	File original = new File("DSLfile.anydsl.dsl");
	File expected = new File("DSLfile.anydsl.dsl.groovy");
	runShortCutTest(original, expected);
    }
    
    @Test
    public void testGroovyFile() throws Exception {
	File original = new File("Groovyfile.groovy");
	runShortCutTest(original, original);
    }
    
    @Test
    public void testDeeperPackage() throws Exception {
	File original = new File("deeper/pakage/dsl.com.bin.ation.dsl");
	File expected = new File("deeper/pakage/dsl.com.bin.ation.dsl.groovy");
	runShortCutTest(original, expected);
    }
    
 
    private String getRelatvieRootFolder(File file){
	String path = file.getPath();
	int indexOf = path.indexOf(File.separator);
	if(indexOf == 0) {
	    path = path.substring(1);
	}	    
	 String root = path.split(File.separator, 2)[0];
	 return root;
    }
    
}

