package de.tud.stg.popart.dslsupport.sql.model

/**
 * A set clause is a key-value-pair of the form <i>columnName</i> <b>=</b> <i>value</i>
 */
class SetClause {
	Identifier columnName
	String source
	
	SetClause(Identifier columnName, String source) {
		this.columnName = columnName;
		this.source = source;
	}
	
	String toString() {
		return "${columnName} = ${source}"
	}
}
