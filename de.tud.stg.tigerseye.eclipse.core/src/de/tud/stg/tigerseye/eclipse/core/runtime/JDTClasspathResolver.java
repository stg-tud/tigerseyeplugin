package de.tud.stg.tigerseye.eclipse.core.runtime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.CheckForNull;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDTClasspathResolver {

    private static final Logger logger = LoggerFactory
	    .getLogger(JDTClasspathResolver.class);

    private final FileLocatorWrapper fileLocator;

    private final ProjectLinker linker;

    public JDTClasspathResolver(FileLocatorWrapper fileLocator,
	    ProjectLinker linker) {
	this.fileLocator = fileLocator;
	this.linker = linker;
    }

    public JDTClasspathResolver() {
	this(new FileLocatorWrapper(), new ProjectLinker());
    }

    public @CheckForNull
    IClasspathEntry[] resolveClasspathAndLinkProject(Bundle bundle) {
	File bundleFile = getBundleFile(bundle);
	try {
	    IProject project = linker.linkProject(bundleFile.toURI(),
		    bundle.getSymbolicName());
	    project.open(null);
	    return resolveClasspath(project);
	} catch (CoreException e) {
	    logger.warn("Failed to resolve project classpath", e);
	}
	return null;
    }

    public IClasspathEntry[] resolveClasspath(IProject project)
	    throws CoreException {
	IJavaProject javaProject = JavaCore.create(project);
	try {
	    String[] computeDefaultRuntimeClassPath = JavaRuntime
		    .computeDefaultRuntimeClassPath(javaProject);
	    logger.info("Computed Default Classpath is {}",
		    ArrayUtils.toString(computeDefaultRuntimeClassPath));
	    ArrayList<IClasspathEntry> resultList = new ArrayList<IClasspathEntry>();
	    for (String string : computeDefaultRuntimeClassPath) {
		resultList.add(JavaCore.newLibraryEntry(new Path(string), null,
			null));
	    }
	    return resultList.toArray(new IClasspathEntry[0]);
	} catch (CoreException e) {
	    logger.error("resolving problem", e);
	}
	return new IClasspathEntry[0];
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

