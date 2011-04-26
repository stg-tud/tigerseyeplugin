package de.tud.stg.popart.eclipse.core.debug;

import java.util.HashMap;
import java.util.Map;

import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartSourceFileKeyword;

/**
 * A registry for PopartSourceFileKeywords.
 * This registry contains the keywords which are used in a Popart source file.
 * In contrast to the PopartKeywordRegistry, which contains the more conceptual
 * PopartKeywords, this registry contains "real" keywords which are taken from
 * the source code.
 * This means, besides the information which PopartKeyword is used in a
 * certain line of code, a PopartSourceFileKeyword, e.g., keeps track of the line
 * number where the keyword occured.
 * 
 * @author David Marx
 * @author Thorsten Peter
 */
public class PopartSourceFileKeywordRegistry {
	
	private static PopartSourceFileKeywordRegistry INSTANCE = new PopartSourceFileKeywordRegistry();
	private Map<Integer, PopartSourceFileKeyword> lines = new HashMap<Integer, PopartSourceFileKeyword>();
	private int maxLine=0;
	
	/**
	 * Private constructor. Not to be instantiated.
	 * 
	 */
	private PopartSourceFileKeywordRegistry() {
	}

	/**
	 * Get the single instance of this class.
	 * 
	 * @return The single instance
	 */
	public static PopartSourceFileKeywordRegistry getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Adds a PopartSourceFileKeyword to the registry
	 * @param sourceKeyword the PopartSourceFileKeyword to be added
	 */
	public void addSourceFileKeyword(PopartSourceFileKeyword sourceKeyword) {
		int line = sourceKeyword.getLineNr();
		
		if (line>maxLine) maxLine = line;
		lines.put(new Integer(line), sourceKeyword);
	}

	/**
	 * Returns the PopartSourceFileKeyword located on a specific line.
	 * @param lineNumber The line number
	 * @return PopartSourceFileKeyword on the specified line
	 */
	public PopartSourceFileKeyword getKeywordAtLine(int lineNumber) {
		return lines.get(new Integer(lineNumber));		
	}
	
	/**
	 * Returns the next line, after the specified line, which contains
	 * a keyword.
	 * 
	 * @param startline The line to start searching from
	 * @return The line number of the next keyword; -1 if none
	 */
	public int nextLineWithKeyword(int startline) {
		for (int i=startline;i<=maxLine;i++) {
			if (lines.get(new Integer(i))!=null) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Clears the registry.
	 */
	public void clear() {
		lines.clear();
	}
	
	/**
	 * Returns a sting representation of the contents of the registry.
	 * 
	 */
	public String toString() {
		String result = "";
		for (int i=0;i<100;i++) {
			Object a = lines.get(new Integer(i));
			if (a!=null) {
				result += "line"+i+": "+a+"\n";
			}
		} 
		return result;
	}

}
