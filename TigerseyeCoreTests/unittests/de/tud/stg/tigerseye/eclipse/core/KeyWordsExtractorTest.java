package de.tud.stg.tigerseye.eclipse.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import groovy.lang.GroovyObject;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.ForEachSyntaxDSL;
import de.tud.stg.popart.builder.test.dsls.SetDSL;

public class KeyWordsExtractorTest {


	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetDeclaredLiteralKeywords() {
		Field[] declaredFields = ForEachSyntaxDSL.class.getDeclaredFields();
		List<Field> declaredLiteralKeywords = new KeyWordExtractor(ForEachSyntaxDSL.class)
				.extractValidMemberFields(Arrays.asList(declaredFields));
		assertTrue("expected empty list", declaredLiteralKeywords.isEmpty());
	}

	@Test
	public void testGetDeclaredLiteralKeywordsForLogo() throws Exception {
		Class<?> loadLogoClass = loadLogoClass();
		//Perform steps mixed up
		Field[] allDeclaredFields = loadLogoClass.getDeclaredFields();
		KeyWordExtractor extractor = new KeyWordExtractor(loadLogoClass);
		List<Field> validModifierFields = extractor 
				.extractValidMemberFields(
				Arrays.asList(loadLogoClass.getDeclaredFields()));
		List<Field> removeTimeStampFields = extractor
				.removeTimeStampFields(validModifierFields);
		List<Field> sortedFields = extractor.sortMembersAlpahbetically(removeTimeStampFields);		
		// Check that class has expected amount of fields
		assertEquals(14, allDeclaredFields.length);
		assertEquals(2, validModifierFields.size());
		assertTrue(removeTimeStampFields.isEmpty());
		//Should be same if performed in changed order
		assertEqualLists(sortedFields, extractor.getValidFieldsForClass());
		
	}

	private void assertEqualLists(List<Field> expected,
			List<Field> actual) {
		for (int i = 0; i < expected.size(); i++) {
			assertEquals("Arrays are not equal", expected.get(i), actual.get(i));
		}
		assertEquals("Arrays haven't the same lenght",expected.size(), actual.size());
	}

	private Class<?> loadLogoClass() throws MalformedURLException,
			ClassNotFoundException {
		File LogoDslroot = new File("resources/LogoDSL/");
		URL[] logoCP = { new File(LogoDslroot, "bin").toURI().toURL(),//
				new File(LogoDslroot, "javalogo.jar").toURI().toURL() };
		Class<?> logoDSL = new URLClassLoader(logoCP)
		/*references a class which is not and should not be on the classpath*/
				.loadClass("de.tud.stg.popart.builder.test.logo.LogoDSL");
		return logoDSL;
	}
	
	@Test
	public void testGetDeclaredMethodsLogo() throws Exception {
		Class<?> logoClass = loadLogoClass();
		KeyWordExtractor extractor = new KeyWordExtractor(logoClass);
		List<Method> methods = Arrays.asList(logoClass.getDeclaredMethods());
		List<Method> validMems = extractor.extractValidMemberFields(
		methods);
		List<Method> sortedMems = extractor.sortMembersAlpahbetically(validMems);
		Method[] declaredMethodsFromClass = sortedMems.toArray(new Method[0]);

		Method[] validMethods = declaredMethodsFromClass;
		String expectedKeywords = "backward, bd, clean, cleanscreen, cs, eval, fd, forward, fs, fullscreen, getBlack, getBlue, getDEBUG, getGreen, getRed, getWhite, getYellow, hideturtle, home, ht, left, lt, pd, pendown, penup, pu, right, rt, setDEBUG, setpc, setpencolor, showturtle, st, textscreen, ts";
		assertExpectedMemberNamesContained(validMethods, csvs(expectedKeywords));				
	}

	private void assertExpectedMemberNamesContained(Member[] validMethods,
			String[] expectedMemberNames) {
		ArrayList<String> methodNames = new ArrayList<String>();
		for (Member method : validMethods) {
			methodNames.add(method.getName());
		}			
		for (String string : expectedMemberNames) {
			boolean contains = methodNames.contains(string);
			assertTrue("expected value '" + string + "' not contained in list", contains);
		}
		assertEquals("unexpected number of declared members",expectedMemberNames.length, methodNames.size());
	}
	
	@Test
	public void testGetDeclaredMethodsForeach() throws Exception {
		
		KeyWordExtractor extractor = new KeyWordExtractor(ForEachSyntaxDSL.class);
		List<Method> methods = Arrays.asList(ForEachSyntaxDSL.class.getDeclaredMethods());
		List<Method> validMems = extractor.extractValidMemberFields(methods);
		List<Method> sortedMems = extractor.sortMembersAlpahbetically(validMems); 
		Method[] declaredMethodsFromClass = sortedMems.toArray(new Method[0]);
		Method[] validMethods = declaredMethodsFromClass;
		String expectedKeywords = "eval, forEach, forEach, main";
		assertExpectedMemberNamesContained(validMethods, csvs(expectedKeywords));		
	}
	
	@Test
	public void testRemoveGroovyObjectMethods() throws Exception {
		
		Method[] groovyMeths = GroovyObject.class.getDeclaredMethods();
		KeyWordExtractor extractor = new KeyWordExtractor(GroovyObject.class);
		List<Method> removeGroovyObjectMethods = extractor .removeGroovyObjectMethods(Arrays.asList(groovyMeths));
		assertEquals("expected empty array", 0, removeGroovyObjectMethods.size());
	}
	
	@Test
	public void testRemoveGroovyObjectMethodsWithRest() throws Exception {
		List<Method> groovyMethods = Arrays.asList(GroovyObject.class.getDeclaredMethods());
		List<Method> setMethods = Arrays.asList(SetDSL.class.getDeclaredMethods());
		List<Method> merged = new ArrayList<Method>(setMethods);
		merged.addAll(groovyMethods);		
		assertEquals(setMethods.size()+ groovyMethods. size(), merged.size());
		KeyWordExtractor extractor = new KeyWordExtractor(SetDSL.class);
		List<Method> removeGroovyObjectMethods = extractor .removeGroovyObjectMethods(merged);
		boolean equals = removeGroovyObjectMethods.containsAll(setMethods);
		assertTrue("Expected Array equal to without groovy methods", equals);
		assertEquals(setMethods.size(), removeGroovyObjectMethods.size());
	}

	private String[] csvs(String expectedKeywords) {
		String[] split = expectedKeywords.replaceAll(" ", "").split(",");
		return split;
	}


	@Ignore("No use case hitherto")
	@Test
	public void testAccessorNameToFieldName() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore("No use case hitherto")
	@Test
	public void testHasPopartMain() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore("No use case hitherto")
	@Test
	public void testGetPopartLanguageExtensionsFromStore() {
		fail("Not yet implemented"); // TODO
	}

}
