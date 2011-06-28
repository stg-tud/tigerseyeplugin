package de.tud.stg.tigerseye.eclipse.core.runtime;

import java.io.File;

/**
 * Utility class for operations with files.
 * 
 * @author Leo Roos
 * 
 */
public class FileHelper {

    public static boolean isJar(String name) {
	int beginIndex = name.lastIndexOf(".") + 1;
	if (beginIndex < 0 || beginIndex >= name.length()) {
	    return false;
	}
	String extension = name.substring(beginIndex);
	return "jar".equalsIgnoreCase(extension);
    }

    public static boolean isJar(File bundleFile) {
	if (bundleFile.isFile())
	    return isJar(bundleFile.getName());
	else
	    return false;
    }

}
