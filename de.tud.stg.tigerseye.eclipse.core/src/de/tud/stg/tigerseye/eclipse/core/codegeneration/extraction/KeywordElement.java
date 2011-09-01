package de.tud.stg.tigerseye.eclipse.core.codegeneration.extraction;


public class KeywordElement extends MethodProductionElement {

    private String capturedString;

    public KeywordElement() {
    }

    public KeywordElement setCapturedString(String capturedString) {
	this.capturedString = capturedString;
	return this;
    }

    @Override
    public ProductionElement getProductionElementType() {
	return ProductionElement.Keyword;
    }

    @Override
    protected String doGetCapturedString() {
	validateInitialized();
	return this.capturedString;
    }

    @Override
    protected boolean isInitialized() {
	return isNotNull(capturedString);
    }

}
