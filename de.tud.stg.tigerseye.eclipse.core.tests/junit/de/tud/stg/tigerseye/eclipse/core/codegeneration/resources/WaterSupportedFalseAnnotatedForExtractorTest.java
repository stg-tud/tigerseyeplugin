package de.tud.stg.tigerseye.eclipse.core.codegeneration.resources;

import de.tud.stg.tigerseye.dslsupport.annotations.DSLClass;
import de.tud.stg.tigerseye.dslsupport.annotations.DSLMethod;
import de.tud.stg.tigerseye.dslsupport.annotations.DSLMethod.DslMethodType;

@DSLClass(waterSupported=false)
public class WaterSupportedFalseAnnotatedForExtractorTest {

	@DSLMethod(type=DslMethodType.Operation)
	public void getAnything(){
		//Should be handled as Operation
	}
	
	@DSLMethod(production="something_p1")
	public Object getProperty(String name){
		//should getfiltered 
		return null;
	}
	
	
}
