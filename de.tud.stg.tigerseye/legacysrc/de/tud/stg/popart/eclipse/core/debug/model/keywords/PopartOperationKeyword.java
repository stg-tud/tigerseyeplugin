package de.tud.stg.popart.eclipse.core.debug.model.keywords;

import java.util.ArrayList;

/**
 * A PopartLiteralKeyword represents an operation in a DSL.
 * A literal can be thought of something like plus or minus
 * when thinking about a mathematical DSL.
 * 
 * For example, see TrivalentDSL which is a trivalent logic
 * DSL. "Puts" is an operation there.
 * 
 * @author David Marx
 * @author Thorsten Peter
 */
public class PopartOperationKeyword extends PopartParameterKeyword {

	protected String returnType;
	
	/**
	 * Constructs an empty PopartOperationKeyword.
	 */
	public PopartOperationKeyword() {
		super();
	}
	
	/**
	 * Constructs a PopartOperationKeyword with the given name.
	 * 
	 * @param name The name
	 */
	public PopartOperationKeyword(String name) {
		super(name);
		params = new ArrayList<PopartParameter>();
		order = 1;
	}

	/**
	 * Sets the return type of this operation. This is a Java or custom type.
	 * 
	 * @param returnType The return type
	 */
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	/**
	 * Returns the return type of this operation.
	 * 
	 * @return The return type
	 */
	public String getReturnType() {
		return returnType;
	}
	
}
