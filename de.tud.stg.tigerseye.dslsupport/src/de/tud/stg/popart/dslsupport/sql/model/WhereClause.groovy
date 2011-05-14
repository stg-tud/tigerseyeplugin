package de.tud.stg.popart.dslsupport.sql.model

import de.tud.stg.popart.dslsupport.sql.model.bool.BooleanTerm;

/**
 * <p>A where clause consists of the keyword WHERE followed by a boolean term,
 * which either returns true or false</p>
 * <p><b>Example:</b><br />
 * <b>WHERE</b> id = 3</p>
 * 
 * @see BooleanTerm
 */
class WhereClause {
	BooleanTerm searchCondition
	
	WhereClause(BooleanTerm searchCondition) {
		this.searchCondition = searchCondition
	}
	
	String toString() {
		return "WHERE ${searchCondition}"
	}
	
	static WhereClause WHERE(BooleanTerm searchCondition) {
		return new WhereClause(searchCondition)
	}
}
