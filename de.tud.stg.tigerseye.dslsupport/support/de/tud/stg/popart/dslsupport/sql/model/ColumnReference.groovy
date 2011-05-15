package de.tud.stg.popart.dslsupport.sql.model

/**
 * Reference to a column
 * @see Reference
 */
class ColumnReference extends Reference {
	
	public ColumnReference(String qualifier, String name, String alias) {
		super(qualifier, name, alias);
	}
	
	public ColumnReference(String qualifiedName, String alias) {
		super(qualifiedName, alias);
	}
	
	public ColumnReference(String qualifiedNameAndAs) {
		super(qualifiedNameAndAs);
	}
	
	/**
	 * Meant to be statically imported, to clean up code
	 */
	public static ColumnReference Col(String col) {
		return new ColumnReference(col)
	}
	
	/**
	 * Meant to be statically imported, to clean up code
	 */
	public static ColumnReference Col(String qualified, String alias) {
		return new ColumnReference(qualified, alias)
	}
	
	/**
	 * Meant to be statically imported, to clean up code
	 */
	public static ColumnReference Col(String qualifier, String column, String alias) {
		return new ColumnReference(qualifier, column, alias) ;
	}
}
