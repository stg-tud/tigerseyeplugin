package de.tud.stg.tigerseye.eclipse.core.codegeneration.resources;

import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.builder.core.annotations.DSLMethod.DslMethodType;

public class MixedAnnotatedProductionForMethodDSLInfoTest {

	public void start_p0_withoutAnnotation() {
	}

	@DSLMethod(production = "grep_p0")
	public Object has_production(String arg) {
		return null;
	}

	@DSLMethod(production = "grep  p0", whitespaceEscape = " ", absolutePriority = 20)
	public Object has_production_and_options(String arg) {
		return null;
	}

	@DSLMethod(arrayDelimiter = "...")
	public void no_production_but_annotation() {
	}

	@DSLMethod(type = DslMethodType.Literal)
	public void getLiteral() {
	}

	@DSLMethod(type = DslMethodType.Literal)
	public void getliteral() {
	}

	@DSLMethod
	public Object getThisWillBeOperation(String any) {
		return null;
	}

	public MixedAnnotatedProductionForMethodDSLInfoTest getNotAnnotatedLitral() {
		return this;
	}

	public MixedAnnotatedProductionForMethodDSLInfoTest getnotAnnotatedLitral() {
		return this;
	}

	@DSLMethod(production = "some?thing", whitespaceEscape = "?")
	public void WSE_shouldbeQM() {
	}

	@DSLMethod(whitespaceEscape = "?")
	public void WSE_shouldbe_forQM() {
	}

	@DSLMethod(whitespaceEscape = " ")
	public void WSE_shouldbe_forWS() {
	}

	@DSLMethod(production = "containing whitespace", whitespaceEscape = " ")
	public void WSE_shouldbeWS() {
	}

	@DSLMethod(whitespaceEscape = "a")
	public void WSE_shouldbea() {
	}

	@DSLMethod(whitespaceEscape = "€")
	public void WSE€shouldbe€() {
	}

	public void hasNoParameter() {

	}

	public void hasVarargsParameter(Integer... ints) {

	}

	@DSLMethod(uniqueIdentifier = "uniqueuid")
	public void hasUserDefinedUniqueIdentifier(Integer... ints) {

	}

	public void get() {
		// corner case use get not as literal but as operation of name get
	}

	@DSLMethod(whitespaceEscape = "")
	public void WSE_asemptyString() {

	}

	@DSLMethod(production = "asdf.zuul", stringQuotation = "\".*?(?<!\\)\"", whitespaceEscape = ".")
	public void annotationWithSomeConfigurationOptions() {

	}

}
