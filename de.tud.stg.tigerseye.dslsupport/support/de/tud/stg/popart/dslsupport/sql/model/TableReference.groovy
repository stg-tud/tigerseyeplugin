package de.tud.stg.popart.dslsupport.sql.model

/**
 * Reference to a table.
 * @see Reference
 */
class TableReference extends Reference {
	
	public TableReference(String qualifier, String name, String alias) {
		super(qualifier, name, alias);
	}
	
	public TableReference(String qualifiedName, String alias) {
		super(qualifiedName, alias);
	}
	
	public TableReference(String qualifiedNameAndAs) {
		super(qualifiedNameAndAs);
	}
	
	/**
	 * Meant to be statically imported, to clean up code
	 */
	public static TableReference Table(String tab) {
		return new TableReference(tab)
	}
	
	/**
	 * Meant to be statically imported, to clean up code
	 */
	public static TableReference Table(String qualified, String alias) {
		return new TableReference(qualified, alias)
	}
	
	/**
	 * Meant to be statically imported, to clean up code
	 */
	public static TableReference Table(String qualifier, String name, String alias) {
		return new TableReference(qualifier, name, alias) ;
	}
}
