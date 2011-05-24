package de.tud.stg.tigerseye.examples.map


/**
 * Tigerseye language: de.tud.stg.tigerseye.examples.mapdsl.MapDSL
 *
 * Declared keywords:
 *  Entry buildEntry(Object, Object)
 *  Object eval(HashMap, Closure)
 */




map(name:'MapTest'){
	
	
	def b1 = buildEntry("firstName", "David")
	
	def b2 = buildEntry("lastName" , "Wheeler")
	
	println b1
	
	def m = buildMap(String.class, String.class, b1, b2)
	
	// Check if it works with 1.7.
	
	/*
	def m2 = [Integer,String: 1="hans",2="peter"]
	*/
	
	println m
	
}
