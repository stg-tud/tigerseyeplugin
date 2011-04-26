package de.tud.stg.popart.eclipse.core.debug.model.keywords;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a keyword which can have parameters.
 *
 * @author David Marx
 * @author Thorsten Peter
 */
public abstract class PopartParameterKeyword extends PopartKeyword {

	protected List<PopartParameter> params = new ArrayList<PopartParameter>();
	
	/**
	 * Constructs an empty PopartParameterKeyword.
	 */
	public PopartParameterKeyword() {
		super();
	}
	
	/**
	 * Constructs a PopartParameterKeyword with the given name.
	 * 
	 * @param name The name
	 */
	public PopartParameterKeyword(String name) {
		super(name);		
	}
	
	/**
	 * Returns a string representation of this keyword which is used for
	 * code generation.
	 * 
	 * @return The generated code string.
	 */
	public String getParameterString() {
		String result = "";
		for (int i = 0; i < params.size(); i++) {
			result += params.get(i).getType() + " " + params.get(i).getName();
			if (i < params.size() - 1) {
				result += ", ";
			}
		}
		return result;
	}
	
	/**
	 * Adds a parameter to this keyword.
	 * 
	 * @param parameter the parameter
	 */
	public void addParameter(PopartParameter parameter) {
		params.add(parameter);
	}

	/**
	 * Removes a parameter from this keyword.
	 * 
	 * @param parameter The parameter
	 */
	public void removeParamerer(PopartParameter parameter) {
		params.remove(parameter);
	}

	/**
	 * Returns all parameters this keyword has got.
	 * 
	 * @return The parameters
	 */
	public Object[] getParameter() {		
		return params.toArray();
	}
	
}
