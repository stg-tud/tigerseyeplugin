package de.tud.stg.tigerseye.eclipse.core.codegeneration.resources;

import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.builder.core.annotations.DSLMethod.DslMethodType;

public class SpecialMethodNamesClassForDSLInformationTestGroovy {

	@DSLMethod
	def ignore$me(){
		//should ignore methods with special characters
	}
	
	def ignoreme2$(){
		//should ignore methods with special characters
	}
	
	def igetparse€d(){
	}
	
	def iget_parseßd(){
	}
	
	@DSLMethod(production="wtu",stringQuotation="'")
	def iget_parseßd_annotated(){
	}
	
	@DSLMethod(type=DslMethodType.Literal)
	def get(){
		//should be literal of production get
	}
	
	def y() {
		// very short method name
	}

	@DSLMethod(type = DslMethodType.Literal)
	def z() {
		// very short method name annotated
	}
	
}
