package de.tud.stg.popart.dslsupport.sql.model

/**
 * <p>A delete statement consists of the <b>DELETE FROM</b> keyword followed by
 * a table name and an optional where clause.</p>
 * <p><b>Example:</b><br />
 * <b>DELETE FROM</b> Students <b>WHERE</b> id = 3</p>
 */
class DeleteStatement {
	QualifiedName tableName
	WhereClause whereClause
	
	DeleteStatement(QualifiedName tableName, WhereClause whereClause = null) {
		this.tableName = tableName
		this.whereClause = whereClause
	}
	
	String toString() {
		def string = "DELETE FROM ${tableName}"
		if (whereClause != null) string += " ${whereClause}"
		return string
	}
}
