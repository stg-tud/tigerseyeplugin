package de.tud.stg.popart.dslsupport.sql.model

/**
 * <p>An update statement consists of the UPDATE keyword, followed by the table to update, the SET keyword,
 * a comma-separated-list of set clauses and finally an optional where clause.</p>
 * <p><b>Example:</b><br />
 * <b>UPDATE</b> Students
 * <b>SET</b> name = "Robert'); DROP TABLE Students;--", nickname = 'LITTLE BOBBY TABLES'
 * <b>WHERE</b> id = 3</p>
 * 
 * @see SetClause
 * @see WhereClause
 */
class UpdateStatement {
	QualifiedName tableName
	List<SetClause> setClauseList
	WhereClause whereClause
	
	UpdateStatement(QualifiedName tableName, List<SetClause> setClauseList, WhereClause whereClause = null) {
		this.tableName = tableName
		this.setClauseList = setClauseList
		this.whereClause = whereClause
	}
	
	String toString() {
		def string = "UPDATE ${tableName} SET ${setClauseList.join(', ')}"
		if (whereClause != null) string += " ${whereClause}"
		return string
	}
}
