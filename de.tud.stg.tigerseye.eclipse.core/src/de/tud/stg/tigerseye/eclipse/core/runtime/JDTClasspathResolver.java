package de.tud.stg.tigerseye.eclipse.core.runtime;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import javax.annotation.CheckForNull;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDTClasspathResolver {

    private static final Logger logger = LoggerFactory
	    .getLogger(JDTClasspathResolver.class);

    private final FileLocatorWrapper fileLocator;

    private final ClassPathDetectorWrapper detector;

    private final ProjectLinker linker;

    public JDTClasspathResolver(FileLocatorWrapper fileLocator,
	    ClassPathDetectorWrapper detector, ProjectLinker linker) {
	this.fileLocator = fileLocator;
	this.detector = detector;
	this.linker = linker;
    }

    public JDTClasspathResolver() {
	this(new FileLocatorWrapper(), new ClassPathDetectorWrapper(),
		new ProjectLinker());
    }

    public @CheckForNull
    File[] resolveClasspath(Bundle bundle) {
	File bundleFile = getBundleFile(bundle);
	try {
	    IProject project = linker.linkProject(bundleFile.toURI(),
		    bundle.getSymbolicName());
	    project.open(null);


	    IJavaProject javaProject = JavaCore.create(project);

	    IClasspathEntry[] detectedClasspath = javaProject
		    .getResolvedClasspath(false);

	    // IClasspathEntry[] detectedClasspath = detector
	    // .getClasspath(project);

	    URI projectLocationURI = project.getLocationURI();
	    IPath outputLocation = javaProject.getOutputLocation();

	    File absoluteOutputFolder = new File(new File(projectLocationURI),
		    outputLocation.removeFirstSegments(1).toOSString());

	    IPath projectLoc = project.getLocation();
	    String projectName = projectLoc.lastSegment();

	    ArrayList<File> resultClassPath = new ArrayList<File>();
	    resultClassPath.add(absoluteOutputFolder);
	    for (int i = 0; i < detectedClasspath.length; i++) {
		IPath cpPath = detectedClasspath[i].getPath();
		String cpAsString = cpPath.toString();
		// boolean isSource = IPackageFragmentRoot.K_SOURCE ==
		// detectedClasspath[i].getContentKind();
		int entryKind = detectedClasspath[i].getEntryKind();
		boolean isSource = IClasspathEntry.CPE_SOURCE == entryKind;

		if (!isSource && projectName.equals(cpPath.segment(0)))
		    resultClassPath.add(new File(new File(projectLocationURI),
			    cpPath.removeFirstSegments(1).toOSString()));
	    }
	    // project.close(null);
	    // project.delete(false, true, null);
	    return resultClassPath.toArray(new File[0]);
	} catch (CoreException e) {
	    logger.warn("Failed to resolve project classpath", e);
	}
	return null;
    }


    private File getBundleFile(Bundle bundle) {
	try {
	    File bundleFile = fileLocator.getBundleFile(bundle);
	    return bundleFile;
	} catch (IOException e) {
	    logger.warn("Could not determine classpath for bundle {}.", bundle,
		    e);
	    return null;
	}
    }

}

