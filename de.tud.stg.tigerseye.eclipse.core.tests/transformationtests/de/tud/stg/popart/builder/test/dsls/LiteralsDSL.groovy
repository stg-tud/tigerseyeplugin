package de.tud.stg.popart.builder.test.dsls

import java.awt.Color;

import de.tud.stg.tigerseye.dslsupport.DSL;
import de.tud.stg.tigerseye.dslsupport.annotations.DSLMethod;
import de.tud.stg.tigerseye.dslsupport.annotations.DSLMethod.DslMethodType;

class LiteralsDSL  implements DSL {
	
	def getBlack(){
		return Color.BLACK;
	}
	
	@DSLMethod(production="green", type=DslMethodType.Literal)
	def moreGreen(){
		return Color.GREEN;
	}
	
	@DSLMethod(type=DslMethodType.Literal)
	def getAnnotatedLiteral(){
		return null;
	}
	
	@DSLMethod
	def getAnnotatedOperation(){
		return null;
	}
		
	@DSLMethod(production="white")
	def moreWhite(){
		return Color.GREEN;
	}
	
	@DSLMethod(production="para__p0",isUnicodeEncoding=true)
	def moreParameter(int i){
		return Color.GREEN;
	}
	
	@DSLMethod(isUnicodeEncoding=true)
	def sum__p0(int[] inp){
		res = inp.collect { it }
		println res
	}
	
	@DSLMethod(production="sum2__p0__p1")
	def summes2(int i , int i2){
		
	}
	
	public void getIllegalLiteral(){
		
	}
	

}
