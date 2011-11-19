package de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction;

import static de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ConfigurationOptions.WHITESPACE_ESCAPE;
import static de.tud.stg.tigerseye.eclipse.core.utils.CustomFESTAssertions.assertThat;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.commands.ParameterType;
import org.junit.Before;
import org.junit.Test;

import utilities.TestUtils;
import de.tud.stg.popart.builder.test.dsls.SetDSL;
import de.tud.stg.tigerseye.dslsupport.annotations.DSLMethod;
import de.tud.stg.tigerseye.dslsupport.annotations.DSLParameter;
import de.tud.stg.tigerseye.dslsupport.annotations.DSLMethod.Associativity;
import de.tud.stg.tigerseye.dslsupport.annotations.DSLMethod.DslMethodType;
import de.tud.stg.tigerseye.dslsupport.annotations.DSLMethod.PreferencePriority;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.GroovyClassWithSomeAnnotatedMethodsForExtractorTest;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.MixedAnnotatedMethodsForExtractionTest;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.MixedAnnotatedProductionForMethodDSLInfoTest;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.NoProductionAnnotationOnMethodNoProduction;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.SdfDSLForExtractingTest;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.SpecialMethodNamesClassForDSLInformationTestJava;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.WaterSupportedFalseAnnotatedForExtractorTest;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.SdfDSLForExtractingTest.ExportOrHiddenSection;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.SdfDSLForExtractingTest.Imports;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.SdfDSLForExtractingTest.Module;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.SdfDSLForExtractingTest.ModuleId;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.resources.SdfDSLForExtractingTest.SortSymbol;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ConfigurationOptions;

public class MethodDSLInformationTest {

	private List<MethodDSLInformation> setDSLMethodlisting;
	/**
	 * Not annotated method
	 */
	private MethodDSLInformation getGrammar_MethodInfo;
	/**
	 * no annotated of name "notAnnotated"
	 */
	private MethodDSLInformation notAnnotated_MethodInfo;
	/**
	 * annotated with @DSLMethod(type=Literal)
	 */
	private MethodDSLInformation someliteral_MethodInfo;
	/**
	 * annotated with @DSLMethod(type=Literal) and beginning with get
	 */
	private MethodDSLInformation getSomeliteral_MethodInfo;
	/**
	 * beginning with get annotated as Operation
	 */
	private MethodDSLInformation getOperation_MethodInfo;
	/**
	 * just annotated as operation
	 */
	private MethodDSLInformation someOperation_MethodInfo;
	/**
	 * annotated {@link DslMethodType#AbstractionOperator} beginning with get
	 */
	private MethodDSLInformation getAbstractionOperation_MethodInfo;
	/**
	 * just annotated {@link DslMethodType#AbstractionOperator}
	 */
	private MethodDSLInformation abstractionOperation_MethodInfo;

	/**
	 * SetDSL methods:
	 * 
	 * <pre>
	 * union(Set, Set)
	 * intersection(Set, Set)
	 * asSet(MyList)
	 * singleElementedList(String)
	 * multiElementedList(String, MyList)
	 * </pre>
	 */

	@Before
	public void beforeEachTest() throws Exception {
		Class<SetDSL> clazz = SetDSL.class;
		ClassDSLInformation extractedClassInforamtion = loadDefaulted(clazz);
		setDSLMethodlisting = extractedClassInforamtion.getMethodsInformation();
		getGrammar_MethodInfo = getFirstMethodInfoInClass(SdfDSLForExtractingTest.class, "getGrammar");
		notAnnotated_MethodInfo = getFirstMethodInfoInClass(MixedAnnotatedMethodsForExtractionTest.class,
				"notAnnotated");
		someliteral_MethodInfo = getFirstMethodInfoInClass(MixedAnnotatedMethodsForExtractionTest.class, "someliteral");
		getSomeliteral_MethodInfo = getFirstMethodInfoInClass(MixedAnnotatedMethodsForExtractionTest.class,
				"getSomeliteral");
		getOperation_MethodInfo = getFirstMethodInfoInClass(MixedAnnotatedMethodsForExtractionTest.class,
				"getOperation");
		someOperation_MethodInfo = getFirstMethodInfoInClass(MixedAnnotatedMethodsForExtractionTest.class,
				"someOperation");
		abstractionOperation_MethodInfo = getFirstMethodInfoInClass(MixedAnnotatedMethodsForExtractionTest.class,
				"abstractionOperation");
		getAbstractionOperation_MethodInfo = getFirstMethodInfoInClass(MixedAnnotatedMethodsForExtractionTest.class,
				"getAbstractionOperation");
	}

	private ClassDSLInformation loadDefaulted(Class<?> clazz) {
		ClassDSLInformation extractedClassInforamtion = new ClassDSLInformation(clazz);
		extractedClassInforamtion.load();
		return extractedClassInforamtion;
	}

	private MethodDSLInformation getFirstMethodInfoFromListForName(List<MethodDSLInformation> mis, String methodname) {
		for (MethodDSLInformation e : mis) {
			if (e.getMethod().getName().contains(methodname)) {
				return e;
			}
		}
		throw new IllegalArgumentException(methodname + "not found in " + TestUtils.collectionPrettyPrint(mis));
	}

	static Method getFirstMethod(Class<?> c, String methodname) {
		Method[] methods = c.getMethods();
		for (Method e : methods) {
			if (e.getName().contains(methodname)) {
				return e;
			}
		}
		throw new IllegalArgumentException(methodname + "not found in "
				+ TestUtils.collectionPrettyPrint(Arrays.asList(methods)));
	}

	static MethodDSLInformation getFirstMethodInfoInClass(Class<?> c, String methodname) {
		Method firstMethod = getFirstMethod(c, methodname);
		MethodDSLInformation minf = new MethodDSLInformation(firstMethod);
		minf.load();
		return minf;
	}

	@Test
	public void shouldBeDefaultBeforeLoad() {
		// @DSLMethod(production = "lexical  start-symbols  p0", topLevel =
		// false)
		// public LexicalStartSymbols
		// lexicalStartSymbols(@DSLParameter(arrayDelimiter =
		// " ") Symbol[] symbols) {
		Method m = getFirstMethod(SdfDSLForExtractingTest.class, "lexicalStartSymbols");
		MethodDSLInformation minfs = new MethodDSLInformation(m);
		assertThat(minfs.isAnnotated()).isEqualTo(false);
		assertThat(minfs.isToplevel()).isEqualTo(DSLInformationDefaults.DEFAULT_DSLMethod.topLevel());
	}

	@Test
	public void shouldBeAnnotatedAfterLoad() {
		// @DSLMethod(production = "lexical  start-symbols  p0", topLevel =
		// false)
		// public LexicalStartSymbols
		// lexicalStartSymbols(@DSLParameter(arrayDelimiter =
		// " ") Symbol[] symbols) {
		Method m = getFirstMethod(SdfDSLForExtractingTest.class, "lexicalStartSymbols");
		MethodDSLInformation minfs = new MethodDSLInformation(m);
		minfs.load(DSLInformationDefaults.DEFAULT_CONFIGURATIONOPTIONS_MAP);
		assertThat(minfs.isAnnotated()).isEqualTo(true);
		assertThat(minfs.isToplevel()).isEqualTo(false);
		assertThat(minfs.getProduction()).isEqualTo("lexical  start-symbols  p0");
	}

	@Test
	public void shouldHaveMethodNameWithGetAsProductionIfNotAnnotatedIfBeginningWithGetAndHasParameters() throws Exception {
		// public Grammar getGrammar(String topLevelModuleName, boolean
		// cleanGrammar) {
		assertThat(getGrammar_MethodInfo.getProduction()).isEqualTo("getGrammar");
		assertThat(getGrammar_MethodInfo.getDSLType()).isEqualTo(DslMethodType.Operation);
	}

	@Test
	public void shouldReturnGetOfTypeOperationForMethodNameGet() throws Exception {
		// public void get(){
		MethodDSLInformation mi = getFirstMethodInfoInClass(MixedAnnotatedProductionForMethodDSLInfoTest.class, "get");
		assertThat(mi.getProduction()).isEqualTo("get");
		assertThat(mi.getDSLType()).isEqualTo(DslMethodType.Operation);
	}

	@Test
	public void shouldUseGetAsProductionForLitrealOfNameGet() throws Exception {
		// @DSLMethod(type=DslMethodType.Literal)
		// public void get(){
		MethodDSLInformation mi = getFirstMethodInfoInClass(SpecialMethodNamesClassForDSLInformationTestJava.class,
				"get");
		assertThat(mi.getProduction()).isEqualTo("get");
		assertThat(mi.getDSLType()).isEqualTo(DslMethodType.Literal);
	}

	@Test
	public void shouldUseMethodNameUntransformedIfNameTooShort() throws Exception {
		MethodDSLInformation mi = getFirstMethodInfoInClass(SpecialMethodNamesClassForDSLInformationTestJava.class, "y");
		assertThat(mi.getProduction()).isEqualTo("y");
		assertThat(mi.getDSLType()).isEqualTo(DslMethodType.Operation);
	}

	@Test
	public void shouldHaveMethodNameAsProductionIfNotProductionUsedInAnnotation() throws Exception {
		// @DSLMethod(arrayDelimiter = ":")
		// public void noproduction(){
		MethodDSLInformation firstMethodInfoForName = getFirstMethodInfoInClass(
				NoProductionAnnotationOnMethodNoProduction.class, "noproduction");
		// verify
		assertThat(firstMethodInfoForName.getProduction()).isEqualTo("noproduction");
		// redundant sanity check
		assertThat(firstMethodInfoForName.isAnnotated()).isTrue();
		assertThat(firstMethodInfoForName.getConfigurationOption(ConfigurationOptions.ARRAY_DELIMITER)).isEqualTo(":");
	}

	@Test
	public void shouldHaveProductionThatWasAnnotated() throws Exception {
		// @DSLMethod(production = "grep_p0")
		// public Object has_production(String arg) {
		assertMethodHasExpectedProduction("has_production", "grep_p0");
	}

	private void assertMethodHasExpectedProduction(String methodname, String expectedProduction) {
		MethodDSLInformation mi = getFirstMethodInfoInClass(MixedAnnotatedProductionForMethodDSLInfoTest.class,
				methodname);
		assertThat(mi.getProduction()).isEqualTo(expectedProduction);
	}

	@Test
	public void shouldHaveProductionThatWasAnnotatedAlsoIfMoreOptionsHaveBeenDefined() throws Exception {
		// @DSLMethod(production = "grep  p0", whitespaceEscape = " ",
		// absolutePriority = 20)
		// public Object has_production_and_options(String arg)
		assertMethodHasExpectedProduction("has_production_and_options", "grep  p0");
	}

	@Test
	public void shouldHaveMethodNameAsProductionIfNotAnnotatedNotBeginningWithGet() throws Exception {
		// public void start_p0_withoutAnnotation() { }
		String methodName = "start_p0_withoutAnnotation";
		assertMethodHasExpectedProduction(methodName, methodName);
	}

	@Test
	public void shouldHaveMethodNameWithoutGetAsProductionIfNotAnnotatedifBeginningWithGet() throws Exception {
		// public MixedAnnotatedProductionForMethodDSLInfoTest
		// getNotAnnotatedLitral() {
		assertMethodHasExpectedProduction("getNotAnnotatedLitral", "notAnnotatedLitral");
	}

	@Test
	public void shouldHaveMethodNameWithoutGetAsProductionIfNotAnnotatedifBeginningWithGet_lowercase() throws Exception {
		// public MixedAnnotatedProductionForMethodDSLInfoTest
		// getNotAnnotatedLitral() {
		assertMethodHasExpectedProduction("getnotAnnotatedLitral", "notAnnotatedLitral");
	}

	@Test
	public void shouldHaveMethodNameWithoutGetAsProduction_ifAnnotatedIfBeginningWithGetIfNotProductionSupported()
			throws Exception {
		// @DSLMethod(type=DslMethodType.Literal)
		// public void getLiteral() {
		assertMethodHasExpectedProduction("getLiteral", "literal");
	}

	@Test
	public void shouldHaveMethodNameWithoutGetAsProduction_ifAnnotatedIfBeginningWithGetIfNotProductionSupported_lowercase()
			throws Exception {
		// @DSLMethod(type=DslMethodType.Literal)
		// public void getliteral() {
		assertMethodHasExpectedProduction("getLiteral", "literal");
	}

	@Test
	public void shouldHaveTypeOperationAndMethodNameWithGetAsProduction_ifAnnotatedWithoutTypeAndBeginningWithGet()
			throws Exception {
		// @DSLMethod public Object getThisWillBeOperation(String any){
		assertMethodHasExpectedProduction("getThisWillBeOperation", "getThisWillBeOperation");
	}

	@Test
	public void shouldHaveMethodAsProductionIfNoProductionInAnnotation() throws Exception {
		// @DSLMethod(arrayDelimiter = "...") public void
		// no_production_but_annotation() { }
		String methodname = "no_production_but_annotation";
		assertMethodHasExpectedProduction(methodname, methodname);
	}

	@Test
	public void shouldHaveGivenCharacterAsWhitespaceIfProductionGiven() throws Exception {
		// @DSLMethod(production="containing whitespace",
		// whitespaceEscape=" ")public void WSE_shouldbeWS() {
		assertForMethodForConfOpValue("WSE_shouldbeWS", WHITESPACE_ESCAPE, " ");
	}

	@Test
	public void shouldHaveDefaultConfigurationOptions_ifNoProductionIfConfigurationIllegalJavaCharacter_WhitespaceEscape()
			throws Exception {
		// @DSLMethod(production="some?thing",whitespaceEscape="?") public void
		// WSE_shouldbeQM()
		String methodName = "WSE_shouldbeQM";
		ConfigurationOptions confop = ConfigurationOptions.WHITESPACE_ESCAPE;
		assertForMethodForConfOpValue(methodName, confop, "?");
	}

	@Test
	public void shouldHaveDefaultWSEForSpecialCharacterIfNoProduction() throws Exception {
		// @DSLMethod(whitespaceEscape="?") public void WSE_shouldbe_forQM() {
		assertForMethodForConfOpValue("WSE_shouldbe_forQM", WHITESPACE_ESCAPE, WHITESPACE_ESCAPE.defaultValue);
	}

	@Test
	public void shouldHaveDefaultWSEForWhitespaceIfNoProduction() throws Exception {
		// @DSLMethod(whitespaceEscape=" ") public void WSE_shouldbe_forWS() {
		assertForMethodForConfOpValue("WSE_shouldbe_forWS", WHITESPACE_ESCAPE, WHITESPACE_ESCAPE.defaultValue);
	}

	@Test
	public void shouldHaveDefinedWSifNoProductionIfValidJavaIdentifier() throws Exception {
		// @DSLMethod(whitespaceEscape="a")public void WSE_shouldbea() {
		assertForMethodForConfOpValue("WSE_shouldbea", WHITESPACE_ESCAPE, "a");
	}

	@Test
	public void shouldHaveDefinedWSifNoProductionIfValidJavaIdentifier_SupportedCurrencyChar() throws Exception {
		// @DSLMethod(whitespaceEscape="€") public void WSE€shouldbe€() {
		assertForMethodForConfOpValue("WSE€shouldbe€", WHITESPACE_ESCAPE, "€");
	}

	@Test
	public void shouldUseDefaultIfWSEIsEmptyString() throws Exception {
		// @DSLMethod(whitespaceEscape="")public void WSE_asemptyString(){
		assertForMethodForConfOpValue("WSE_asemptyString", WHITESPACE_ESCAPE, WHITESPACE_ESCAPE.defaultValue);
	}

	private void assertForMethodForConfOpValue(String methodName, ConfigurationOptions confop, String expectedValue) {
		MethodDSLInformation mi = getFirstMethodInfoInClass(MixedAnnotatedProductionForMethodDSLInfoTest.class,
				methodName);
		assertThat(mi.getConfigurationOption(confop)).isEqualTo(expectedValue);
	}

	@Test
	public void shouldNotBeAnnotatedJava() {
		assertThat(getGrammar_MethodInfo.isAnnotated()).isFalse();
	}

	@Test
	public void shouldBeAnnotatedGroovy() {
		MethodDSLInformation firstMethod = getFirstMethodInfoInClass(
				GroovyClassWithSomeAnnotatedMethodsForExtractorTest.class, "shouldbeannoated");
		assertThat(firstMethod.isAnnotated()).isTrue();
	}

	@Test
	public void shouldNotBeAnnotatedGroovy() {
		MethodDSLInformation firstMethod = getFirstMethodInfoInClass(
				GroovyClassWithSomeAnnotatedMethodsForExtractorTest.class, "shouldnotbeannoated");
		assertThat(firstMethod.isAnnotated()).isFalse();
	}

	@Test
	public void shouldGetDefaultConfigurationOptionsForNotAnnotatedMethod() {
		Collection<String> defaultValues = DSLInformationDefaults.DEFAULT_CONFIGURATIONOPTIONS_MAP.values();
		assertThat(getGrammar_MethodInfo.getConfigurationOptions().values()).containsOnly(defaultValues);
	}

	// DSLtype handling

	// Literals

	@Test
	public void shouldUseMethodNameUntransformedIfNameTooShortLiteral() throws Exception {
		MethodDSLInformation mi = getFirstMethodInfoInClass(SpecialMethodNamesClassForDSLInformationTestJava.class, "z");
		assertThat(mi.getProduction()).isEqualTo("z");
		assertThat(mi.getDSLType()).isEqualTo(DslMethodType.Literal);
	}

	@Test
	public void shouldGetDSLTypeOperationForNotAnnotatedMethodNotBeginningWithGet() {
		assertThat(notAnnotated_MethodInfo.getDSLType()).isEqualTo(DslMethodType.Operation);
	}

	@Test
	public void shouldGetDSLTypeLiteralForNotAnnotatedMethodBeginningWithGet() {
		//"public String getLiteralNoAnnotation(){"
		MethodDSLInformation minf = getMinfFromMixed("getLiteralNoAnnotation");
		assertThat(minf.getDSLType()).isEqualTo(DslMethodType.Literal);
	}

	@Test
	public void shouldGetDSLTypeLiteralForAnnotatedWithItNotBeginningWithGet() {
		assertThat(someliteral_MethodInfo.getDSLType()).isEqualTo(DslMethodType.Literal);
	}

	@Test
	public void shouldGetDSLTypeLiteralForAnnotatedWithItBeginningWithGet() {
		assertThat(getSomeliteral_MethodInfo.getDSLType()).isEqualTo(DslMethodType.Literal);
	}

	@Test
	public void shouldBeValidLiteral_notAnnotated_GetGrammarWithReturnType() throws Exception {
		assertThat(getGrammar_MethodInfo.isValid()).isTrue();
	}

	@Test
	public void shouldBeValidLiteral_annotated_GetGrammarWithReturnType() throws Exception {
		// @DSLMethod(type = DslMethodType.Literal) public Object validLiteral()
		// {
		getFirstMethodInfoInClass(MixedAnnotatedProductionForMethodDSLInfoTest.class, "validLiteral");
		assertThat(getGrammar_MethodInfo.isValid()).isTrue();
	}

	@Test
	public void shouldBeInvalidIfLiteralHasNoReturntype_notannotated() throws Exception {
		// public void getInvalidLiteral(){
		MethodDSLInformation mi = getFirstMethodInfoInClass(MixedAnnotatedProductionForMethodDSLInfoTest.class,
				"getInvalidLiteral");
		assertThat(mi.isValid()).isFalse();
	}

	@Test
	public void shouldBeInvalidIfLiteralHasNoReturntype_annotated() throws Exception {
		MethodDSLInformation mi = getFirstMethodInfoInClass(SpecialMethodNamesClassForDSLInformationTestJava.class, "z");
		assertThat(mi.isValid()).isFalse();
	}

	// Operations

	@Test
	public void shouldGetDSLTypeOperationForAnnotatedWithItBeginningWithGet() {
		assertThat(getOperation_MethodInfo.getDSLType()).isEqualTo(DslMethodType.Operation);
	}

	@Test
	public void shouldGetDSLTypeOperationForAnnotatedWithNotBeginningWithGet() {
		assertThat(someOperation_MethodInfo.getDSLType()).isEqualTo(DslMethodType.Operation);
	}

	@Test
	public void shouldGetDSLTypeAbstractionOperatorForAnnotatedWithItBeginningWithGet() {
		assertThat(getAbstractionOperation_MethodInfo.getDSLType()).isEqualTo(DslMethodType.AbstractionOperator);
	}

	@Test
	public void shouldGetDSLTypeAbstractionOperatorForAnnotatedWithNotBeginningWithGet() {
		assertThat(abstractionOperation_MethodInfo.getDSLType()).isEqualTo(DslMethodType.AbstractionOperator);
	}

	@Test
	public void shouldGetMethod() {
		Method aMethod = getFirstMethod(SdfDSLForExtractingTest.class, "contextFreeStartSymbols");
		MethodDSLInformation aMInfo = new MethodDSLInformation(aMethod);
		assertThat(aMInfo.getMethod()).isEqualTo(aMethod);
	}

	@Test
	public void shouldHaveIsToplevelFalse() {
		MethodDSLInformation info = getFirstMethodInfoFromListForName(setDSLMethodlisting, "multiElementedList");
		assertThat(info.isToplevel()).isFalse();
	}

	@Test
	public void shouldHaveIsToplevelTrue() {
		MethodDSLInformation info = getFirstMethodInfoFromListForName(setDSLMethodlisting, "union");
		assertThat(info.isToplevel()).isTrue();
	}

	@Test
	public void shouldHaveDefaultIsToplevelIfNotSpecified() {
		MethodDSLInformation info = getFirstMethodInfoFromListForName(setDSLMethodlisting, "asSet");
		assertThat(info.isToplevel()).isEqualTo(DSLInformationDefaults.DEFAULT_DSLMethod.topLevel());
	}

	/*
	 * @DSLMethod public Object getThisWillBeOperation(String any) { return
	 * null; }
	 * 
	 * public MixedAnnotatedProductionForMethodDSLInfoTest
	 * getNotAnnotatedLitral() { return this; }
	 */

	@Test
	public void shouldHaveExpectedIdentifierForNoParameters() throws Exception {
		// public void hasNoParameter(){
		Class<?> c = MixedAnnotatedProductionForMethodDSLInfoTest.class;
		MethodDSLInformation mi = getFirstMethodInfoInClass(c, "hasNoParameter");
		assertThat(mi.getUniqueIdentifier()).isEqualTo(c.getName() + "hasNoParameter()");
	}

	@Test
	public void shouldHaveExpectedIdentifierForOneParameters() throws Exception {
		MethodDSLInformation mi = getFirstMethodInfoFromListForName(setDSLMethodlisting, "asSet");
		assertThat(mi.getUniqueIdentifier()).isEqualTo(
				SetDSL.class.getName() + "asSet(" + SetDSL.MyList.class.getName() + ")");
	}

	@Test
	public void shouldHaveExpectedIdentifierForTwoParameters() throws Exception {
		// @DSLMethod(production = "( p0 => p1 )", topLevel = false)
		// public FunctionSymbol functionSymbol(@DSLParameter(arrayDelimiter =
		// " ")
		// Symbol[] left, Symbol right)
		Class<?> c = SdfDSLForExtractingTest.class;
		MethodDSLInformation mi = getFirstMethodInfoInClass(c, "functionSymbol");
		assertThat(mi.getUniqueIdentifier()).isEqualTo(c.getName() + "functionSymbol(" + //
				SdfDSLForExtractingTest.Symbol[].class.getName() + "," //
				+ SdfDSLForExtractingTest.Symbol.class.getName() //
				+ ")");
	}

	@Test
	public void shouldHaveExpectedIdentifierForVarargsParameters() throws Exception {
		// public void hasVarargsParameter(Integer... ints)
		Class<?> c = MixedAnnotatedProductionForMethodDSLInfoTest.class;
		MethodDSLInformation mi = getFirstMethodInfoInClass(c, "hasVarargsParameter");
		assertThat(mi.getUniqueIdentifier()).isEqualTo(
				c.getName() + "hasVarargsParameter(" + Integer[].class.getName() + ")");
	}

	@Test
	public void shouldHaveUserDefinedUniqueIdentifier() throws Exception {
		// @DSLMethod(uniqueIdentifier="uniqueuid")
		// public void hasUserDefinedUniqueIdentifier(Integer... ints) {
		Class<?> c = MixedAnnotatedProductionForMethodDSLInfoTest.class;
		MethodDSLInformation mi = getFirstMethodInfoInClass(c, "hasUserDefinedUniqueIdentifier");
		assertThat(mi.getUniqueIdentifier()).isEqualTo("uniqueuid");
	}

	@Test
	public void shouldHaveDefaultsIfNotAnnotated() throws Exception {
		MethodDSLInformation mi = getFirstMethodInfoInClass(MixedAnnotatedProductionForMethodDSLInfoTest.class,
				"getNotAnnotatedLitral");
		String expectedUniqueIdentifier = MixedAnnotatedProductionForMethodDSLInfoTest.class.getName()
				+ "getNotAnnotatedLitral()";
		String expectedProduction = "notAnnotatedLitral";

		assertHasDefaultsPlusProductionAndIdentifier(mi, expectedUniqueIdentifier, expectedProduction,
				DslMethodType.Literal);
	}

	private void assertHasDefaultsPlusProductionAndIdentifier(MethodDSLInformation mi, String expectedUniqueIdentifier,
			String expectedProduction, DslMethodType expectedDSLType) {
		DSLMethod def = DSLInformationDefaults.DEFAULT_DSLMethod;
		assertThat(mi.getConfigurationOptions()).isEqualTo(DSLInformationDefaults.DEFAULT_CONFIGURATIONOPTIONS_MAP);
		assertThat(mi.getDSLType()).isEqualTo(expectedDSLType);
		assertThat(mi.getAbsolutePriority()).isEqualTo(def.absolutePriority());
		assertThat(mi.getAssociativity()).isEqualTo(def.associativity());
		assertThat(mi.getPreferencePriority()).isEqualTo(def.preferencePriority());
		assertThat(mi.getPriorityHigherThan()).isEqualTo(def.priorityHigherThan());
		assertThat(mi.getPriorityLowerThan()).isEqualTo(def.priorityLowerThan());
		assertThat(mi.isToplevel()).isEqualTo(def.topLevel());
		assertThat(mi.getProduction()).isEqualTo(expectedProduction);
		assertThat(mi.getUniqueIdentifier()).isEqualTo(expectedUniqueIdentifier);
	}

	@Test
	public void shouldHaveDefaultsIfEmptyAnnotated() throws Exception {
		MethodDSLInformation mi = getFirstMethodInfoInClass(MixedAnnotatedProductionForMethodDSLInfoTest.class,
				"start_p0_withoutAnnotation");
		String expectedUniqueIdentifier = MixedAnnotatedProductionForMethodDSLInfoTest.class.getName()
				+ "start_p0_withoutAnnotation()";
		String expectedProduction = "start_p0_withoutAnnotation";
		assertHasDefaultsPlusProductionAndIdentifier(mi, expectedUniqueIdentifier, expectedProduction,
				DslMethodType.Operation);
	}

	private MethodDSLInformation getMinfFromMixed(String string) {
		return getFirstMethodInfoInClass(MixedAnnotatedProductionForMethodDSLInfoTest.class, string);
	}

	@Test
	public void shouldHaveUserSet_preferencePriority() throws Exception {
		// @DSLMethod(preferencePriority=PreferencePriority.Avoid) public void
		// hasprefvoid
		MethodDSLInformation minf = getMinfFromMixed("hasprefvoid");
		assertThat(minf.getPreferencePriority()).isEqualTo(PreferencePriority.Avoid);
	}

	@Test
	public void shouldHaveUserSet_priorityLowerThan() throws Exception {
		// @DSLMethod(priorityLowerThan="lowerman") public void haspriolower(){
		MethodDSLInformation minf = getMinfFromMixed("haspriolower");
		assertThat(minf.getPriorityLowerThan()).isEqualTo("lowerman");
	}

	@Test
	public void shouldHaveUserSet_priorityHigherThan() throws Exception {
		// @DSLMethod(priorityHigherThan="uberman") public void haspriohigher()
		MethodDSLInformation minf = getMinfFromMixed("haspriohigher");
		assertThat(minf.getPriorityHigherThan()).isEqualTo("uberman");
	}

	@Test
	public void shouldHaveUserSet_absolutePriority_POS() throws Exception {
		// @DSLMethod(absolutePriority=12)public void hasAbsolutePriorityPos(){
		MethodDSLInformation minf = getMinfFromMixed("hasAbsolutePriorityPos");
		assertThat(minf.getAbsolutePriority()).isEqualTo(12);
	}

	@Test
	public void shouldHaveUserSet_absolutePriority_NEG() throws Exception {
		// @DSLMethod(absolutePriority=-3245)public void
		// hasAbsolutePriorityNeg()
		MethodDSLInformation minf = getMinfFromMixed("hasAbsolutePriorityNeg");
		assertThat(minf.getAbsolutePriority()).isEqualTo(-3245);
	}

	@Test
	public void shouldHaveUserSet_associativity() throws Exception {
		// @DSLMethod(associativity=Associativity.LEFT) public void
		// hasassoleft()
		MethodDSLInformation minf = getMinfFromMixed("hasassoleft");
		assertThat(minf.getAssociativity()).isEqualTo(Associativity.LEFT);
	}

	@Test
	public void shouldGetAnnotationParameterOptionsOverInitialMap() {
		HashMap<ConfigurationOptions, String> initMap = new HashMap<ConfigurationOptions, String>();
		initMap.put(WHITESPACE_ESCAPE, "ws");
		initMap.put(ConfigurationOptions.ARRAY_DELIMITER, "~");
		initMap.put(ConfigurationOptions.PARAMETER_ESCAPE, "zuul");
		initMap.put(ConfigurationOptions.STRING_QUOTATION, ".*");
		// execute
		// @DSLMethod(production = "asdf.zuul",stringQuotation =
		// "\".*?(?<!\\)\"", whitespaceEscape = ".")public void
		// annotationWithSomeConfigurationOptions() {
		Method method = getFirstMethod(MixedAnnotatedProductionForMethodDSLInfoTest.class,
				"annotationWithSomeConfigurationOptions");
		MethodDSLInformation mi = new MethodDSLInformation(method);
		mi.load(initMap);
		// verify
		HashMap<ConfigurationOptions, String> expectedMap = new HashMap<ConfigurationOptions, String>(initMap);
		expectedMap.put(ConfigurationOptions.WHITESPACE_ESCAPE, ".");
		expectedMap.put(ConfigurationOptions.STRING_QUOTATION, "\".*?(?<!\\)\"");
		assertThat(mi.getConfigurationOptions()).isEqualTo(expectedMap);
	}

	@Test
	public void shouldToString() {
		// @DSLMethod(production = "asdf.zuul",stringQuotation =
		// "\".*?(?<!\\)\"", whitespaceEscape = ".")public void
		// annotationWithSomeConfigurationOptions() {
		MethodDSLInformation mi = getFirstMethodInfoInClass(MixedAnnotatedProductionForMethodDSLInfoTest.class,
				"annotationWithSomeConfigurationOptions");
		String toString = mi.toString();
		String[] toContainIgnoreCase = { "production", "asdf.zuul", "string", "Quotation", "\".*?(?<!\\)\"",
				"whitespace", "escape", ".", "array", "delimiter", ",", "parameter", "escape", "p",
				"annotationWithSomeConfigurationOptions", "is", "annotated", };
		assertThat(toString).containsAllSubstringsIgnoreCase(toContainIgnoreCase);
	}

	// Parameter Infos check

	@Test
	public void shouldHaveParameterInfosEmpty() throws Exception {
		// public void hasNoParameter() {
		MethodDSLInformation mi = getFirstMethodInfoInClass(MixedAnnotatedProductionForMethodDSLInfoTest.class,
				"hasNoParameter");
		List<ParameterDSLInformation> parameterInfos = mi.getParameterInfos();
		assertThat(parameterInfos).isEmpty();
	}

	@Test
	public void shouldHaveParameterInfosOne() throws Exception {
		// public SortSymbol sortSymbol(String name) {
		MethodDSLInformation minf = getFirstMethodInfoInClass(SdfDSLForExtractingTest.class, "sortSymbol");
		assertThat(minf.getParameterInfos()).hasSize(1);
	}

	@Test
	public void shouldHaveParameterInfosTwo() throws Exception {
		assertThat(getGrammar_MethodInfo.getParameterInfos()).hasSize(2);
	}

	@Test
	public void shouldReturnNullIfIndexForParameterInfoOutOfRange() throws Exception {
		assertThat(getGrammar_MethodInfo.getParameterInfo(2)).isNull();
	}

	@Test
	public void shouldReturnNullIfIndexForParameterInfoOutOfRange_BoundaryHigh() throws Exception {
		assertThat(getGrammar_MethodInfo.getParameterInfo(1)).isNotNull();
	}

	@Test
	public void shouldReturnNullIfIndexForParameterInfoOutOfRange_BoundaryLow() throws Exception {
		assertThat(getGrammar_MethodInfo.getParameterInfo(0)).isNotNull();
	}

	@Test
	public void shouldReturnNullIfIndexForParameterInfoOutOfRange_Negative() throws Exception {
		assertThat(getGrammar_MethodInfo.getParameterInfo(-1)).isNull();
	}

	@Test
	public void shouldHaveParameterInfosArray_LittleIntegrationTest() throws Exception {
		// @DSLMethod(production = "module  p0  p1  p2", topLevel = true)
		// public Module moduleWithoutParameters(ModuleId name,
		// @DSLParameter(arrayDelimiter = " ") Imports[] imports,
		// @DSLParameter(arrayDelimiter = " ") ExportOrHiddenSection[]
		// exportOrHiddenSections) {
		MethodDSLInformation firstMethodInfoInClass = getFirstMethodInfoInClass(SdfDSLForExtractingTest.class,
				"moduleWithoutParameters");
		List<ParameterDSLInformation> paraInfs = firstMethodInfoInClass.getParameterInfos();
		assertThat(paraInfs).hasSize(3);
		ParameterDSLInformation importsArray = paraInfs.get(1);
		assertThat(importsArray.getType()).isEqualTo(SdfDSLForExtractingTest.Imports[].class);
		assertThat(importsArray.getConfigurationOption(ConfigurationOptions.ARRAY_DELIMITER)).isEqualTo(" ");
	}

	@Test
	public void shouldSupportDirectAccessToAspecificParameter_littleIntegrationtest() throws Exception {
		// @DSLMethod(production = "module  p0  p1  p2", topLevel = true)
		MethodDSLInformation firstMethodInfoInClass = getFirstMethodInfoInClass(SdfDSLForExtractingTest.class,
				"moduleWithoutParameters");
		Class<?> expected = firstMethodInfoInClass.getMethod().getParameterTypes()[2];
		ParameterDSLInformation info = firstMethodInfoInClass.getParameterInfo(2);
		Type ipt = info.getType();
		assertThat(ipt).isEqualTo(expected);

	}

	@Test
	public void shouldHaveBooleanWhetherHasNtReturnType() throws Exception {
		MethodDSLInformation minfFromMixed = getMinfFromMixed("hasNtReturnType");
		assertThat(minfFromMixed.hasReturnValue()).isFalse();
	}

	@Test
	public void shouldHaveBooleanWhetherHasReturnType() throws Exception {
		MethodDSLInformation minfFromMixed = getMinfFromMixed("hasReturnType");
		assertThat(minfFromMixed.hasReturnValue()).isTrue();
	}
	
	@Test
	public void shouldParseMethodBeginningWithGetAndParameterAsOperation() throws Exception {
		// public Object get__p0(String key) {
		MethodDSLInformation minf = getMinfFromMixed("get__p0");
		assertThat(minf.getProduction()).isEqualTo("get__p0");
		assertThat(minf.getDSLType()).isEqualTo(DslMethodType.Operation);
	}
	
	@Test
	public void shouldParseCorrectlyAsSingleParameterOperation() throws Exception {
//		@DSLMethod(production="get__p0") public Object get__0(String key) {
		MethodDSLInformation minf = getMinfFromMixed("get__a0");
		assertThat(minf.getProduction()).isEqualTo("get__p0");
		assertThat(minf.getDSLType()).isEqualTo(DslMethodType.Operation);
	}

}
