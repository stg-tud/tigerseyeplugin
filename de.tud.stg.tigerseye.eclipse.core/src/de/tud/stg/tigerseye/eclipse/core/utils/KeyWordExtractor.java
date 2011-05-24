package de.tud.stg.tigerseye.eclipse.core.utils;

import groovy.lang.GroovyObject;

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

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.popart.dslsupport.DSL;

/**
 * Extracts Fields and Methods of a {@link DSL} class which represent the
 * keywords of the corresponding language.
 * 
 * @author Leo Roos
 * 
 */
public class KeyWordExtractor {

    private static final Logger logger = LoggerFactory
	    .getLogger(KeyWordExtractor.class);
    private final Class<?> clazz;

    /**
     * @param cl
     *            the DSL class for which methods and Fields will be extracted
     * 
     */
    public KeyWordExtractor(Class<?> cl) {
	this.clazz = cl;
    }

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
    public @Nonnull
    Method[] getMethodKeywords() {
	List<Method> finalMems = getValidMethods();
	return finalMems.toArray(new Method[0]);
    }

    private List<Method> getValidMethods() {
	// Returns all local declarations
	// List<Method> declaredMethods = Arrays.asList(getDSLClazz()
	// .getDeclaredMethods());
	// Returns all public declarations in the hierarchy
	List<Method> declaredMethods = Collections.emptyList();
	// try {
	Method[] methods = getDSLClazz().getDeclaredMethods();// FIXME check if
							      // declaredMethods
							      // only methods()
	    declaredMethods = Arrays.asList(methods);

	List<Method> sortedValidMems = extractValidModifiersSorted(declaredMethods);
	List<Method> finalMems = removeGroovyObjectMethods(sortedValidMems);
	return finalMems;
    }

    @Nonnull
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
    public @Nonnull
    Field[] getDeclaredLiteralKeywords() {
	List<Field> validFields = getValidFieldsForClass();
	return validFields.toArray(new Field[0]);
    }

    List<Field> getValidFieldsForClass() {
	Field[] declaredFields = getDSLClazz().getDeclaredFields();
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
    @Nonnull
    List<Field> removeTimeStampFields(List<Field> sortedFields) {
	List<Field> correctList = new LinkedList<Field>();
	for (Field field : sortedFields) {
	    if (!field.getName().startsWith("__timeStamp")) {
		correctList.add(field);
	    }
	}
	return correctList;
    }

    private <T extends Member> List<T> extractValidModifiersSorted(List<T> mems) {
	List<T> validModifierFields = extractValidMemberFields(mems);
	List<T> sortedFields = sortMembersAlpahbetically(validModifierFields);
	return sortedFields;
    }

    <T extends Member> List<T> extractValidMemberFields(List<T> members) {
	ArrayList<T> validFields = new ArrayList<T>(members.size());
	for (T field : members) {
	    if (isValidModifiersCombination(field.getModifiers())) {
		validFields.add(field);
	    }
	}
	return validFields;
    }

    private boolean isValidModifiersCombination(int modifiers) {
	// XXX if the a valid modifier just has to be public a bitwise and would
	// reduce this method to:
	// return (modifiers & Modifier.PUBLIC) != 0
	return (modifiers == Modifier.PUBLIC)
		|| (modifiers == (Modifier.PUBLIC | Modifier.STATIC))
		|| (modifiers == (Modifier.PUBLIC | Modifier.FINAL))
		|| (modifiers == (Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL))
		|| (modifiers == (Modifier.PUBLIC | Modifier.FINAL | Modifier.NATIVE))
		|| (modifiers == (Modifier.PUBLIC | Modifier.NATIVE));
    }

    <T extends Member> List<T> sortMembersAlpahbetically(List<T> toSort) {
	Collections.sort(toSort, new Comparator<T>() {
	    @Override
	    public int compare(T mem0, T mem1) {
		return mem0.getName().compareTo(mem1.getName());
	    }
	});
	return toSort;
    }

    public Class<?> getDSLClazz() {
	return clazz;
    }

}
