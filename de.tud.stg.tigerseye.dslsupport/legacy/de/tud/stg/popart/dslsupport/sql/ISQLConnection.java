package de.tud.stg.popart.dslsupport.sql;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Encapsulates the SQL Connection to the database. (Indirection between Program and JDBC)
 * Most of these methods are derived from {@link groovy.sql.Sql}. Exception to
 * this are getColumnNames(), getTablesNames() and isIdentifier()
 */
public interface ISQLConnection {
	public boolean execute(String query) throws SQLException;

	public List<List<Object>> executeInsert(String query) throws SQLException;

	public int executeUpdate(String query) throws SQLException;

	@SuppressWarnings("rawtypes")
	public List<Map> executeQuery(String query) throws SQLException;

	/**
	 * @return all valid column names for this SQLConnection
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	public Set<String> getColumnNames() throws SQLException;

	/**
	 * @return all valid table names for this SQLConnection
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	public Set<String> getTableNames() throws SQLException;

	/**
	 * This method helps by identifying valid identifiers for this
	 * SQLConnection. Identifiers are names for columns or tables.
	 * 
	 * @return true if given string is valid identifier, false otherwise
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	public boolean isIdentifier(String string);
}
