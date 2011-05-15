package de.tud.stg.popart.dslsupport.sql.model

/**
 * <p>A group-by clause consists of the <b>GROUP BY</b> keyword followed by a list of columns,
 * by which the aggregated values should be grouped.</p>
 * <p>NOTE: The groupingColumnReferenceList is currently underspecified and the whole
 * class is considered to be a draft</p>
 */
class GroupByClause {
	def groupingColumnReferenceList
	
	String toString(){
		return "GROUP BY ${groupingColumnReferenceList}"
	}
}
