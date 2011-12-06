package de.tud.stg.tigerseye.eclipse.core.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.api.DSLKey;
import de.tud.stg.tigerseye.eclipse.core.api.NoLegalPropertyFoundException;

/**
 * TODO this class is no longer used but could be used again? If you know that
 * that is no longer the case please delete this class.
 * 
 * PopartEditorUtils defines useful methods such as reading a file, getting
 * values from store and some others methods, that are used more time over the
 * whole plug-in.
 * 
 * @author Yevgen Fanshil
 * @author Leonid Melnyk
 * @author Tom Dinkelaker
 * @author Leo Roos
 */
public class PopartEditorUtils {
	private static final Logger logger = LoggerFactory
			.getLogger(PopartEditorUtils.class);


    public static String accessorNameToFieldName(String getterName) {
    if ((!getterName.startsWith("get")) && (!getterName.startsWith("set"))) {
    		throw new IllegalArgumentException(
    				"An accessor must start with 'get' or 'set'.");
    }
    
    	String name = getterName.substring(3);
    if (name.isEmpty()) {
    		return name;
    }
    
    	String firstLetter = name.substring(0, 1); // first CamelCase letter
    												// after get/set
    	String remainingLetters = name.substring(1); // the remaining letters
    													// after get/set and the
    													// first CamelCase
    													// letter
    	String litralKeywordName = firstLetter.toLowerCase() + remainingLetters;
    	return litralKeywordName;
    }

    
	            /**
     * This method checks whether the given IFile is suitable for running like a
     * Popart class or not.
     * <p>
     * FIXME(leo;06.12.2011)
     * This implementation is just a heuristic on plain text and may fail if by
     * accident actual code uses the same keywords that are expected here.  
     * 
     * @param file
     *            the IFile that should be checked.
     * @return <code>true</code> if <code>file</code> has popart type main
     */
	public static boolean hasPopartMain(IFile file) {

		InputStream inStream = null;
		try {
			inStream = file.getContents();
	} catch (CoreException e2) {
	    logger.warn("Failed to determine if file is a dsl main class", e2);
	    return false; // Although absolutely sure
	}

		BufferedReader bufferedReader = null;
		try {
	    bufferedReader = new BufferedReader(new InputStreamReader(inStream));
	    String line = null;
			while ((line = bufferedReader.readLine()) != null) {

				if (line.trim().startsWith("/") || line.trim().startsWith("*")
			|| line.trim().equals("")) {
					continue;
		}

				List<String> popartLanguageExtensionsFromStore = getPopartLanguageExtensionsFromStore();

				for (String extension : popartLanguageExtensionsFromStore) {

		    if (line.trim().contains(extension + "(name:")) {
						return true;
		    }
				}
			}
		} catch (IOException e1) {
			logger.warn("Generated log statement", e1);
	} finally {
	    IOUtils.closeQuietly(bufferedReader);
		}

		return false;
	}

    /**
     * This method returns a list of all saved Popart extensions from store.
     * 
     * @return a list of all stored Popart extensions.
     */
    private static List<String> getPopartLanguageExtensionsFromStore() {
	List<String> extensions = new ArrayList<String>();
	Collection<DSLDefinition> dslDefinitions = TigerseyeCore
		.getLanguageProvider().getDSLDefinitions();
	for (DSLDefinition dslDefinition : dslDefinitions) {
	    try {
		String value = dslDefinition.getValue(DSLKey.EXTENSION);
		extensions.add(value);
	    } catch (NoLegalPropertyFoundException e) {
		logger.debug("no extension found for dslDefinition {} ",
			dslDefinition, e);
	    }
	}
	return extensions;
    }

}