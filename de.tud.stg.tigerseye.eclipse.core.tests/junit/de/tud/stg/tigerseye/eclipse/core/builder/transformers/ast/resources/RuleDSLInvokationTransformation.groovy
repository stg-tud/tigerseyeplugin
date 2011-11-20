package de.tud.stg.tigerseye.eclipse.core.builder.transformers.ast.resources

import static java.lang.System.err
import groovy.lang.Closure
import de.tud.stg.tigerseye.dslsupport.DSL
import de.tud.stg.tigerseye.dslsupport.annotations.DSLMethod
import de.tud.stg.tigerseye.dslsupport.annotations.DSLMethod.DslMethodType

class RuleDSLInvokationTransformation implements DSL{

	static class Policy{

	}

	static class PolicyDescription{
		def name
	}

	static class Rule{

	}

	static class Entry{

	}

	@DSLMethod(production = "Policy__(__Id__=__p0__)__{__p1__}")
	def PolicyDescription aPolicyWithId(String idName, Closure cl) {
		poDes = new PolicyDescription()
		return poDes;
	}

	@DSLMethod(production = "Policy__{__p0__}", topLevel=true)
	def PolicyDescription aPolicy(Closure cl) {
		aPolicyWithId("[policy_id_unassigned]",cl)
	}

	@DSLMethod(production = "Rule__{__p0__}", arrayDelimiter=",")
	def aRule(Entry[] listElements) {
		return new PolicyDescription()
	}

	@DSLMethod(production = "Rule__(__Id__=__p0__)__{__p1__}", arrayDelimiter=",")
	def aRuleWithId(String id, Entry[] listElements) {
		return new PolicyDescription()
	}

	@DSLMethod(production = "Options__=__{__p0__}", arrayDelimiter=",")
	def Entry entryOptions(String[] strEntries){
		return new Entry("Options", strEntries)
	}

	@DSLMethod(production="p0__=__p1", topLevel=false)
	public Entry aKeyValue(String key, String[] val){
		new Entry(key,val)
	}

	@DSLMethod(type=DslMethodType.Literal)
	public Policy getGeneratedPolicy(){
		return createPolicyEmergent();
	}

	@DSLMethod(production="Extends__(_Super__=__p0__)")
	public void doesExtendSuperPolicy(String name){

	}

	public void printPolicy(){
		println "Policy is: poDes.name"
	}

	/*
	 * misusing this feature to interpret keywords as keys for the attributes inside Rule statements.
	 */
	def String propertyMissing(String name){
		return name
	}

	def methodMissing(String name, args){
		println "missing method: $name with args $args"
	}

	@DSLMethod(type=DslMethodType.Literal, production="resultingPolicy")
	Policy createPolicyEmergent(){
		return new Policy()
	}
	
	@DSLMethod(production="get__the__policy")
	Policy createZeroArityMethod(){
		return new Policy()
	}

}



