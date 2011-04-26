package de.tud.stg.popart.eclipse.core.debug;

import org.eclipse.core.resources.IFile;

/**
 * Utility class that currently does nothing more than holding the
 * currently debugged Popart-IFile.
 *
 * @author David Marx
 * @author Thorsten Peter
 */
public class PopartDebugUtils {
	
	private static IFile popartSourceFile;
	private static IFile groovyTempFile;
	public static String className;
	
	/**
	 * Returns the Popart source file.
	 * 
	 * @return The Popart source file
	 */
	public static IFile getPopartSourceFile() {
		return PopartDebugUtils.popartSourceFile;
	}
	
	/**
	 * Sets the popart source file.
	 * 
	 * @param groovyTempFile The Popart source file
	 */
	public static void setPopartSourceFile(IFile popartSourceFile) {
		PopartDebugUtils.popartSourceFile = popartSourceFile;
	}
	
	/**
	 * Returns the Groovy temp file.
	 * 
	 * @return The Groovy temp file
	 */
	public static IFile getGroovyTempFile() {
		return PopartDebugUtils.groovyTempFile;
	}
	
	/**
	 * Sets the Groovy temp file.
	 * 
	 * @param groovyTempFile The Groovy temp file
	 */
	public static void setGroovyTempFile(IFile groovyTempFile) {
		PopartDebugUtils.groovyTempFile = groovyTempFile;
	}
	
	/**
	 * Returns the class name of the currently running Popart language class.
	 * 
	 * @return class name
	 */
	public static String getClassName() {
		return className;
	}

	/**
	 * Sets the class name of the currently running Popart language class.
	 * 
	 * @param className The class name
	 */
	public static void setClassName(String className) {
		PopartDebugUtils.className = className;
	}
	
}
