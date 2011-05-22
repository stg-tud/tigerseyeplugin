package de.tud.stg.tigerseye.examples.set


/**
 * Tigerseye language: de.tud.stg.tigerseye.examples.setdsl.SetDSL
 *
 * Declared keywords:
 *  Set asSet(MyList)
 *  Object eval(HashMap, Closure)
 *  Set intersection(Set, Set)
 *  Set union(Set, Set)
 *  
 *  	
 *  
 */




set(name:'SetTest'){

	/* Translation with a special character like ⋃ only seems to work for one application,
	 * when a second operation uses another special character it is not always translated correctly. 
	 */  

	/*
	Set a = { "2"} ⋃ { "6", "8", "2"} 
	Set ares = { "2", "6", "8"}
	 */
	 
	Set b = { "2"} ⋂ { "6", "8", "2"}
	Set bres = { "2" }
	
	
//	println a
	println b 
//	println (a.equals(b)) 
	println (b.equals(bres))	
}
