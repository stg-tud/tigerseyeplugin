package de.tud.stg.popart.eclipse.core.debug.model.keywords;

/**
 * A PopartSourceFileKeyword can be thought of as an instance
 * of a PopartKeyword in a Popart source file. Whereas a PopartKeyword
 * is on a more conceptual level, a PopartSourceFileKeyword exists in a
 * concrete Popart source file.
 * Thus, it contains the line number information of the Popart source file
 * and information about how the source file is structured, i.e. which
 * PopartSourceFileKeyword is nested in another one.
 * There may be more information available, depending on the concrete subtype
 * of the PopartSourceFileKeyword.
 * 
 * According to the type of the keyword, there exist several
 * subclasses of this abstract class.
 *
 * @author David Marx
 * @author Thorsten Peter
 */
public abstract class PopartSourceFileKeyword {
	
	private PopartSourceFileStructuredElementKeyword parent;
	private PopartKeyword keyword;
	private int lineNr;
	
	/**
	 * Constructs a PopartSourceFileKeyword for the given
	 * PopartKeyword and the specified line number
	 * 
	 * @param keyword
	 * @param lineNr
	 */
	public PopartSourceFileKeyword(PopartKeyword keyword, int lineNr) {
		this.keyword = keyword;
		this.lineNr = lineNr;
	}
	
	/**
	 * Returns the PopartSourceFileStructuredElementKeyword where this
	 * keyword is nested in.
	 * 
	 * @return The enclosing PopartSourceFileStructuredElementKeyword
	 */
	public PopartSourceFileStructuredElementKeyword getParent() {
		return parent;
	}
	
	/**
	 * Sets the PopartSourceFileStructuredElementKeyword where this keyword
	 * is nested in.
	 * 
	 * @param parent The enclosing PopartSourceFileStructuredElementKeyword
	 */
	public void setParent(PopartSourceFileStructuredElementKeyword parent) {
		this.parent = parent;
	}
	
	/**
	 * Returns the PopartKeyword this keyword has got.
	 * 
	 * @return This keyword's associated PopartKeyword
	 */
	public PopartKeyword getKeyword() {
		return keyword;
	}
	
	/**
	 * Returns the line number where this PopartSourceFileKeyword was found on.
	 * 
	 * @return The line number
	 */
	public int getLineNr() {
		return lineNr;
	}
	
	/**
	 * Returns a string representation of this keyword.
	 */
	public String toString() {
		String result = "[";
		result += "keyword: "+keyword.getName()+" parent: "+parent;
		result += "]";
		return result;
	}

}
