package de.tud.stg.popart.eclipse.core.debug.model.keywords;

/**
 * A PopartSourceFileOperationKeyword can be thought of as an instance
 * of a PopartOperationKeyword. It represents an operation keyword
 * in a Popart source file.
 *
 * @author David Marx
 * @author Thorsten Peter
 */
public class PopartSourceFileOperationKeyword extends PopartSourceFileKeyword {
	
	/**
	 * Constructs a PopartSourceFileOperationKeyword for the given
	 * PopartOperationKeyword and the specified line number
	 * 
	 * @param keyword The PopartLiteralKeyword
	 * @param lineNr The line number
	 */
	public PopartSourceFileOperationKeyword(PopartOperationKeyword keyword, int lineNr) {
		super(keyword, lineNr);	
	}
	
}
