package de.tud.stg.tigerseye.eclipse.core.codegeneration.resources;

import de.tud.stg.tigerseye.dslsupport.annotations.DSLMethod;
import de.tud.stg.tigerseye.dslsupport.annotations.DSLMethod.DslMethodType;


public class MixedAnnotatedMethodsForExtractionTest {

	public void notAnnotated(){
		
	}
	
	@DSLMethod
	public void emptyDSLMethodAnnotation(){
		
	}
	
	@DSLMethod(type=DslMethodType.Literal)
	public void someliteral(){
		
	}
	
	@DSLMethod(type=DslMethodType.Literal)
	public void getSomeliteral(){
		
	}
	
	@DSLMethod(type=DslMethodType.Operation)
	public void getOperation(String any){
		
	}
	
	@DSLMethod(type=DslMethodType.Operation)
	public void someOperation(String any){
		
	}
	
	@DSLMethod(type=DslMethodType.AbstractionOperator)
	public void getAbstractionOperation(Integer any){
		
	}
	
	@DSLMethod(type=DslMethodType.AbstractionOperator)
	public Object abstractionOperation(String any){
		return null;
	}
	
	@DSLMethod
	public Object getThisWillBeOperation(String any){
		return null;
	}
	
}
