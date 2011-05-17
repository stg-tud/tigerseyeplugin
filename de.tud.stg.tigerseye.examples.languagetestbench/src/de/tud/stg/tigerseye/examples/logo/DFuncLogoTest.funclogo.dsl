package de.tud.stg.tigerseye.examples.logo

/**
 * Tigerseye language: de.tud.stg.tigerseye.lang.logo.FunctionalLogo
 *
 * Declared keywords:
 *  Closure app(String)
 *  void fun(String, Closure)
 *  HashMap getFunctionNamesToClosure()
 *  void setFunctionNamesToClosure(HashMap)
 */


/**
 * Functional Logo provides the keywords fun and app with which 
 * a function can be defined but first executed when called with
 * it's identifier via app.
 * 
 * Additionally this Logo language extends Timed Logo which introduces 
 * delays after each drawing operation to simulate costs
 * by slowing it down by 100 ms.
 */

funclogo(name:'FuncLogoTest'){
	

	fun("polygon") { int length, int edges ->
		int angle = (int)(360 / edges)
		repeat (edges) {
		  forward length
		  right angle
		}
	}
	
	app("polygon")(50,8);
	
	left 90
	forward 150
	right 90
	
	app("polygon")(50,20);
	

}
