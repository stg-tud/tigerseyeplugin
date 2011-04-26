package de.tud.stg.popart.eclipse.core.debug.model.keywords;

/**
 * A PopartKeyword is a language element of a DSL.
 * PopartKeywords are specified in a Popart language definition.
 * According to the type of the keyword, there exist several
 * subclasses of this abstract class.
 * 
 * @author David Marx
 * @author Thorsten Peter
 */
public abstract class PopartKeyword implements Comparable<PopartKeyword> {
	
	protected String name;
	protected boolean breakpointPossible;
	protected int order;	// defines the position in the view
	
	/**
	 * Constructs an empty PopartKeyword
	 */
	public PopartKeyword() {
		
	}
	
	/**
	 * Constructs a PopartKeyword with the given name. On this
	 * keyword no breakpoint can be set.
	 * 
	 * @param name The name
	 */
	public PopartKeyword(String name) {
		this(name, false);
	}
	
	/**
	 * Constructs a PopartKeyword with the given name and
	 * the information if a breakpoint can be set on this keyword.
	 * 
	 * @param name The name
	 * @param breakpoint True, if a breakpoint can be set; false otherwise
	 */
	public PopartKeyword(String name, boolean breakpoint) {
		this.name = name;		
		this.breakpointPossible = breakpoint;
	}
	
	/**
	 * Returns the name of this keyword.
	 * 
	 * @return The name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Sets the name of this keyword.
	 * 
	 * @param name The name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns whether a breakpoint can be set on this keyword.
	 * 
	 * @return True, if a breakpoint can be set; false otherwise
	 */
	public boolean isBreakpointPossible() {
		return this.breakpointPossible;
	}
	
	/**
	 * Specifies if a breakpoint can be set on this keyword.
	 * 
	 * @param breakpointPossible True, if a breakpoint can be set; false otherwise
	 */
	public void setBreakpointPossible(boolean breakpointPossible) {
		this.breakpointPossible = breakpointPossible;		
	}

	/**
	 * Compares two PopartKeywords by their name.
	 */
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		// object must be PopartKeyword at this point
		PopartKeyword otherKeyword = (PopartKeyword) obj;
		return this.name.equals(otherKeyword.getName());
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode();
		
	}
	
	/**
	 * Used for ordering keywords in the UI.
	 */
	public int compareTo(PopartKeyword o) {	
		if (this.order==o.order) {
			return 0;
		}
		else {
			return (order < o.order) ? -1 : 1;
		}		
	}

	/**
	 * Returns a string representation of this keyword.
	 */
	public String toString() {
		String result = "[";
		result += "name: "+name+", breakpointPossible: "+breakpointPossible+"]";
		return result;
	}
	
}
