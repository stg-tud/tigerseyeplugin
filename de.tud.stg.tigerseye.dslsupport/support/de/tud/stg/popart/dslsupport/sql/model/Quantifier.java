package de.tud.stg.popart.dslsupport.sql.model;

/**
 * <p>Quantifier for SQL queries. The quantifier is an optional argument after the SELECT keyword.</p>
 * <p>Example: SELECT <b>DISTINCT</b> * FROM table</p>
 */
public enum Quantifier {
	ALL, DISTINCT ;
	
	/**
	 * Default quantifier if omitted
	 */
	public static final Quantifier Default = Quantifier.ALL ;
}
