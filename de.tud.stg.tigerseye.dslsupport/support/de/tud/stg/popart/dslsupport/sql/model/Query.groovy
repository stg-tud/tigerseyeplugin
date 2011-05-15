package de.tud.stg.popart.dslsupport.sql.model

/**
 * This class is a model for SQL queries (a.k.a. SELECT statements)
 */
class Query {
	Quantifier quantifier
	List<ColumnReference> selectList
	TableExpression tableExpression
	
	Query(List<ColumnReference> selectList, TableExpression tableExpression, Quantifier quantifier = Quantifier.Default) {
		this.selectList = selectList
		this.tableExpression = tableExpression
		this.quantifier = quantifier
	}
	
	String toString() {
		def query = "${selectList.join(', ')} ${tableExpression}"
		if (getQuantifier() == Quantifier.Default) {
			return "SELECT ${query}"
		} else {
			return "SELECT ${quantifier} ${query}"
		}
	}
	
	static Query SELECT(List<ColumnReference> selectList, TableExpression tableExpression) {
		return new Query(selectList, tableExpression)
	}
	
	static Query SELECT(Quantifier quantifier, List<ColumnReference> selectList, TableExpression tableExpression) {
		return new Query(selectList, tableExpression, quantifier)
	}
}
