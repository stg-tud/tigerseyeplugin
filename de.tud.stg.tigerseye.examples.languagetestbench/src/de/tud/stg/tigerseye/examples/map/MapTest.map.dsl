package de.tud.stg.tigerseye.examples.map


/**
 * Tigerseye language: de.tud.stg.tigerseye.examples.mapdsl.MapDSL
 *
 * Declared keywords:
 *  Entry buildEntry(Object, Object)
 *  Object eval(HashMap, Closure)
 */


//needs manual import of dependency that will be necessary after transformation
import de.tud.stg.tigerseye.examples.mapdsl.Entry



map(name:'MapTest'){	
	
	def hanspeter = [Integer,String: 1="hans",2="peter"]	
	
	println hanspeter
	
	def doubleint = [Double , Integer : 0=0,1=1,2=2,3=3, 4=4,5=5,6=6,7=7,8=8,9=9,10=10]
	println doubleint
 
	def hans = [String, Object : "name"="Hans", "lastname"="Häuser", "age"=21 , "married"=false]	
	println "hans ist $hans"
	
	def peter = [String, Object : "name"= "Peter", "lastname"="Bauer", "age"=45, "married"=true]	
	println "peter ist $peter"
	
	hans.putAll(peter) 
	assert hans.equals(peter)
	
}
