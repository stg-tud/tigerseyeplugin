package de.tud.stg.tigerseye.eclipse.core.codegeneration.resources;

import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.builder.core.annotations.DSLMethod.DslMethodType;

public class SpecialMethodNamesClassForDSLInformationTestJava {

	@DSLMethod
	public void ignore$me(){
		//should ignore methods with special characters
	}
	
	public void ignoreme2$(){
		//should ignore methods with special characters
	}
	
	public void igetparse€d(){
	}
	
	public void iget_parseßd(){
	}
	
	@DSLMethod(production="wtu",stringQuotation="'")
	public String iget_parseßd_annotated(){
		return "";
	}
	
	@DSLMethod(type=DslMethodType.Literal)
	public void get(){
		//should be literal of production get
	}
	
	public void y() {
		// very short method name
	}

	@DSLMethod(type = DslMethodType.Literal)
	public void z() {
		// very short method name annotated
	}
	
}
