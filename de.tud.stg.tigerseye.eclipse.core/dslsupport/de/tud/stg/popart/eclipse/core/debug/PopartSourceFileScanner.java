package de.tud.stg.popart.eclipse.core.debug;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartLiteralKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartSourceFileKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartSourceFileLiteralKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartSourceFileOperationKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartSourceFileStructuredElementKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartStructuredElementKeyword;

/**
 * Parses a Popart source file and adds the found keywords represented as
 * PopartSourceFileKeywords to the PopartSourceFileKeywordRegistry.
 * 
 * @author David Marx
 * @author Thorsten Peter
 */
public class PopartSourceFileScanner {

	private Stack<PopartSourceFileStructuredElementKeyword> stack = new Stack<PopartSourceFileStructuredElementKeyword>();
	private int lineNumber = 1;
	
	/**
	 * Scans the specified Popart source file for keywords and adds them
	 * to to PopartSourceFileKeywordRegistry.
	 * 
	 * @param file The Popart source file to be scanned
	 */
	public void scanFile(IFile file) {
		PopartSourceFileKeywordRegistry.getInstance().clear();
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(file.getContents()));
			String line = null;			
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (!line.startsWith("*") && !line.startsWith("/*")
						&& !line.equals("")) {
					processLine(line);					
				}
				lineNumber++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}	
	}

	/**
	 * Searches in the trimmed line for keywords
	 * @param line The line
	 */
	private void processLine(String line) {
		
		String firstToken = getFirstToken(line);

		PopartKeyword keyword = PopartKeywordRegistry.getInstance().getKeywordByName(firstToken);

		PopartSourceFileKeyword sourceFileKeyword = null;
		
		// found a DSL keyword
		if (keyword != null) {
			
			sourceFileKeyword = createSourceFileKeyword(keyword);
			
			// if inside structured element, add current keyword to surrounding structured element
			if (!stack.isEmpty()) {
				PopartSourceFileStructuredElementKeyword parent = stack.peek();
				parent.add(sourceFileKeyword);
			}
			
			// now push current keyword on stack if it is a structured element									
			if (keyword instanceof PopartStructuredElementKeyword) {
				stack.push((PopartSourceFileStructuredElementKeyword) sourceFileKeyword);
			}
			
			PopartSourceFileKeywordRegistry.getInstance().addSourceFileKeyword(sourceFileKeyword);
		}
		// found "}"
		if (firstToken.equals("}")) {
			if (!stack.isEmpty()) {
				// pop element from stack and set endline-number to current line
				PopartSourceFileStructuredElementKeyword se = stack.pop();							
				se.setEndLine(lineNumber);
			}
		}
	}

	/**
	 * Given a PopartKeyword, returns a PopartSourceFileKeyword which contains
	 * the line number it was found on.
	 * 
	 * @param keyword The PopartKeyword
	 * @return The PopartSourceFileKeyword
	 */
	private PopartSourceFileKeyword createSourceFileKeyword(
			PopartKeyword keyword) {
		if (keyword instanceof PopartLiteralKeyword) {
			return new PopartSourceFileLiteralKeyword(
					(PopartLiteralKeyword) keyword, lineNumber);
		}
		if (keyword instanceof PopartOperationKeyword) {
			return new PopartSourceFileOperationKeyword(
					(PopartOperationKeyword) keyword, lineNumber);
		}
		if (keyword instanceof PopartStructuredElementKeyword) {
			return new PopartSourceFileStructuredElementKeyword(
					(PopartStructuredElementKeyword) keyword, lineNumber);
			
		}
		return null;
	}

	/**
	 * Returns the first token in the specified line.
	 * @param line The line
	 * @return The first token in the line
	 */
	private String getFirstToken(String line) {
		String result;

		int whitespace = line.indexOf(' ');
		int bracket = line.indexOf('(');

		if (whitespace == -1 && bracket == -1) {
			return line;
		}
		if (bracket > -1) {
			return line.substring(0, line.indexOf('('));
		} else {
			if (whitespace > -1) {
				return line.substring(0, line.indexOf(' '));
			}
		}

		return "";
	}

}
