package de.tud.stg.tigerseye.eclipse.core.api;

import org.eclipse.jface.preference.IPreferenceStore;

public class DSLTransformation extends AbstractTransformatinType {

    private final DSLDefinition dsl;

    public DSLTransformation(DSLDefinition dsl, IPreferenceStore store) {
	super(store);
	this.dsl = dsl;
    }

    @Override
    public boolean getDefaultActiveFor(ITransformationHandler handler) {
	return TigerseyeDefaultConstants.DEFAULT_TRANSFORMER_FOR_DSLS_ACTIVATION_STATE;
    }

    @Override
    protected String getIdentifier() {
	return dsl.getLanguageKey() + "TransformationContext";
    }

}
