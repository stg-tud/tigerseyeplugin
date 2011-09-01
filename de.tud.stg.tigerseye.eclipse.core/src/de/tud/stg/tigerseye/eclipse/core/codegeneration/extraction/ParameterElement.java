package de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction;

public class ParameterElement extends MethodProductionElement {

    private String parameterEscape;
    private String capturedString;
    private int parsedParameterNumber;

    @Override
    public ProductionElement getProductionElementType() {
	return ProductionElement.Parameter;
    }

    public String getParameterEscape() {
	validateInitialized();
	return parameterEscape;
    }

    public int getParsedParameterNumber() {
	validateInitialized();
	return parsedParameterNumber;
    }

    @Override
    protected String doGetCapturedString() {
	validateInitialized();
	return capturedString;
    }

    @Override
    protected boolean isInitialized() {
	return isNotNull(parameterEscape, capturedString);
    }

    /**
     * @param capturedString
     * @param parameterEscape
     * @return
     * @throws IllegalArgumentException
     *             if parameters are an obviously wrong combination
     * @throws NumberFormatException
     *             if number in capturedString can not be parsed
     */
    public ParameterElement setCapturedAndEscape(String capturedString,
	    String parameterEscape) {
	this.capturedString = capturedString;
	this.parameterEscape = parameterEscape;
	validate();
	String number = capturedString.substring(parameterEscape.length());
	int parseInt = Integer.parseInt(number);
	this.parsedParameterNumber = parseInt;
	return this;
    }

    private void validate() {
	boolean startsWith = capturedString.startsWith(parameterEscape);
	if (!startsWith)
	    throw new IllegalArgumentException(
		    "Bad combination, escape character " + parameterEscape
			    + " is not prefix of parameter declaration"
			    + capturedString);
    }

}
