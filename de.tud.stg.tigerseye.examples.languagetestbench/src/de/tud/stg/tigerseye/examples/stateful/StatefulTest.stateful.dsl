package de.tud.stg.tigerseye.examples.stateful


/**
 * Tigerseye language: de.tud.stg.tigerseye.examples.statefuldsl.StatefulDSL
 *
 * Declared keywords:
 *  Object eval(HashMap, Closure)
 *  Object get__p0(String)
 *  void set__p0_equals_p1(String, Object)
 */




stateful(name:'StatefulTest'){
	
	
	set "OS" = "unix"	
	set "isValid" = true
	
	println "isValid is:" + get "isValid"
	
	if("notValid") set "isValid" = false
	
	println "isValid is:" + get "isValid"
		
	println "OS is: " + get "OS"
	
	
}
