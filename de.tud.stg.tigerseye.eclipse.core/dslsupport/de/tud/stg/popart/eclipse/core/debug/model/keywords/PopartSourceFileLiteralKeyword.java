package de.tud.stg.popart.eclipse.core.debug.model.keywords;

/**
 * A PopartSourceFileLiteralKeyword can be thought of as an instance
 * of a PopartLiteralKeyword. It represents a literal keyword
 * in a Popart source file.
 *
 * @author David Marx
 * @author Thorsten Peter
 */
public class PopartSourceFileLiteralKeyword extends PopartSourceFileKeyword {
	
	/**
	 * Constructs a PopartSourceFileLiteralKeyword for the given
	 * PopartLiteralKeyword and the specified line number
	 * 
	 * @param keyword The PopartLiteralKeyword
	 * @param lineNr The line number
	 */
	public PopartSourceFileLiteralKeyword(PopartLiteralKeyword keyword, int lineNr) {
		super(keyword, lineNr);		
	}
	
}
