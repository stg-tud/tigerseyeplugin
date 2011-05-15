package de.tud.stg.popart.dslsupport.sql.model.bool

class ComparisonPredicate extends Predicate {
	def val1, val2
	ComparisonOperator op
	
	ComparisonPredicate(ComparisonOperator op, val1, val2) {
		this.op = op
		this.val1 = val1
		this.val2 = val2
	}
	
	String toString() {
		return "(${val1} ${op} ${val2})"
	}
	
	boolean equals(Object o) {
		if(!(o instanceof ComparisonPredicate)) return false
		def other = o as ComparisonPredicate
		return val1 == other.val1 && val2 == other.val2 && op == other.op
	}
	
	static ComparisonPredicate EQUALS(val1, val2) {
		return new ComparisonPredicate(ComparisonOperator.EQUALS, val1, val2)
	}
	
	static ComparisonPredicate LIKE(val1, val2) {
		return new ComparisonPredicate(ComparisonOperator.LIKE, val1, val2)
	}
}
