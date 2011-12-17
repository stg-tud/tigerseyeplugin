package de.tud.stg.tigerseye.eclipse.core.internal;

import org.eclipse.jface.preference.IPreferenceStore;

import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.api.DSLKey;
import de.tud.stg.tigerseye.eclipse.core.api.TigerseyeDefaultConstants;

/**
 * Internal class to access the activation state of DSLs. The definition of the
 * string variable {@Code key} is defined by
 * {@link DSLConfigurationElement#getId()}. The client approach for resolving
 * any property of a DSL is via one of the {@link DSLKey} implementations. <br>
 * This class is necessary to get the state of DSL bundle or workspace project
 * with minimal information about it.
 * 
 * @author Leo_Roos
 * 
 */
public class DSLActivationState {

    private final PreferenceBool prefBool;

    public DSLActivationState(IPreferenceStore store) {
	prefBool = new PreferenceBool(TigerseyeCore.getPreferences());
    }

    /**
     * 
     * @param key
     *            the id of the DSL.
     * @param store
     * @return the activation state for the key or it's default
     */
    public Boolean getValue(String key) {
	return prefBool.getValue(key, getDefault());
    }

    public void setValue(String key, boolean value) {
	prefBool.setValue(key, value);
    }

    public Boolean getDefault() {
	return TigerseyeDefaultConstants.DEFAULT_LANGUAGE_ACTIVE_VALUE;
    }

    public Boolean getValue(DSLDefinition key) {
	return getValue(getKeyFor(key));
    }

    public void setValue(DSLDefinition key, Boolean value) {
	setValue(getKeyFor(key), value);
    }

    public Boolean getValue(DSLConfigurationElement key) {
	return getValue(getKeyFor(key));
    }

    public void setValue(DSLConfigurationElement key, Boolean value) {
	setValue(getKeyFor(key), value);
    }

    /**
     * Defines the global interpretation of the dsl activation key for
     * definition objects
     * 
     * @param dsl
     * @return
     */
    public String getKeyFor(DSLDefinition dsl) {
	return dsl.getLanguageKey();
    }

    /**
     * Defines the global interpretation of the dsl activation key for
     * DSLConfigurationElement objects
     * 
     * @param dslc
     * @return
     */
    public String getKeyFor(DSLConfigurationElement dslc) {
	return dslc.getId();
    }

    public boolean storeContainsKeyFor(DSLDefinition dsl) {
	return prefBool.storeContainsKey(getKeyFor(dsl));
    }

}
