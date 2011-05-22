package de.tud.stg.tigerseye.examples.map


/**
 * Tigerseye language: de.tud.stg.tigerseye.examples.mapdsl.MapDSL
 *
 * Declared keywords:
 *  Entry buildEntry(Object, Object)
 *  Object eval(HashMap, Closure)
 */




map(name:'MapTest'){
	
	// How to use the special writing style
	
	def b1 = buildEntry("firstName", "David")
	
	def b2 = buildEntry("lastName" , "Wheeler")
	
	println b1
	
	def m = buildMap(String.class, String.class, b1, b2)
	
	println m
	
}
