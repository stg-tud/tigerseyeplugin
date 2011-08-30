package de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction;

import static de.tud.stg.tigerseye.eclipse.core.utils.CustomFESTAssertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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

import de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction.ExtractedClassInforamtion.ComparableMethod;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars.HostLanguageGrammar;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.ClassWithSameMethodsAsOther1;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.ClassWithSameMethodsAsOther2;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.NoPublicMethodsGroovyClassForExtractorTest;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.NoPublicMethodsJavaClass;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.NotAnnotatedClassForExtractorTest;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.SdfDSLForExtractingTest;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.WaterSupportedFalseAnnotatedForExtractorTest;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ConfigurationOptions;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.TypeHandler;
import de.tud.stg.tigerseye.util.ListBuilder;

public class ExtractedClassInformationTest {

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

	private ExtractedClassInforamtion notAnnotated;

	private ExtractedClassInforamtion testee;

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

	private ExtractedClassInforamtion getNoWaterClass() {
		return loadDefault(WaterSupportedFalseAnnotatedForExtractorTest.class);
	}

	private ExtractedClassInforamtion loadDefault(Class<?> clazz) {
		ExtractedClassInforamtion extractedClassInforamtion = new ExtractedClassInforamtion(clazz);
		extractedClassInforamtion.load(ExtractorDefaults.DEFAULT_CONFIGURATIONOPTIONS_MAP);
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
		List<ExtractedClassInforamtion> add = ListBuilder.newList(getNoWaterClass()).add(testee).toList();
		for (ExtractedClassInforamtion iterable_element : add) {
			assertTrue(iterable_element.isAnnotated());
		}
	}

	@Test
	public void shouldBeEqualComparableMethodsOfSameNames() throws Exception {
		Method[] m1s = ClassWithSameMethodsAsOther1.class.getDeclaredMethods();
		Method[] m2s = ClassWithSameMethodsAsOther2.class.getDeclaredMethods();
		Set<ComparableMethod> comps1 = ExtractedClassInforamtion.getComparableMethods(Arrays.asList(m1s));
		Set<ComparableMethod> comps2 = ExtractedClassInforamtion.getComparableMethods(Arrays.asList(m2s));	
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
		Class<? extends HostLanguageGrammar>[] typeRules2 = ExtractorDefaults.DEFAULT_DSLClass.hostLanguageRules();
		assertContainsExactly(expected, Arrays.asList(typeRules2));
	}

	@Test
	public void shouldContainDefaultsWhenNotAnnotated() throws Exception {
		Set<Class<? extends TypeHandler>> typeRules = notAnnotated.getTypeRules();
		Class<? extends TypeHandler>[] typeRules2 = ExtractorDefaults.DEFAULT_DSLClass.typeRules();
		assertContainsExactly(Arrays.asList(typeRules2), typeRules);
	}

	@Test
	public void shouldDefaultWaterSupportIfNotAnnotated() throws Exception {
		boolean waterSupported = notAnnotated.isWaterSupported();
		assertEquals(ExtractorDefaults.DEFAULT_DSLClass.waterSupported(), waterSupported);
	}

	@Test
	public void shouldHaveAllPublicMethods() throws Exception {
		String[][] expectedAsMethodInputArray = getExpectedAsMethodInputArray();

		List<ExtractedMethodInformation> methodsInformation = testee.getMethodsInformation();
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
		List<ExtractedMethodInformation> methodsInformation = loadDefault(
				NoPublicMethodsGroovyClassForExtractorTest.class).getMethodsInformation();
		assertEmpty(methodsInformation);
	}

	@Test
	public void shouldHaveNoMethodsInformationForNoPublicMethodsJavaClass() throws Exception {
		List<ExtractedMethodInformation> methodsInformation = loadDefault(NoPublicMethodsJavaClass.class)
				.getMethodsInformation();
		assertEmpty(methodsInformation);
	}

	@Test
	public void shouldHaveOnlyPublicMethodsOfSDFclass() throws Exception {
		assertThat(testee.getMethodsInformation()).hasSize(expectedSDFMethodsToBeExtracted.length);
	}

	@Test
	public void shouldHaveWaterSupporteFalse() throws Exception {
		ExtractedClassInforamtion nowater = getNoWaterClass();
		assertFalse(nowater.isWaterSupported());
	}

	@Test
	public void shouldNotBeEqualMethodsOfSameNames() throws Exception {
		Method[] m1s = ClassWithSameMethodsAsOther1.class.getDeclaredMethods();
		Method[] m2s = ClassWithSameMethodsAsOther2.class.getDeclaredMethods();
		assertThat(Arrays.asList(m1s)).excludes(Arrays.asList(m2s));		
	}
	
	@Test
	public void shouldNotHaveMethodAnnotationsIfNoMethods() throws Exception {
		List<ExtractedMethodInformation> methodsInformation = getNoWaterClass().getMethodsInformation();
		assertEmpty(methodsInformation);
	}
	
	

	@Test
	public void shouldNotIsAnnotatedIfNotAnnotated() throws Exception {
		boolean annotated = notAnnotated.isAnnotated();
		assertFalse(annotated);
	}

	@Test
	public void shouldHaveInheritedMethods() throws Exception {
		fail("HaveInheritedMethods has yet to be written.");
	}

}
