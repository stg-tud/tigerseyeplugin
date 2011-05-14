package de.tud.stg.popart.dslsupport.sql.model.bool

class BinaryBooleanTerm extends BooleanTerm {
	Set<BooleanTerm> terms
	BinaryBooleanOperator op
	
	BinaryBooleanTerm(BinaryBooleanOperator op, Set<BooleanTerm> terms) {
		this.op = op
		this.terms = terms
	}
	
	String toString() {
		def opString = op.toString()
		def joined = terms.join(" ${opString} ")
		return "(${joined})"
	}
	
	boolean equals(Object o) {
		if (!(o instanceof BinaryBooleanTerm)) return false
		def other = o as BinaryBooleanTerm
		return terms == other.terms && op == other.op
	}
	
	static BinaryBooleanTerm AND(Set<BooleanTerm> terms) {
		return new BinaryBooleanTerm(BinaryBooleanOperator.AND, terms)
	}
	
	static BinaryBooleanTerm OR(Set<BooleanTerm> terms) {
		return new BinaryBooleanTerm(BinaryBooleanOperator.OR, terms)
	}
}
