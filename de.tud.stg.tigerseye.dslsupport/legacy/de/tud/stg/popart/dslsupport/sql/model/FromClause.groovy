package de.tud.stg.popart.dslsupport.sql.model

/**
 * <p>A from clause consists of the <b>FROM</b> keyword followed by a list of tables. These
 * tables are separated by comma and could be derived from different kinds of joins.
 * The actual implementations only supports lists of {@link TableReference}, though.</p>
 * <p><b>Example:</b><br />
 * <b>FROM</b> students AS Students, grades</p>
 */
class FromClause {
	List<TableReference> tableReferences = []
	
	FromClause(List<TableReference> tableReferences) {
		this.tableReferences = tableReferences
	}
	
	String toString() {
		return "FROM ${tableReferences.join(', ')}"
	}
	
	static FromClause FROM(List<TableReference> tableReferences) {
		return new FromClause(tableReferences)
	}
}
