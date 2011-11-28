package de.tud.stg.popart.dslsupport.sql

import groovy.sql.Sql;

import java.util.List;
import java.util.Map;

import de.tud.stg.tigerseye.dslsupport.Interpreter;
import de.tud.stg.popart.dslsupport.sql.model.*;
import static de.tud.stg.popart.dslsupport.sql.ListHelper.*;

/**
 * Language facade class (evaluator).
 */
class SimpleSQL extends Interpreter implements ISimpleSQL {
	private boolean DEBUG = false;
	private static ISQLConnection sqlConnection
	
	/**
	 * Empty constructor for possible compatibility issues
	 */
	public SimpleSQL() {
		super()
	}
	
	/**
	 * The interpreter will use the given SQL connection. {@link #connect(String)} will override this connection.
	 * @param sqlConnection
	 */
	public SimpleSQL(ISQLConnection sqlConnection) {
		super()
		this.sqlConnection = sqlConnection
	}
	
	public void connect(String jdbcUrl) {
		sqlConnection = new JDBCConnection(Sql.newInstance(jdbcUrl))
	}
	
	public List<Map> selectFrom(List<ColumnReference> selectList, List<TableReference> tables) {
		def fromClause = new FromClause(convertToTableReferenceList(tables));
		def tableExpression = new TableExpression(fromClause)
		query(new Query(convertToColumnReferenceList(selectList), tableExpression))
	}
	
	public List<Map> selectFromWhere(List<ColumnReference> selectList, List<TableReference> tables, WhereClause whereClause) {
		def fromClause = new FromClause(convertToTableReferenceList(tables))
		def tableExpression = new TableExpression(fromClause, whereClause)
		query(new Query(convertToColumnReferenceList(selectList), tableExpression))
	}
	
	public int updateSetWhere(Identifier tableName, List<SetClause> setList, WhereClause whereClause) {
		def qualifiedTableName = tableName as QualifiedName;
		def updateStatement = new UpdateStatement(qualifiedTableName, setList, whereClause)
		update(updateStatement)
	}
	
	/**
	 * Creates an Identifier out of the String and calls updateSetWhere(Identifier, List, WhereClause)
	 * @see #updateSetWhere(Identifier, List, WhereClause)
	 */
	public int updateSetWhere(String tableName, List<SetClause> setList, WhereClause whereClause) {
		def identifier = new Identifier(tableName)
		updateSetWhere(identifier, setList, whereClause)
	}
	
	public boolean deleteFromWhere(Identifier tableName, WhereClause whereClause) {
		def qualifiedTableName = tableName as QualifiedName;
		def deleteStatement = new DeleteStatement(qualifiedTableName, whereClause)
		delete(deleteStatement)
	}
	
	/**
	 * Creates an Identifier out of the String and calls deleteFromWhere(Identifier, WhereClause)
	 * @see #deleteFromWhere(identifier, WhereClause)
	 */
	public boolean deleteFromWhere(String tableName, WhereClause whereClause) {
		def identifier = new Identifier(tableName)
		deleteFromWhere(identifier, whereClause)
	}
	
	public List<List> insertIntoValues(Identifier tableName, List columns, List values) throws SQLDSLException {
		if (columns.size() != values.size()) {
			throw new SQLDSLException("List of columns must have same length as list of values")
		}
		
		def colValueMap = [:]
		columns.eachWithIndex { col,i -> colValueMap.put(col,values[i]) }
		
		insertIntoValues(tableName, colValueMap)
	}
	
	/**
	 * Creates an Identifier out of the String and calls insertIntoValues(Identifier, List, List)
	 * @see #insertIntoValues(Identifier, List, List)
	 */
	public List<List> insertIntoValues(String tableName, List columns, List values) throws SQLDSLException {
		def identifier = new Identifier(tableName)
		insertIntoValues(identifier, columns, values)
	}
	
	/**
	* Creates an Identifier out of the String and calls insertIntoValues(Identifier, Map)
	* @see #insertIntoValues(Identifier, List, List)
	*/
	public List<List> insertIntoValues(String tableName, Map<Identifier, String> values) {
		def identifier = new Identifier(tableName)
		insertIntoValues(identifier, values)
	}
	
	/**
	 * Creates an InsertStatement out of the given arguments and sends it to the database
	 * @see #insertIntoValues(Identifier, List, List)
	 */
	public List<List> insertIntoValues(Identifier tableName, Map<Identifier, String> values) {
		def qualifiedTableName = tableName as QualifiedName;
		def insertStatement = new InsertStatement(qualifiedTableName, values)
		insert(insertStatement)
	}
	
	/**
	 * This method is a select join point
	 */
	private List<Map> query(Query query) {
		if(DEBUG) println("executing: $query");
		sqlConnection.executeQuery(query.toString())
	}
	
	/**
	 * This method is a update join point
	 */
	private int update(UpdateStatement updateStatement) {
		if(DEBUG) println("executing: $updateStatement");
		sqlConnection.executeUpdate(updateStatement.toString())
	}
	
	/**
	 * This method is a delete join point
	 */
	private boolean delete(DeleteStatement deleteStatement) {
		if(DEBUG) println("executing: $deleteStatement");
		sqlConnection.execute(deleteStatement.toString())
	}
	
	/**
	 * This method is an insert join point
	 */
	private List<List> insert(InsertStatement insertStatement) {
		if(DEBUG) println("executing: $insertStatement");
		sqlConnection.executeInsert(insertStatement.toString())
	}
	
	/**
	 * Missing properties are checked against being a valid SQL-identifier. If that check is successful then they are returned as such.
	 * @see Identifier
	 */
	public Object propertyMissing(String name) {
		if (DEBUG) println("Unknown property: $name");
		if (sqlConnection.isIdentifier(name)) { return new Identifier(name)
		}
		else throw new MissingPropertyException(name);
	}
}
