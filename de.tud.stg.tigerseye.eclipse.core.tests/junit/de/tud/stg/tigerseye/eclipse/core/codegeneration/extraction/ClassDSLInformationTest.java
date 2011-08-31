package de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction;

import static de.tud.stg.tigerseye.eclipse.core.utils.CustomFESTAssertions.assertThat;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static utilities.TestUtils.collectionPrettyPrint;
import static utilities.TigerseyeAssert.assertContainsExactly;
import static utilities.TigerseyeAssert.assertEmpty;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.ClassDSLInformation.ComparableMethod;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars.HostLanguageGrammar;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.ClassWithSameMethodsAsOther1;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.ClassWithSameMethodsAsOther2;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.MathDSL4GrammarBuilderTest;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.NoPublicMethodsGroovyClassForExtractorTest;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.NoPublicMethodsJavaClass;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.NotAnnotatedClassForExtractorTest;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.SdfDSLForExtractingTest;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.SpecialMethodNamesClassForDSLInformationTestGroovy;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.SpecialMethodNamesClassForDSLInformationTestJava;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.WaterSupportedFalseAnnotatedForExtractorTest;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ConfigurationOptions;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.TypeHandler;
import de.tud.stg.tigerseye.util.ListBuilder;

public class ClassDSLInformationTest {

	String[] expectedSDFMethodsToBeExtracted = { //
	"getGrammar(String,boolean)",//
			"eval(HashMap,Closure)",//
			"moduleWithoutParameters(ModuleId,Imports[],ExportOrHiddenSection[])",//
			"moduleWithParameters(ModuleId,Symbol[],Imports[],ExportOrHiddenSection[])",//
			"parseString(String,String)",//
			"printGeneratedGrammarHTML(String,String)",//
			"caseSensitiveLiteralSymbol(String)",//
			"caseInsensitiveLiteralSymbol(CaseInsensitiveLiteralSymbol)",//
			"caseInsensitiveLiteralSymbol(String)",//
			"sortSymbol(String)",//
			"characterClassSymbol(String)",//
			"characterClassComplement(CharacterClassSymbol)",//
			"characterClassDifference(CharacterClassSymbol,CharacterClassSymbol)",//
			"characterClassIntersection(CharacterClassSymbol,CharacterClassSymbol)",//
			"characterClassUnion(CharacterClassSymbol,CharacterClassSymbol)",//
			"optionalSymbol(Symbol)",//
			"repetitionSymbolAtLeastZero(Symbol)",//
			"repetitionSymbolAtLeastOnce(Symbol)",//
			"sequenceSymbol(Symbol[])",//
			"listSymbolAtLeastZero(Symbol,Symbol)",//
			"listSymbolAtLeastOnce(Symbol,Symbol)",//
			"alternativeSymbol(Symbol,Symbol)",//
			"tupleSymbol(Symbol[])",//
			"functionSymbol(Symbol[],Symbol)",//
			"labelledSymbol(Symbol,String)",//
			"symbol(SortSymbol)",//
			"symbol(LiteralSymbol)",//
			"symbol(AlternativeSymbol)",//
			"symbol(ListSymbol)",//
			"symbol(OptionalSymbol)",//
			"symbol(RepetitionSymbol)",//
			"symbol(SequenceSymbol)",//
			"symbol(CharacterClass)",//
			"characterClass(CharacterClassSymbol)",//
			"characterClass(CharacterClassComplement)",//
			"characterClass(CharacterClassDifference)",//
			"characterClass(CharacterClassIntersection)",//
			"characterClass(CharacterClassUnion)",//
			"symbol(TupleSymbol)",//
			"symbol(FunctionSymbol)",//
			"exports(GrammarElement[])",//
			"hiddens(GrammarElement[])",//
			"exportOrHiddenSection(Exports)",//
			"exportOrHiddenSection(Hiddens)",//
			"importsStatement(Import[])",//
			"importModuleWithoutParameters(ModuleId)",//
			"importModuleWithParameters(ModuleId,Symbol[])",//
			"importModuleWithRenamings(ModuleId,Renaming[])",//
			"importModuleWithParametersAndRenamings(ModuleId,Symbol[],Renaming[])",//
			"renaming(Symbol,Symbol)",//
			"sortsDeclaration(SortSymbol[])",//
			"lexicalSyntax(Production[])",//
			"contextFreeSyntax(Production[])",//
			"lexicalStartSymbols(Symbol[])",//
			"contextFreeStartSymbols(Symbol[])",//
			"aliases(Alias[])",//
			"alias(Symbol,Symbol)",//
			"production(Symbol[],Symbol)",//
			"production(Symbol,String)",//
			"grammarElement(Imports)",//
			"grammarElement(Sorts)",//
			"grammarElement(StartSymbols)",//
			"grammarElement(Syntax)",//
			"startSymbols(ContextFreeStartSymbols)",//
			"startSymbols(LexicalStartSymbols)",//
			"syntax(ContextFreeSyntax)",//
			"syntax(LexicalSyntax)",//
			"syntax(Aliases)",//
	};
	private Pattern nameMatcher = Pattern.compile("(\\w+)\\(.*?\\)");

	private Pattern parameterMatcher = Pattern.compile("\\((.*?)\\)");

	private ClassDSLInformation notAnnotated;

	private ClassDSLInformation testee;

	private boolean arrayContainsArray(String[][] expectedAsMethodInputArray, String[] methoddescribingsubs) {
		for (String[] string : expectedAsMethodInputArray) {
			boolean equal = Arrays.equals(string, methoddescribingsubs);
			if (equal)
				return true;
		}
		return false;
	}

	private void assertSubArrayIsContained(String[][] expectedAsMethodInputArray, String[] strings) {
		boolean arrayContainsArray = arrayContainsArray(expectedAsMethodInputArray, strings);
		if (!arrayContainsArray)
			fail(Arrays.toString(strings) + " not contained");
	}

	@Before
	public void beforeEachTest() throws Exception {
		testee = loadDefault(SdfDSLForExtractingTest.class);
		notAnnotated = loadDefault(NotAnnotatedClassForExtractorTest.class);
	}

	private String[][] getExpectedAsMethodInputArray() {
		String[][] result = new String[expectedSDFMethodsToBeExtracted.length][];
		for (int i = 0; i < expectedSDFMethodsToBeExtracted.length; i++) {
			String next = expectedSDFMethodsToBeExtracted[i];
			Matcher matcher = nameMatcher.matcher(next);
			matcher.find();
			String[] name = { matcher.group(1) };

			Matcher paraMatch = parameterMatcher.matcher(next);
			String paras = "";
			boolean find = paraMatch.find();
			if (find) {
				paras = paraMatch.group(1);
			}
			if (paras.isEmpty()) {
				result[i] = name;
			} else {
				String[] parasArray = paras.split(",");
				String[] parasArray2 = new String[parasArray.length + 1];
				parasArray2[0] = name[0];
				System.arraycopy(parasArray, 0, parasArray2, 1, parasArray.length);
				result[i] = parasArray2;
			}
		}
		return result;
	}

	private ClassDSLInformation getNoWaterClass() {
		return loadDefault(WaterSupportedFalseAnnotatedForExtractorTest.class);
	}

	private ClassDSLInformation loadDefault(Class<?> clazz) {
		ClassDSLInformation extractedClassInforamtion = new ClassDSLInformation(clazz);
		extractedClassInforamtion.load(DSLAnnotationDefaults.DEFAULT_CONFIGURATIONOPTIONS_MAP);
		return extractedClassInforamtion;
	}

	/**
	 * Compatible to the format of the outline view
	 * 
	 * @param method
	 * @return
	 */
	private String[] methodToSimpleNameStringOfMethodNameAndParameters(Method method) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		String[] parameterSimpleStrings = new String[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			Class<?> class1 = parameterTypes[i];
			parameterSimpleStrings[i] = class1.getSimpleName();
		}
		String[] methodInfos = new String[parameterSimpleStrings.length + 1];
		methodInfos[0] = method.getName();
		for (int i = 0; i < parameterSimpleStrings.length; i++) {
			methodInfos[i + 1] = parameterSimpleStrings[i];
		}
		return methodInfos;
	}

	@Test
	public void shouldBeAnnotatedIfAnnotated() throws Exception {
		List<ClassDSLInformation> add = ListBuilder.newList(getNoWaterClass()).add(testee).toList();
		for (ClassDSLInformation iterable_element : add) {
			assertTrue(iterable_element.isAnnotated());
		}
	}

	@Test
	public void shouldBeEqualComparableMethodsOfSameNames() throws Exception {
		Method[] m1s = ClassWithSameMethodsAsOther1.class.getDeclaredMethods();
		Method[] m2s = ClassWithSameMethodsAsOther2.class.getDeclaredMethods();
		Set<ComparableMethod> comps1 = ClassDSLInformation.getComparableMethods(Arrays.asList(m1s));
		Set<ComparableMethod> comps2 = ClassDSLInformation.getComparableMethods(Arrays.asList(m2s));
		assertThat(comps1).containsOnly(comps2);
	}

	@Test
	public void shouldContainAnnotatedHostlanguage() throws Exception {
		Set<Class<? extends HostLanguageGrammar>> typeRules = testee.getHostLanguageRules();
		List<Class<? extends HostLanguageGrammar>> expected = new ArrayList<Class<? extends HostLanguageGrammar>>();
		expected.add(SdfDSLForExtractingTest.MyHostLanguage.class);
		assertContainsExactly(expected, typeRules);
	}

	@Test
	public void shouldContainAnnotatedTypeRules() throws Exception {
		ArrayList<Class<? extends TypeHandler>> expected = new ArrayList<Class<? extends TypeHandler>>();
		expected.add(SdfDSLForExtractingTest.SortSymbolType.class);
		expected.add(SdfDSLForExtractingTest.ModuleIdType.class);
		expected.add(SdfDSLForExtractingTest.CharacterClassSymbolType.class);
		expected.add(SdfDSLForExtractingTest.CaseInsensitiveLiteralSymbolType.class);
		Set<Class<? extends TypeHandler>> typeRules = testee.getTypeRules();
		assertContainsExactly(expected, typeRules);
	}

	@Test
	public void shouldContainDefaultHostLanguageIfNotAnnotated() throws Exception {
		Set<Class<? extends HostLanguageGrammar>> expected = notAnnotated.getHostLanguageRules();
		Class<? extends HostLanguageGrammar>[] typeRules2 = DSLAnnotationDefaults.DEFAULT_DSLClass.hostLanguageRules();
		assertContainsExactly(expected, Arrays.asList(typeRules2));
	}

	@Test
	public void shouldContainDefaultsWhenNotAnnotated() throws Exception {
		Set<Class<? extends TypeHandler>> typeRules = notAnnotated.getTypeRules();
		Class<? extends TypeHandler>[] typeRules2 = DSLAnnotationDefaults.DEFAULT_DSLClass.typeRules();
		assertContainsExactly(Arrays.asList(typeRules2), typeRules);
	}

	@Test
	public void shouldDefaultWaterSupportIfNotAnnotated() throws Exception {
		boolean waterSupported = notAnnotated.isWaterSupported();
		assertEquals(DSLAnnotationDefaults.DEFAULT_DSLClass.waterSupported(), waterSupported);
	}

	@Test
	public void shouldHaveAllPublicMethods() throws Exception {
		String[][] expectedAsMethodInputArray = getExpectedAsMethodInputArray();

		List<MethodDSLInformation> methodsInformation = testee.getMethodsInformation();
		String[][] methodsInfoArray = new String[methodsInformation.size()][];
		for (int i = 0; i < methodsInformation.size(); i++) {
			Method method = methodsInformation.get(i).getMethod();
			String[] methoddescribingsubs = methodToSimpleNameStringOfMethodNameAndParameters(method);
			methodsInfoArray[i] = methoddescribingsubs;
		}

		for (String[] strings : methodsInfoArray) {
			assertSubArrayIsContained(expectedAsMethodInputArray, strings);
		}

		for (String[] strings : expectedAsMethodInputArray) {
			assertSubArrayIsContained(methodsInfoArray, strings);
		}

		assertThat(methodsInformation).hasSize(expectedAsMethodInputArray.length);
	}

	/*
	 * @DSLClass(whitespaceEscape = " ",typeRules = {
	 * SdfDSLForExtractingTest.SortSymbolType.class, *
	 * SdfDSLForExtractingTest.ModuleIdType.class, *
	 * SdfDSLForExtractingTest.CharacterClassSymbolType.class, *
	 * SdfDSLForExtractingTest.CaseInsensitiveLiteralSymbolType.class })
	 */
	@Test
	public void shouldHaveAnnotatedOptionsAndRestDefault() throws Exception {
		assertEquals(testee.getConfiguratinoOption(ConfigurationOptions.WHITESPACE_ESCAPE), " ");
		List<ConfigurationOptions> confOps = new ArrayList<ConfigurationOptions>(Arrays.asList(ConfigurationOptions
				.values()));
		confOps.remove(ConfigurationOptions.WHITESPACE_ESCAPE);
		for (ConfigurationOptions restshouldbedefault : confOps) {
			String expected = restshouldbedefault.defaultValue;
			String actual = testee.getConfiguratinoOption(restshouldbedefault);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void shouldHaveDefaultsIfNotAnnotated() throws Exception {
		for (ConfigurationOptions iterable_element : ConfigurationOptions.values()) {
			String actual = notAnnotated.getConfiguratinoOption(iterable_element);
			assertEquals(iterable_element.defaultValue, actual);
		}
	}

	@Test
	public void shouldHaveNoMethodsInformationForNoPublicMethodsGroovyClass() throws Exception {
		List<MethodDSLInformation> methodsInformation = loadDefault(NoPublicMethodsGroovyClassForExtractorTest.class)
				.getMethodsInformation();
		assertEmpty(methodsInformation);
	}

	@Test
	public void shouldHaveNoMethodsInformationForNoPublicMethodsJavaClass() throws Exception {
		List<MethodDSLInformation> methodsInformation = loadDefault(NoPublicMethodsJavaClass.class)
				.getMethodsInformation();
		assertEmpty(methodsInformation);
	}

	@Test
	public void shouldHaveAllPublicMethodsOfSDFclass() throws Exception {
		assertThat(loadDefault(SdfDSLForExtractingTest.class).getMethodsInformation()).hasSize(
				expectedSDFMethodsToBeExtracted.length);
	}

	@Test
	public void shouldHaveWaterSupporteFalse() throws Exception {
		ClassDSLInformation nowater = getNoWaterClass();
		assertFalse(nowater.isWaterSupported());
	}

	@Test
	public void shouldNotBeEqualMethodsOfSameNames() throws Exception {
		Method[] m1s = ClassWithSameMethodsAsOther1.class.getDeclaredMethods();
		Method[] m2s = ClassWithSameMethodsAsOther2.class.getDeclaredMethods();
		assertThat(Arrays.asList(m1s)).excludes(Arrays.asList(m2s));
	}

	@Test
	public void shouldNotHaveMethodDSLInformationIfNoMethods() throws Exception {
		List<MethodDSLInformation> methodsInformation = loadDefault(NoPublicMethodsJavaClass.class)
				.getMethodsInformation();
		assertEmpty(methodsInformation);
	}

	@Test
	public void shouldExtractGetMethodOfNot_GroovyObjectSupportclass_Name() throws Exception {
		ClassDSLInformation waterClass = loadDefault(WaterSupportedFalseAnnotatedForExtractorTest.class);
		List<MethodDSLInformation> methodsInformation = waterClass.getMethodsInformation();
		for (MethodDSLInformation methodDSLInformation : methodsInformation) {
			if (methodDSLInformation.getMethod().getName().contains("getAnything")) {
				return;
			}
		}
		fail("expected to have extracted class of name getAnything");
	}

	@Test
	public void shouldNotExtractGetPropertyMethod() throws Exception {
		ClassDSLInformation waterClass = loadDefault(WaterSupportedFalseAnnotatedForExtractorTest.class);
		List<MethodDSLInformation> methodsInformation = waterClass.getMethodsInformation();
		for (MethodDSLInformation methodDSLInformation : methodsInformation) {
			assertThat(methodDSLInformation.getMethod().getName()).doesNotContain("getProperty");
		}
	}

	@Test
	public void shouldNotIsAnnotatedIfNotAnnotated() throws Exception {
		boolean annotated = notAnnotated.isAnnotated();
		assertFalse(annotated);
	}

	@Test
	public void shouldIgnoreAnnotatedMethodsWithSpecialFilteredCharacter() throws Exception {
		// @DSLMethod public void ignore$me(){
		assertWhetherMethodWithSpecialCharsContained("ignore$me", false);
	}

	private void assertWhetherMethodWithSpecialCharsContained(String text, boolean shouldContain) {
		assertWhetherClassContainsMethod(shouldContain, text, SpecialMethodNamesClassForDSLInformationTestGroovy.class);
		assertWhetherClassContainsMethod(shouldContain, text, SpecialMethodNamesClassForDSLInformationTestJava.class);
	}

	private void assertWhetherClassContainsMethod(boolean shouldContain, String text, Class<?> clazz) {
		ClassDSLInformation javaClass = loadDefault(clazz);
		List<String> classMethods = methodsInformationToMethodNames(javaClass);
		if (shouldContain) {
			assertSubStringContained(text, classMethods);
		} else
			assertSubStringNotContained(text, classMethods);
	}

	private void assertSubStringContained(String text, List<String> javaMethods) {
		for (String string : javaMethods) {
			if (string.contains(text)) {
				return;
			}
		}
		fail("expected " + text + " to be contained in " + collectionPrettyPrint(javaMethods));
	}

	private void assertSubStringNotContained(String text, List<String> javaMethods) {
		for (String string : javaMethods) {
			if (string.contains(text)) {
				fail("expected " + text + " not to be contained in " + collectionPrettyPrint(javaMethods));
			}
		}
	}

	private List<String> methodsInformationToMethodNames(ClassDSLInformation javaClass) {
		List<MethodDSLInformation> methodsInformation = javaClass.getMethodsInformation();
		List<String> methodsinfosMethodNames = new ArrayList<String>(methodsInformation.size());
		for (MethodDSLInformation methodDSLInformation : methodsInformation) {
			methodsinfosMethodNames.add(methodDSLInformation.getMethod().getName());
		}
		return methodsinfosMethodNames;
	}

	@Test
	public void shouldIgnoreMethodsWithSpecialFilteredCharacter() throws Exception {
		// public void ignoreme2$(){
		assertWhetherMethodWithSpecialCharsContained("ignoreme2$", false);
	}

	@Test
	public void shouldProcessMethodsWithValidSpecialCharacters() throws Exception {
		// public void igetparse€d(){
		// public void iget_parseßd(){
		String[] contained = { "igetparse€d", "iget_parseßd" };
		for (String string : contained) {
			assertWhetherMethodWithSpecialCharsContained(string, true);
		}
	}

	@Test
	public void shouldProcessAnnotatedMethodsWithSpecialChar() throws Exception {
		// @DSLMethod(production="wtu",stringQuotation="'")public String
		// iget_parseßd_annotated(){
		assertWhetherMethodWithSpecialCharsContained("iget_parseßd_annotated", true);
	}

	@Test
	public void shouldHaveInheritedMethods() throws Exception {
		fail("HaveInheritedMethods has yet to be written.");
	}

}
