package de.tud.stg.popart.dslsupport.sql.model;


public class AnyTableReference extends TableReference {

	public static final ANY_TABLE = new AnyTableReference()
	
	public AnyTableReference() {
		super("ANY_TABLE")
	}
	
}
