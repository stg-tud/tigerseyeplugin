package de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction;

import de.tud.stg.tigerseye.eclipse.core.codegeneration.utils.WhitespaceCategoryDefinition;

/**
 * Describes a whitespace region in a production. Usually a string comprised of
 * exactly one or whitespace escape strings. The WSE for this element can be
 * queried with {@link #getWhitespaceEscape()}.
 * 
 * @author Leo_Roos
 * 
 */
public class WhitespaceElement extends MethodProductionElement {

    private String whitespaceEscape;
    private String capturedString;
    private boolean optional;

    @Override
    public ProductionElement getProductionElementType() {
	return ProductionElement.Whitespace;
    }

    /**
     * @return the original whitespace character
     */
    public String getWhitespaceEscape() {
	validateInitialized();
	return whitespaceEscape;
    }

    /**
     * Whether this object represents an optional whitespace string capture.
     * 
     * @return whether captured string is optional
     * @see WhitespaceCategoryDefinition
     */
    public boolean isOptional() {
	validateInitialized();
	return this.optional;
    }

    @Override
    public boolean isInitialized() {
	return isNotNull(capturedString, whitespaceEscape);
    }

    /**
     * @param capturedString
     * @param whitespaceEscape
     * @return
     * @throws IllegalArgumentException
     *             if bad combination is passed
     */
    public WhitespaceElement setCapturedAndEscape(String capturedString,
	    String whitespaceEscape) {
	this.capturedString = capturedString;
	this.whitespaceEscape = whitespaceEscape;
	validateCombination();
	return this;
    }

    private void validateCombination() {
	String currentSubstring = capturedString;
	validateStartsWithWSE(currentSubstring);
	String substring = capturedString.substring(whitespaceEscape.length());
	if (substring.isEmpty()) {
	    this.optional = false;
	    return;
	}
	validateStartsWithWSE(substring);
	String shouldBeEmpty = substring.substring(whitespaceEscape
		.length());
	if (shouldBeEmpty.isEmpty()) {
	    this.optional = true;
	} else
	    throwBadCombination();
    }

    private void validateStartsWithWSE(String currentSubstring) {
	boolean shouldStartWith = currentSubstring.startsWith(whitespaceEscape);
	if (!shouldStartWith)
	    throwBadCombination();
    }

    private void throwBadCombination() {
	throw new IllegalArgumentException(
		"Passed values make no sense: Captured String" + capturedString
			+ " must only consist of " + whitespaceEscape);
    }

    @Override
    protected String doGetCapturedString() {
	validateInitialized();
	return this.capturedString;
    }

}
