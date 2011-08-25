package de.tud.stg.tigerseye.eclipse.core.internal;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jface.preference.IPreferenceStore;

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

    /*
     * The values are deliberately saved as strings instead of booleans. When
     * saved as boolean, the keys are deleted when they equal the default value,
     * i.e. IPreferenceStore#contains(String) returns false although the key was
     * manually set/changed.
     * 
     * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=22533
     */
    private static final String TRUE = "MYTRUE";
    private static final String FALSE = "MYFALSE";

    /**
     * 
     * @param key
     *            the id of the DSL.
     * @param store
     * @return the activation state for the key or it's default
     */
    public static Boolean getValue(String key, IPreferenceStore store) {
	if (!store.contains(key)) {
	    return TigerseyeDefaultConstants.DEFAULT_LANGUAGE_ACTIVE_VALUE;
	}
	String bool = store.getString(key);
	return parseMyBool(bool);
    }

    public static void setValue(String key, IPreferenceStore store,
            Boolean value) {
        String bool;
        if (value)
            bool = TRUE;
        else
            bool = FALSE;
        store.setValue(key, bool);
    }

    private static Boolean parseMyBool(String bool) {
	if (bool.equals(TRUE))
	    return true;
	else if (bool.equals(FALSE))
	    return false;
	else
	    throw new IllegalArgumentException("Found unexpected value: ["
		    + bool + "] where one of: "
		    + ArrayUtils.toString(new String[] { TRUE, FALSE })
		    + " was expected.");
    }

    public static Boolean getDefault() {
	return TigerseyeDefaultConstants.DEFAULT_LANGUAGE_ACTIVE_VALUE;
    }

    public static Boolean getValue(DSLDefinition key, IPreferenceStore store) {
	return getValue(getKeyFor(key), store);
    }

    public static void setValue(DSLDefinition key, IPreferenceStore store,
	    Boolean value) {
	setValue(getKeyFor(key), store, value);
    }

    public static Boolean getValue(DSLConfigurationElement key,
	    IPreferenceStore store) {
	return getValue(getKeyFor(key), store);
    }

    public static void setValue(DSLConfigurationElement key,
	    IPreferenceStore store, Boolean value) {
	setValue(getKeyFor(key), store, value);
    }

    /**
     * Defines the global interpretation of the dsl activation key for
     * definition objects
     * 
     * @param dsld
     * @return
     */
    public static String getKeyFor(DSLDefinition dsld) {
	return dsld.getLanguageKey();
    }

    /**
     * Defines the global interpretation of the dsl activation key for
     * DSLConfigurationElement objects
     * 
     * @param dsld
     * @return
     */
    public static String getKeyFor(DSLConfigurationElement dsld) {
	return dsld.getId();
    }

}
