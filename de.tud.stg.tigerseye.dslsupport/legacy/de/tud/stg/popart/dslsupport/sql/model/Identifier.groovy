package de.tud.stg.popart.dslsupport.sql.model

import de.tud.stg.popart.dslsupport.sql.SimpleSQL;

/**
 * This class is an identifier as it is defined in the SQL BNF. It represents
 * a simple name without qualification. With help of the <code>asType</code> 
 * method, it can be converted to {@link QualifiedName}, {@link ColumnReference}
 * and {@link TableReference}. This mechanism is used to resolve column and
 * table names, which are written as identifier names in the Groovy code.
 * @see SimpleSQL
 */
class Identifier {
	String string
	
	public Identifier(String string) {
		this.string = string
	}
	
	public String toString() {
		return string
	}
	
	public Object asType(Class clazz) {
		def obj = null
		switch (clazz) {
			case QualifiedName:
				obj =  new QualifiedName(string)
				break
				
			case ColumnReference:
				obj = new ColumnReference(string)
				break
				
			case TableReference:
				obj = new TableReference(string)
				break
				
			case String:
				obj = this.toString()
				break
		}
		
		return obj
	}
}
