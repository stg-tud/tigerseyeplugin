package de.tud.stg.popart.eclipse.editor;

import groovy.lang.GroovyObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.DSLKey;
import de.tud.stg.tigerseye.eclipse.core.NoLegalPropertyFound;
import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;

/**
 * TODO this is not a special editor utility should be moved to some language
 * processing class or into a new one.
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


    /**
     * 
     * This method returns public declared keyword methods for given language in
     * the class path. Only keyword methods are returned, i.e., not all public
     * methods are returned, in particular methods inherited from GroovyObject
     * are removed.
     * 
     * @param contributorSymbolicName
     *            the bundle name of the DSL contributing plug-in
     * @param externalClassPath
     *            the path from a class with methods that should be found.
     */
    public static Method[] getMethodKeywords(String contributorSymbolicName,
	    String externalClassPath) {
	Class<?> cl = loadClassChecked(contributorSymbolicName,
		externalClassPath);
	if (cl == null) {
	    return new Method[] {};
	}
	List<Method> finalMems = getValidMethods(cl);
	return finalMems.toArray(new Method[0]);
    }

    private static List<Method> getValidMethods(Class<?> cl) {
	List<Method> declaredMethods = Arrays.asList(cl.getDeclaredMethods());
	List<Method> sortedValidMems = extractValidModifiersSorted(declaredMethods);
	List<Method> finalMems = removeGroovyObjectMethods(sortedValidMems);
	return finalMems;
    }

    static @Nonnull
    List<Method> removeGroovyObjectMethods(List<Method> sortedMems) {
	Method[] groovyObjectMethods = GroovyObject.class.getMethods();
	List<Method> noGroovyMems = new ArrayList<Method>(sortedMems);
	boolean removed = noGroovyMems.removeAll(Arrays
		.asList(groovyObjectMethods));
	logger.trace(removed ? "Removed Groovy methods"
		: "No Groovy methods to remove.");
	return noGroovyMems;
    }

    /**
     * 
     * 
     * This method returns public declared keyword literals for given language
     * in the class path. Only keyword fields are returned, i.e., not all public
     * methods are returned.
     * 
     * @param externalClassPath
     *            the path from a class with methods that should be found.
     */
    public static @Nonnull
    Field[] getDeclaredLiteralKeywords(@Nonnull String contributorSymbolicName,
	    @Nonnull String externalClassPath) {
	Class<?> cl = loadClassChecked(contributorSymbolicName,
		externalClassPath);
	if (cl == null) {
	    return new Field[0];
	}
	List<Field> validFields = getValidFieldsForClass(cl);
	return validFields.toArray(new Field[0]);
    }

    static List<Field> getValidFieldsForClass(Class<?> cl) {
	Field[] declaredFields = cl.getDeclaredFields();
	List<Field> sortedFields = extractValidModifiersSorted(Arrays
		.asList(declaredFields));
	List<Field> noTimeStampFields = removeTimeStampFields(sortedFields);
	return noTimeStampFields;
    }

    /**
     * 
     * Remove the time stamp fields. This method is used to remove time stamp
     * Groovy fields.
     */
    static @Nonnull
    List<Field> removeTimeStampFields(List<Field> sortedFields) {
        List<Field> correctList = new LinkedList<Field>();
        for (Field field : sortedFields) {
            if (!field.getName().startsWith("__timeStamp")) {
        	correctList.add(field);
            }
        }
        return correctList;
    }

    private static <T extends Member> List<T> extractValidModifiersSorted(
	    List<T> mems) {
	List<T> validModifierFields = extractValidMemberFields(mems);
	List<T> sortedFields = sortMembersAlpahbetically(validModifierFields);
	return sortedFields;
    }

    static <T extends Member> List<T> extractValidMemberFields(List<T> members) {
	ArrayList<T> validFields = new ArrayList<T>(members.size());
	for (T field : members) {
	    if (isValidModifiersCombination(field.getModifiers())) {
		validFields.add(field);
	    }
	}
	return validFields;
    }

	private static boolean isValidModifiersCombination(int modifiers) {
	// XXX if the a valid modifier just has to be public a bitwise and would
	// reduce this statement:
	// return (modifiers & Modifier.PUBLIC) != 0
	return (modifiers == Modifier.PUBLIC)
		|| (modifiers == (Modifier.PUBLIC | Modifier.STATIC))
		|| (modifiers == (Modifier.PUBLIC | Modifier.FINAL))
		|| (modifiers == (Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL))
		|| (modifiers == (Modifier.PUBLIC | Modifier.FINAL | Modifier.NATIVE))
		|| (modifiers == (Modifier.PUBLIC | Modifier.NATIVE));
	}

    static <T extends Member> List<T> sortMembersAlpahbetically(List<T> toSort) {
	Collections.sort(toSort, new Comparator<T>() {
	    @Override
	    public int compare(T mem0, T mem1) {
		return mem0.getName().compareTo(mem1.getName());
	    }
	});
	return toSort;
    }

    /**
     * This method checks whether a class for a giving class-path exists or not.
     * 
     * @param classPath
     *            class-path to a checkable class.
     * @return the loaded class for <code>classPath</code> if it exists and
     *         <code>null</code> otherwise.
     */
    private static @CheckForNull
    Class<?> loadClassChecked(@Nonnull String contributorSymbolicName,
	    @Nonnull String classPath) {
	try {
	    Bundle bundle = Platform.getBundle(contributorSymbolicName);
	    Class<?> loadClass = bundle.loadClass(classPath);
	    return loadClass;
	} catch (ClassNotFoundException e) {
	    logger.error("Class not found for {} in ", new Object[] {
		    classPath, contributorSymbolicName }, e);
	}
	return null;
    }

    // /**
    // * This method returns the class-path from store for a given IFile.
    // *
    // * @param openedFile
    // * the IFile, whose extension is important for retrieving the
    // * class-path from store.
    // * @return the class-path from store for IFiles extension.
    // */
    // public static String getClassPathForIFile(IFile openedFile) {
    //
    // return getClassPathForFile(openedFile.getName());
    // }
    //
    // public static String getClassPathForFile(String filename) {
    // IPreferenceStore store = TigerseyeCore.getPreferences();
    // if (!store
    // .contains(TigerseyePreferenceConstants.POPART_EDITOR_NUMBER_OF_LANGUAGES))
    // {
    // logger.error(TigerseyePreferenceConstants.POPART_EDITOR_NUMBER_OF_LANGUAGES
    // + " did not store in store");
    // store.setDefault(
    // TigerseyePreferenceConstants.POPART_EDITOR_NUMBER_OF_LANGUAGES,
    // 0);
    // }
    //
    // // Get number of languages
    // int numberOfLanguages = store
    // .getInt(TigerseyePreferenceConstants.POPART_EDITOR_NUMBER_OF_LANGUAGES);
    //
    // // for every language started by ONE !!! until number of languages
    // // included !!!
    // for (int languageId = 1; languageId <= numberOfLanguages; languageId++) {
    // String extensionKey = TigerseyePreferenceConstants.POPART_LANGUAGE_KEY_
    // + languageId + TigerseyePreferenceConstants._EXTENSION;
    //
    // if (store.contains(extensionKey)
    // && (store.getString(extensionKey) != null)) {
    //
    // // Get extension for new language
    // String popartLanguageExtension = store.getString(extensionKey);
    //
    // // Get file name of the opened file
    // String openedFileName = filename;
    //
    // // If the name of the opened file ends with the extension from
    // // store concatenated with .dsl
    // if (openedFileName.endsWith("." + popartLanguageExtension
    // + ".dsl")) {
    //
    // // If store contains ClassPath and extension for language
    // String classPathKey = TigerseyePreferenceConstants.POPART_LANGUAGE_KEY_
    // + languageId
    // + TigerseyePreferenceConstants._CLASS_PATH;
    // if (store.contains(classPathKey)) {
    // // Get ClassPath for new language
    // String classPath = store.getString(classPathKey);
    //
    // return classPath;
    // }
    // }
    // }
    // }
    // return null;
    // }

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
     * This method checks whether the given IFile is suitable for running like
     * Popart class or not.
     * 
     * @param file
     *            the IFiele that should be checked.
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
    public static List<String> getPopartLanguageExtensionsFromStore() {
	List<String> extensions = new ArrayList<String>();
	List<DSLDefinition> dslDefinitions = TigerseyeCore
		.getLanguageProvider().getDSLDefinitions();
	for (DSLDefinition dslDefinition : dslDefinitions) {
	    try {
		String value = dslDefinition.getValue(DSLKey.EXTENSION);
		extensions.add(value);
	    } catch (NoLegalPropertyFound e) {
		logger.debug("no extension found for dslDefinition {} ",
			dslDefinition, e);
	    }
	}
	return extensions;
    }

}