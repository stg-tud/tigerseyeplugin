package de.tud.stg.popart.dslsupport.sql.model;

/**
 * AST class that represents a list of any column references. ANY can be thought of as "...".
 */
public class AnyColumnReference extends ColumnReference {	
	public static final AnyColumnReference ANY = new AnyColumnReference();
	
	public AnyColumnReference() {
		super("ANY");
	}
}
