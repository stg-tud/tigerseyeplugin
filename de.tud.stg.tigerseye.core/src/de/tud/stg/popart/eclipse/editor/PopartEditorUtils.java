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

import de.tud.stg.tigerseye.core.DSLDefinition;
import de.tud.stg.tigerseye.core.DSLKey;
import de.tud.stg.tigerseye.core.NoLegalPropertyFound;
import de.tud.stg.tigerseye.core.TigerseyeCore;

/**
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

	// -TD-2009-12-01-BEGIN
	// remove none keywords
    /**
     * TODO move to language processing class.
     * 
     * Return a list with all none keyword methods, i.e. all methods that are
     * inherited from GroovyObject.
     * 
     * @return a list with all none keyword methods.
     */
	private static Method[] getNoneKeywordMethods(String externalClassPath) {
		// XXX is Groovy the only case to consider?
		return GroovyObject.class.getMethods();
	}

    /**
     * TODO move to language processing class.
     * 
     * Remove the elements of the right array in the left array. This method is
     * used to remove framework methods from extending sub-classes.
     */
	private static Method[] removeRightMethodsFromLeftMethods(Method[] left,
			Method[] right) {

	// XXX inconsistent: the logging messages are domain specific although
	// the method name is generic
		if ((right == null) || (right.length == 0)) {
			logger.debug("No \"None Keyword Methods\". Nothing to remove.");
			return null;
		}
	if ((left == null) || (left.length < right.length)) {
	    logger.debug("Could not find none keyword methods. List of methods is smaller than the list of the Object class. Nothing to remove.");
	    return null;
	}
		if (logger.isDebugEnabled()) {
			logger.debug("removeRMFLM: right= {}", Arrays.toString(right));
			logger.debug("removeRMFLM: left= {}", Arrays.toString(left));
		}


		List<Method> leftList = Arrays.asList(left);
		List<Method> rightList = Arrays.asList(right);
		List<Method> correctList = new LinkedList<Method>();

		for (Method leftMethod : leftList) {
			boolean contains = false;
			for (Method rightMethod : rightList) {
				if (leftMethod.getName().equals(rightMethod.getName())) {
					contains = true;
				}
			}
			if (!contains) {
				correctList.add(leftMethod);
			}
		}

		Method[] correctMethods = new Method[correctList.size()];
		correctList.toArray(correctMethods);
		if (logger.isDebugEnabled()) {
			logger.debug(
					"Methods without \"None Keywords\" i.e. correct methods: {}",
					Arrays.toString(correctMethods));
		}
		return correctMethods;
	}

    /**
     * TODO move to language processing class.
     * 
     * Remove the time stamp fields. This method is used to remove time stamp
     * Groovy fields.
     */
	private static Field[] removeTimeStampFields(Field[] fields) {
		logger.debug("fields: {}", Arrays.toString(fields));

		List<Field> fieldList = Arrays.asList(fields);
		List<Field> correctList = new LinkedList<Field>();

		for (Field field : fieldList) {
			if (!field.getName().startsWith("__timeStamp")) {
				correctList.add(field);
			}
		}

		Field[] correctFields = new Field[correctList.size()];
		correctList.toArray(correctFields);
		if (logger.isDebugEnabled()) {
			logger.debug("correctFields= {}", Arrays.toString(correctFields));
		}
		return correctFields;
	}

	// -TD-2009-12-01-END

    /**
     * TODO move to language processing class.
     * 
     * This method returns public declared keyword methods for given language in
     * the class path. Only keyword methods are returned, i.e., not all public
     * methods are returned.
     * 
     * @param contributorSymbolicName
     *            ?
     * @param externalClassPath
     *            the path from a class with methods that should be found.
     */
	public static Method[] printDeclaredMethodKeywords(
			String contributorSymbolicName, String externalClassPath) {
		// -TD-2009-12-01-BEGIN
		// Changed so that none keywords are removed
		Method[] methods = getDeclaredMethods(contributorSymbolicName,
				externalClassPath);
		Method[] frameworkMethods = getNoneKeywordMethods(externalClassPath);
		Method[] correctMethods = removeRightMethodsFromLeftMethods(methods,
				frameworkMethods);
		if (correctMethods == null) {
			correctMethods = new Method[0];
		}
		return correctMethods;
		// -TD-2009-12-01-END
	}

	// -TD-2009-12-02-BEGIN
    /**
     * TODO this is not a special editor utility should be moved to some
     * language processing class or into a new one.
     * 
     * This method returns public declared keyword literals for given language
     * in the class path. Only keyword fields are returned, i.e., not all public
     * methods are returned.
     * 
     * @param externalClassPath
     *            the path from a class with methods that should be found.
     */
	public static Field[] getDeclaredLiteralKeywords(
			String contributorSymbolicName, String externalClassPath) {
		Field[] fields = getDeclaredFields(contributorSymbolicName,
				externalClassPath);
		Field[] correctFields = removeTimeStampFields(fields);
		return correctFields;
	}

	// -TD-2009-12-02-END

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

	// -TD-2009-12-01-BEGIN
	// remove none keywords
	/**
	 * Returns a list of all declared methods of a class by its class name.
	 *
	 * @param contributorSymbolicName
	 *            The full class name.
	 * @param externalClassPath
	 *            The class path in which the class must be contained.
	 * @return The list of all declared methods of the class or
	 *         <code>null</code> if class could not be found.
	 */
    private static @CheckForNull
    Method[] getDeclaredMethods(@Nonnull String contributorSymbolicName,
	    @Nonnull String externalClassPath) {
		Class<?> cl = loadClassChecked(contributorSymbolicName,
				externalClassPath);
		if (cl == null) {
			return null;
		}
		return getDeclaredMethodsFromClass(cl);
	}

	/**
	 * Returns a list of all declared fields of a class by its class name.
	 *
	 * @param contributorSymbolicName
	 *            The full class name.
	 * @param externalClassPath
	 *            The class path in which the class must be contained.
	 * @return The list of all declared fields of the class.
	 */
	private static Field[] getDeclaredFields(String contributorSymbolicName,
			String externalClassPath) {

		logger.info(contributorSymbolicName);

		if (externalClassPath == null) {
			return null;
		}

		Class<?> cl = null;
		cl = loadClassChecked(contributorSymbolicName, externalClassPath);
		if (cl == null) {
			return null;
		}
		return getDeclaredFieldsFromClass(cl);
	}

	// -TD-2009-12-01-END

	/**
	 * Returns a list of all declared methods of a class.
	 *
	 * @param clazz
	 *            The class.
	 * @return The list of all declared methods of the class.
	 */
	private static Method[] getDeclaredMethodsFromClass(Class<?> clazz) {

		logger.info("Plug-in introspects methods of class: " + clazz.getName());

		for (int i = 0; i < clazz.getInterfaces().length; i++) {
			logger.info("Ifc[" + i + "]: "
					+ (clazz.getInterfaces())[i].getName());
		}
		if (clazz.getSuperclass() != null) {
			logger.info("Superclass: " + clazz.getSuperclass().getName());
		}

		ArrayList<Method> publicDeclaredMethodsList = new ArrayList<Method>();

		for (Method declaredMethod : clazz.getDeclaredMethods()) {
			if (isValidModifiersCombination(declaredMethod.getModifiers())) {
				publicDeclaredMethodsList.add(declaredMethod);
			}
		}

		Collections.sort(publicDeclaredMethodsList, new MethodsComparator());

		Method[] publicDeclaredMethods = publicDeclaredMethodsList
				.toArray(new Method[0]);

		if (logger.isDebugEnabled()) {
			StringBuilder prettyPrintMember = prettyPrintMember(
					publicDeclaredMethods, "detected method keywords {");
			logger.trace(prettyPrintMember.toString());
		}

		return publicDeclaredMethods;
	}

	private static StringBuilder prettyPrintMember(
			Member[] publicDeclaredMethods, String initMsg) {
		StringBuilder sb = new StringBuilder(initMsg).append("{");
		for (int i = 0; i < publicDeclaredMethods.length; i++) {
			if (i != 0) {
				sb.append(", ");
			}
			sb.append(publicDeclaredMethods[i].getName());
		}
		sb.append("}");
		return sb;
	}

	private static boolean isValidModifiersCombination(int modifiers) {
		return (modifiers == Modifier.PUBLIC)
				|| (modifiers == (Modifier.PUBLIC | Modifier.STATIC))
				|| (modifiers == (Modifier.PUBLIC | Modifier.FINAL))
				|| (modifiers == (Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL))
				|| (modifiers == (Modifier.PUBLIC | Modifier.FINAL | Modifier.NATIVE))
				|| (modifiers == (Modifier.PUBLIC | Modifier.NATIVE));
	}

	/**
	 * Returns a list of all declared fields of a class.
	 *
	 * @param cl
	 *            The class.
	 * @return The list of all declared fields of the class.
	 */
	private static Field[] getDeclaredFieldsFromClass(Class<?> cl) {

		logger.info("Plug-in introspects fields of class: " + cl.getName());
		for (int i = 0; i < cl.getInterfaces().length; i++) {
			logger.info("Ifc[" + i + "]: " + (cl.getInterfaces())[i].getName());
		}
		if (cl.getSuperclass() != null) {
			logger.info("Superclass: " + cl.getSuperclass().getName());
		}

		ArrayList<Field> publicDeclaredFieldsList = new ArrayList<Field>();

		// Read all public declared fields from external class
		Field[] declaredFields = cl.getDeclaredFields();
		for (Field declaredField : declaredFields) {
			if (isValidModifiersCombination(declaredField.getModifiers())) {
				publicDeclaredFieldsList.add(declaredField);
			}
		}

		Collections.sort(publicDeclaredFieldsList, new Comparator<Field>() {
			@Override
			public int compare(Field field0, Field field1) {
				return field0.getName().compareTo(field1.getName());
			}
		});

		Field[] publicDeclaredFields = new Field[publicDeclaredFieldsList
				.size()];
		publicDeclaredFieldsList.toArray(publicDeclaredFields);

		logger.info("Superclass: " + cl.getSuperclass().getName());
		StringBuilder prettyPrintMember = prettyPrintMember(
				publicDeclaredFields,
 "Detected field keywords ");
		logger.info(prettyPrintMember.toString());

		return publicDeclaredFields;
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