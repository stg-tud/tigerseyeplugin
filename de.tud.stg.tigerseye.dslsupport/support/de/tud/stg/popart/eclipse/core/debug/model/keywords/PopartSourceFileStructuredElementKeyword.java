package de.tud.stg.popart.eclipse.core.debug.model.keywords;

import java.util.ArrayList;

/**
 * A PopartSourceFileStructuredElementKeyword can be thought of as an instance
 * of a PopartStructuredElementKeyword. It represents an operation keyword
 * in a Popart source file.
 *
 * @author David Marx
 * @author Thorsten Peter
 */
public class PopartSourceFileStructuredElementKeyword extends PopartSourceFileKeyword {
	
	private int endLineNr = -1;

	public ArrayList<PopartSourceFileKeyword> keywords = new ArrayList<PopartSourceFileKeyword>();
	
	public PopartSourceFileStructuredElementKeyword(PopartStructuredElementKeyword keyword, int startLineNr) {
		super(keyword, startLineNr);
	}
	
	public PopartSourceFileStructuredElementKeyword(PopartStructuredElementKeyword keyword, int startLineNr, int endLineNr) {
		super(keyword, startLineNr);
		this.endLineNr = endLineNr;
	}
	
	public void add(PopartSourceFileKeyword keyword) {
		keyword.setParent(this);
		keywords.add(keyword);
	}
	
	public void setEndLine(int endLineNr) {
		this.endLineNr = endLineNr;
	}
	
	public int getEndLine() {
		return endLineNr;
	}

	public String toString() {
		String result = "[";
		result += "keyword: "+getKeyword().getName()+" endline:"+ endLineNr+ " parent: "+getParent();
		result += "]";
		return result;		
	}
	
	public boolean isLastKeyword(PopartSourceFileKeyword theKeyword) {		
		return (keywords.get(keywords.size()-1)).equals(theKeyword);
	}

	public PopartSourceFileKeyword getKeyword(int i) {
		return keywords.get(i);		
	}
	
	public int numChildren() {
		return keywords.size();
	}

	public  PopartSourceFileKeyword getLastKeyword() {
		return keywords.get(keywords.size()-1);
	}

	
}
