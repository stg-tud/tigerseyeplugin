package de.tud.stg.popart.dslsupport.sql.model

/**
 * <p>A having clause consists of the HAVING keyword followed by a search condition.</p>
 * <p>NOTE: The searchCondition is currently underspecified and the whole
 * class is considered to be a draft</p>
 */
class HavingClause {
	def searchCondition
	
	String toString() {
		return "HAVING ${searchCondition}"
	}
}
