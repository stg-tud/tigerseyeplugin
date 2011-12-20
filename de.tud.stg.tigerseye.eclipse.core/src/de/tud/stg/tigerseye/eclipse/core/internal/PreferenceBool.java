package de.tud.stg.tigerseye.eclipse.core.internal;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jface.preference.IPreferenceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreferenceBool {

    private static final Logger logger = LoggerFactory.getLogger(PreferenceBool.class);

    private final IPreferenceStore store;

    public PreferenceBool(IPreferenceStore preferences) {
	this.store = preferences;

    }

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
     * @param key
     * @param defaultValue
     *            if key not found or content not parsable default will be
     *            returned
     * @return
     */
    public Boolean getValue(String key, boolean defaultValue) {
	if (!storeContainsKey(key)) {
	    return defaultValue;
	}
	String bool = store.getString(key);
	return parseMyBool(bool, defaultValue);
    }

    public boolean storeContainsKey(String key) {
	return store.contains(key);
    }

    private Boolean parseMyBool(String bool, boolean defaultValue) {
	if (bool.equals(TRUE))
	    return true;
	else if (bool.equals(FALSE))
	    return false;
	else {
	    boolean def = defaultValue;
	    logger.error("Found unexpected value: [{}] where one of: {} was expected. Will return default {}",
		    new Object[] { bool, ArrayUtils.toString(new String[] { TRUE, FALSE }), def });
	    return def;
	}
    }

    public void setValue(String key, boolean value) {
	String bool;
	if (value)
	    bool = TRUE;
	else
	    bool = FALSE;
	store.setValue(key, bool);
    }
}
