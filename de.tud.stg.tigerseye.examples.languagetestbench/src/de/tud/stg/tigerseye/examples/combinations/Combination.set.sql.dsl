package de.tud.stg.tigerseye.examples.combinations

def interp = set,sql(name:'Combination') {
	Set M = {"a" , "b" , "c"} ⋃ {"c", "d"} ;
	
	println M

	def a = SELECT "id", "name" FROM "students", "teachers"
}