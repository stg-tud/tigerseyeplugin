package de.tud.stg.popart.eclipse.core.debug.model.keywords;

/**
 * A Parameter for a PopartParameterKeyword. Parameters are specified
 * by a name and a type.
 *
 * @author David Marx
 * @author Thorsten Peter
 */
public class PopartParameter {

	private String name;
	private String type;
	
	/**
	 * Constructs a PopartParameter with the given name and type.
	 * @param name The name
	 * @param type The type
	 */
	public PopartParameter(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	/**
	 * Returns the name of the paramater.
	 * 
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the type of the paramater.
	 * 
	 * @return The type
	 */
	public String getType() {
		return type;
	}

	
	/**
	 * Sets the name of the parameter.
	 * 
	 * @param name The name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the type of the parameter.
	 * 
	 * @param name The type
	 */
	public void setType(String type) {
		this.type = type;
	}

}
