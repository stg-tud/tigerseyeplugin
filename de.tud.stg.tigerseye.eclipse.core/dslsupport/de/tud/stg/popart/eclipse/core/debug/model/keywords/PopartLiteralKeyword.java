package de.tud.stg.popart.eclipse.core.debug.model.keywords;

/**
 * A PopartLiteralKeyword represents a literal in a DSL.
 * A literal can be thought of something like "true", "false".
 * 
 * For example, see TrivalentDSL which is a trivalent logic
 * DSL. "T", "F", "U" are literals there.
 * 
 * @author David Marx
 * @author Thorsten Peter
 */
public class PopartLiteralKeyword extends PopartKeyword {

	protected String type;
	
	/**
	 * Constructs an empty PopartLiteralKeyword.
	 */
	public PopartLiteralKeyword() {
		super();
	}

	/**
	 * Constructs a PopartLiteralKeyword with the given name.
	 * 
	 * @param name The name
	 */
	public PopartLiteralKeyword(String name) {
		super(name);
		order = 0;
	}

	/**
	 * Sets the type of this keyword. This is a Java or custom type.
	 * 
	 * @param type The type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Returns the type of this keyword.
	 * 
	 * @return The type
	 */
	public String getType() {
		return type;
	}

}
