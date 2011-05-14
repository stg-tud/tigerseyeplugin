package de.tud.stg.popart.dslsupport.sql.model

/**
 * <p>A table expression is the expression after the select list in a query. A minimal
 * table expression at least consists of a from expression. Optional parts are a
 * where clause, a group-by clause and a having clause.</p>
 * <p><b>Example:</b><br />
 * <b>FROM</b> students, grades
 * <b>WHERE</b> name LIKE 'A%'
 * <b>GROUP BY</b> name
 * <b>HAVING</b> SUM(points) > 50 
 * </p>
 */
class TableExpression {
	FromClause fromClause
	WhereClause whereClause
	GroupByClause groupByClause
	HavingClause havingClause
	
	TableExpression(FromClause fromClause, WhereClause whereClause = null, GroupByClause groupByClause = null, HavingClause havingClause = null) {
		this.fromClause = fromClause
		this.whereClause = whereClause
		this.groupByClause = groupByClause
		this.havingClause = havingClause
	}
	
	String toString() {
		def clauses = [fromClause, whereClause, groupByClause, havingClause].findAll { it != null }
		return clauses.join(" ")
	}
}
