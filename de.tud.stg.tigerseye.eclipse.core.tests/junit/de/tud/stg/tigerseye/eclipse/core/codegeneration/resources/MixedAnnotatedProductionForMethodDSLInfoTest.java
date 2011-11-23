package de.tud.stg.tigerseye.eclipse.core.codegeneration.resources;

import de.tud.stg.tigerseye.dslsupport.annotations.DSLMethod;
import de.tud.stg.tigerseye.dslsupport.annotations.DSLMethod.Associativity;
import de.tud.stg.tigerseye.dslsupport.annotations.DSLMethod.DslMethodType;
import de.tud.stg.tigerseye.dslsupport.annotations.DSLMethod.PreferencePriority;

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
		//no return type actaully invalid literal 
	}
	
	public String getLiteralNoAnnotation(){
		return "";
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
	
	@DSLMethod(type = DslMethodType.Literal)
	public Object validLiteral() {
		return null;
	}
	
	public void getInvalidLiteral(){
		
	}
	
	// priority access methods testing
	
	@DSLMethod(priorityLowerThan="lowerman")
	public void haspriolower(){		
	}
	@DSLMethod(priorityHigherThan="uberman")
	public void haspriohigher(){		
	}
	@DSLMethod(associativity=Associativity.LEFT)
	public void hasassoleft(){		
	}
	@DSLMethod(preferencePriority=PreferencePriority.Avoid)
	public void hasprefvoid(){		
	}
	@DSLMethod(absolutePriority=-3245)
	public void hasAbsolutePriorityNeg(){		
	}
	@DSLMethod(absolutePriority=12)
	public void hasAbsolutePriorityPos(){		
	}
	
	// Return type tests
	
	public Object hasReturnType(){
		return null;
	}
	
	public void hasNtReturnType(){
		
	}
	
	// special case get as operation keyword with single parameter
	public Object get__p0(String key) {
		return null;
	}

	@DSLMethod(production="get__p0")
	public Object get__a0(String key) {
		return null;
	}
	
	
	@DSLMethod(isUnicodeEncoding=true)
	public void hasKeywordTranslationActivated(int i){
		
	}
	
	@DSLMethod(isUnicodeEncoding=false)
	public void hasKeywordTranslationExplicitlyFalse(int j){
		
	}

}
