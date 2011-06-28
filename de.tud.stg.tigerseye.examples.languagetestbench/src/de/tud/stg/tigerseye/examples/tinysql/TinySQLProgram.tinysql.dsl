package de.tud.stg.tigerseye.examples.tinysql


/**
 * Tigerseye language: de.tud.stg.tigerseye.example.tinysql.TinySQL
 *
 * Declared keywords:
 *  List selectFrom(String[], String[])
 */



/**
 * The TinySQL language was the exmaple used in the <i>Incremental Concrete Syntax for Embedded Languages</i> Paper.
 */
tinysql(name:'TinySQLProgram'){

	println "Registered people:"
	
	List<Map> result = SELECT "NAME","AGE" FROM "PERSON" 
	
	result .each{
		
		Map row ->
		println "Name: ${row.Name} Age:${row.Age}";
		
		}
	
	
}
