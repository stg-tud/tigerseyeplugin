package de.tud.stg.tigerseye.examples.sql


/**
 * Tigerseye language: de.tud.stg.tigerseye.examples.dsldefinitions.simplesqldsl.SimpleSqlDSL
 *
 * Declared keywords:
 *  Object eval(HashMap, Closure)
 */




sql(name:'SqlQuery'){ 

	def q11 = selectFrom( ["NAME","AGE"] as String[] , ["PERSONS"]  as String[] )
	
	def q12 = SELECT "NAME","AGE" FROM "PERSONS"
	
	def q21 = selectFromWhere(["NAME","AGE"] as String[] , ["PERSONS"]  as String[], ["AGE>20"] as String[])
	
	def q22 = SELECT "NAME","AGE" FROM "PERSONS" WHERE "AGE>20"
}
