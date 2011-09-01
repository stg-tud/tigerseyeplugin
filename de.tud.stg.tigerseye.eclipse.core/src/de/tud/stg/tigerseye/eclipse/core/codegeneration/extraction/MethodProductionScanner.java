package de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.eclipse.core.runtime.Assert;

import de.tud.stg.tigerseye.eclipse.core.codegeneration.typeHandling.ConfigurationOptionDefaults;

public class MethodProductionScanner implements
	Iterable<MethodProductionElement> {

    private Pattern getProductionWhitespacePattern(String methodWhitespaceEscape) {
	return Pattern.compile("(?:\\Q" + methodWhitespaceEscape + "\\E){1,2}");
    }

    private Pattern getProductionParameterPattern(String methodParameterEscape) {
	return Pattern.compile("\\Q" + methodParameterEscape + "\\E(\\d+)");
    }

    /**
     * maps whitespace escape strings to the according pattern
     */
    private static Map<String, Pattern> whitespacePatterncache = new ConcurrentHashMap<String, Pattern>();
    /**
     * maps parameter escape strings to the according pattern
     */
    private static Map<String, Pattern> parameterEscapePatterncache = new ConcurrentHashMap<String, Pattern>();

    private @Nonnull
    String whitespaceEscape;
    private @Nonnull
    String parameterEscape;
    private String currentProduction;
    private Pattern whitespaceEscapePattern;
    private Pattern parameterEscapePattern;
    /**
     * describes next position to read
     */
    private int currentPosition;
    /**
     * the initialized whitespace matcher
     */
    private Matcher wsMatcher;
    /**
     * the initialized parameter matcher
     */
    private Matcher pMatcher;
    private int currentProductionLength;

    public @Nonnull
    MethodProductionScanner(String whitespaceEscape, String parameterEscape) {
	configureWhitespaceEscapeState(whitespaceEscape);
	configureParameterEscapeState(parameterEscape);
    }

    private void configureWhitespaceEscapeState(String whitespaceEscape) {
	this.whitespaceEscape = whitespaceEscape;
	this.whitespaceEscapePattern = newOrFromCacheWSE(whitespaceEscape);
    }

    private void configureParameterEscapeState(String parameterEscape) {
	this.parameterEscape = parameterEscape;
	this.parameterEscapePattern = newOrFromCachePE(parameterEscape);
    }

    private Pattern newOrFromCachePE(String pe) {
	Pattern pattern = parameterEscapePatterncache.get(pe);
	if (pattern == null) {
	    Pattern ppe = getProductionParameterPattern(pe);
	    parameterEscapePatterncache.put(pe, ppe);
	    return ppe;
	} else {
	    return pattern;
	}
    }

    private Pattern newOrFromCacheWSE(String wse) {
	Pattern pattern = whitespacePatterncache.get(wse);
	if (pattern == null) {
	    Pattern pwse = getProductionWhitespacePattern(wse);
	    whitespacePatterncache.put(wse, pwse);
	    return pwse;
	} else {
	    return pattern;
	}
    }

    public MethodProductionScanner() {
	this(ConfigurationOptionDefaults.DEFAULT_WHITESPACE_ESCAPE,
		ConfigurationOptionDefaults.DEFAULT_PARAMETER_ESCAPE);
    }

    /**
     * Change the parameter escape string.
     * 
     * @param pse
     *            the parameter escape character
     * @throws IllegalStateException
     *             if scan process already started
     */
    public void setParameterEscape(String pse) {
	checkIfRunning();
	configureParameterEscapeState(pse);
    }

    private void checkIfRunning() {
	if (this.currentProduction != null)
	    throw new IllegalStateException(
		    "values can not be changed after the scan processes started");
    }

    /**
     * Change the whitespace escape string.
     * 
     * @param wse
     * @throws IllegalStateException
     *             if scan process already started
     */
    public void setWhitespaceEscape(String wse) {
	checkIfRunning();
	configureWhitespaceEscapeState(wse);
    }

    /**
     * @return used whitespace escape
     */
    public String getWhitespaceEscape() {
	return whitespaceEscape;
    }

    /**
     * @return used Parameter escape
     */
    public String getParameterEscape() {
	return parameterEscape;
    }

    /**
     * Resets the scanner to work with {@code production} Once the scan process
     * has started whitespace and parameter escape can no longer be changed
     * 
     * @param production
     */
    public void startScan(String production) {
	Assert.isNotNull(production);
	this.currentProduction = production;
	this.currentProductionLength = currentProduction.length();
	wsMatcher = this.whitespaceEscapePattern.matcher(production);
	pMatcher = this.parameterEscapePattern.matcher(production);
	reset();
    }

    /**
     * Resets the current scan process.
     */
    public void reset() {
	this.currentPosition = 0;
    }

    /**
     * @return the currently processed production or <code>null</code> if none
     *         is set.
     */
    public @CheckForNull
    String getCurrentProduction() {
	return currentProduction;
    }

    @Override
    public Iterator<MethodProductionElement> iterator() {
	return new Iterator<MethodProductionElement>() {
	    MethodProductionScanner iterated = MethodProductionScanner.this;

	    @Override
	    public boolean hasNext() {
		return iterated.hasNext();
	    }

	    @Override
	    public MethodProductionElement next() {
		return iterated.next();
	    }

	    /**
	     * Not supported by this implementation
	     */
	    @Override
	    public void remove() {
		throw new UnsupportedOperationException();
	    }
	};
    }

    /**
     * @return the next element
     * @throws IllegalStateException
     *             if no more elements can be processed, i.e. hasNext() returns
     *             <code>false</code>
     */
    public MethodProductionElement next() {
	if (!hasNext())
	    throw new IllegalStateException("No more elements to process");

	MethodProductionElement result;
	int nextWhitespacePos = Integer.MAX_VALUE;
	int nextParameterPos = Integer.MAX_VALUE;

	// XXX(Leo_Roos;Aug 31, 2011) could/should I cache the results of ws and
	// p matcher?
	if (wsMatcher.find(currentPosition))
	    nextWhitespacePos = wsMatcher.start();
	if (pMatcher.find(currentPosition))
	    nextParameterPos = pMatcher.start();

	if (nextWhitespacePos <= currentPosition) {
	    int endIndex = wsMatcher.end();
	    String nextSubstring = nextSubstring(endIndex);
	    result = new WhitespaceElement().setCapturedAndEscape(
		    nextSubstring, whitespaceEscape);

	} else if (nextParameterPos <= currentPosition) {
	    int end = pMatcher.end();
	    result = new ParameterElement().setCapturedAndEscape(
		    nextSubstring(end), parameterEscape);
	} else {
	    int endIndex = min(nextWhitespacePos, nextParameterPos,
		    currentProductionLength);
	    String nextToken = nextSubstring(endIndex);
	    result = new KeywordElement().setCapturedString(nextToken);
	}
	return result;
    }

    private int min(int i1, int i2, int i3) {
	int min1 = Math.min(i1, i2);
	int minf = Math.min(min1, i3);
	return minf;
    }

    /**
     * Returns substring from current Position till endPos exclusively and sets
     * current Position to endPos
     * 
     * @param endIndex
     * @return
     */
    private String nextSubstring(int endIndex) {
	int oldCurrentPos = currentPosition;
	currentPosition = endIndex;
	return currentProduction.substring(oldCurrentPos, endIndex);
    }

    public boolean hasNext() {
	if (currentProduction == null) {
	    return false;
	} else {
	    return currentPosition < currentProduction.length();
	}
    }

}
