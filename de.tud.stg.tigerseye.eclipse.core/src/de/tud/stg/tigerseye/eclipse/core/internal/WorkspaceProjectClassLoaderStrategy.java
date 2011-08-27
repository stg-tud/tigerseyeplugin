package de.tud.stg.tigerseye.eclipse.core.internal;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.api.ClassLoaderStrategy;

public class WorkspaceProjectClassLoaderStrategy implements ClassLoaderStrategy {

    private static final Logger logger = LoggerFactory
	    .getLogger(WorkspaceProjectClassLoaderStrategy.class);

    private final IProject workspaceProject;

    public WorkspaceProjectClassLoaderStrategy(IProject workspaceProject) {
	this.workspaceProject = workspaceProject;

    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
	URLClassLoader classLoader = getClassLoaderWithIndependentContext();
	Class<?> loadedClass = classLoader.loadClass(className);
	assertNotOnPluginClasspath(className);
	return loadedClass;
    }

    private void assertNotOnPluginClasspath(String className) {
	try {
	    Class<?> loadClass = Class.forName(className, false, getClass()
		    .getClassLoader());
	    if (loadClass != null) {
		logger.info(
			"did not expect class {} to be loadable by the tigerseye plugin."
				+ "This means the class can not be updated and consequently no changes of that class can be considered by the transformation process.",
			loadClass.toString());
	    }
	} catch (ClassNotFoundException e) {
	    // $EXPECTED EXCEPTION$
	    /*
	     * I see no possibility to check if a class is actually on the
	     * classpath other than trying to load it.
	     */
	}
    }

    private URLClassLoader getClassLoaderWithIndependentContext()
	    throws ClassNotFoundException {
	IJavaProject javaProject = JavaCore.create(workspaceProject);
	URL[] urls = computeDefaultRuntimeClasspathAsURLs(javaProject);
	/*
	 * WARNING: When planning to modify the following logic, bear in mind
	 * some subtleties. The loaded class might easily end up in the
	 * wrong/unexpected class loader context. That again will lead to a
	 * situation where classes of the same name might not be considered
	 * equal and hence Java Reflections won't work. For example "new
	 * URLClassloader(urls, parentClassloader)" is not equal to
	 * "URLClassloader.newInstance" with the same arguments, since the
	 * newInstance method also configures the correct classloader Context.
	 * You must also always define the parent classloader as the one which
	 * handles the classes of this plug-in (or more specifically the classes
	 * that want to access the loaded class via reflections). Otherwise a
	 * default classloader context will be used, which in Eclipses' plug-in
	 * environment is usually not of the same context as the classloader of
	 * this plug-in (=bundle).
	 */
	URLClassLoader classLoader = URLClassLoader.newInstance(urls,
		getClass().getClassLoader());
	return classLoader;
    }

    private URL[] computeDefaultRuntimeClasspathAsURLs(IJavaProject javaProject)
	    throws ClassNotFoundException {
	File[] resolveClasspath;
	try {
	    String[] computeDefaultRuntimeClassPath = JavaRuntime
		    .computeDefaultRuntimeClassPath(javaProject);
	    resolveClasspath = new File[computeDefaultRuntimeClassPath.length];
	    for (int i = 0; i < computeDefaultRuntimeClassPath.length; i++) {
		resolveClasspath[i] = new File(
			computeDefaultRuntimeClassPath[i]);
	    }
	} catch (CoreException e) {
	    throw new ClassNotFoundException(
		    "Failed to resolve classpath for project "
			    + workspaceProject, e);
	}
	List<URL> urlList = new ArrayList<URL>();
	for (int i = 0; i < resolveClasspath.length; i++) {
	    File entry = resolveClasspath[i];
	    try {
		URL url = entry.toURI().toURL();
		urlList.add(url);
	    } catch (MalformedURLException e) {
		throw new ClassNotFoundException(
			"Failed to transform classpath location file " + entry
				+ " to an URL.", e);
	    }
	}
	URL[] urls = urlList.toArray(new URL[0]);
	return urls;
    }

}
