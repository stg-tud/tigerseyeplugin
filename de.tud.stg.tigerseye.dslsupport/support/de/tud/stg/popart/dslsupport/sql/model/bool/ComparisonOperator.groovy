package de.tud.stg.popart.dslsupport.sql.model.bool;

enum ComparisonOperator {
	LESS_THAN("<"),
	LESS_THAN_EQUALS("<="),
	EQUALS("="),
	GREATER_THAN_EQUALS(">="),
	GREATER(">"),
	LIKE("LIKE")
	
	String representation
	
	ComparisonOperator(String representation) {
		this.representation = representation
	}
	
	String toString() {
		return representation
	}
}
