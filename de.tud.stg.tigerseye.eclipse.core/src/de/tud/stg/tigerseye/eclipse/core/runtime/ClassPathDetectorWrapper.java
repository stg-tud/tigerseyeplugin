package de.tud.stg.tigerseye.eclipse.core.runtime;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.internal.ui.wizards.ClassPathDetector;

/**
 * Thin wrapper around the internal {@link ClassPathDetector} class
 * 
 * @author Leo Roos
 * 
 */
@SuppressWarnings("restriction")
public class ClassPathDetectorWrapper {

    public IClasspathEntry[] getClasspath(IProject project)
	    throws CoreException {
	    ClassPathDetector detector = new ClassPathDetector(project, null);
	    IClasspathEntry[] classpath = detector.getClasspath();
	    return classpath;
    }

}
