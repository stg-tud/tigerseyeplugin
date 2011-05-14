package de.tud.stg.popart.dslsupport.sql.model.bool

class NegatedTerm extends BooleanTerm {
	BooleanTerm term
	
	NegatedTerm(BooleanTerm term) {
		this.term = term
	}
	
	String toString() {
		return "NOT ${term}"
	}
	
	boolean equals(Object o) {
		if(!(o instanceof NegatedTerm)) return false
		def other = o as NegatedTerm
		return term == other.term
	}
}
