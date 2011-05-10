package de.tud.stg.tigerseye.eclipse.core.runtime;

/**
 * General utility class for operations with files.
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

}
