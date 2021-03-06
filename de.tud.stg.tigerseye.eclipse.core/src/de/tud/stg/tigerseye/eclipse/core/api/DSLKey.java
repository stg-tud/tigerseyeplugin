package de.tud.stg.tigerseye.eclipse.core.api;

import org.eclipse.jface.preference.IPreferenceStore;

import de.tud.stg.tigerseye.eclipse.core.preferences.TigerseyePreferenceConstants;


/**
 * A class to centralize the naming scheme of properties for different DSLs. If
 * a new property for a DSL is introduced the responsible plug-in should extend
 * this class and provide the key as a static final field. <br>
 * 
 * @author Leo Roos
 * @see {@link DSLDefinition#getValue(DSLKey)},
 *      {@link DSLDefinition#setValue(DSLKey, Object)},
 *      {@link DSLDefinition#setToDefault(DSLKey)}
 */
public abstract class DSLKey<T> {

    private static final String SUFFIX_LANGUAGE = "_language";
    private static final String SUFFIX_EXTENSION = "_extension";

    /**
     * Key to access the configured extension name for a DSL.
     */
    public static final DSLKey<String> EXTENSION = new ExtensionDSLKey();

    /**
     * Key to access the configured active state of a DSL
     */
    // public static final DSLKey<Boolean> LANGUAGE_ACTIVE = new
    // ActivatedDSLKey();

    /**
     * Not representing any kind of key to anything. Can be used instead of
     * <code>null</code> when no valid key is available.
     */
    public static final DSLKey<?> NULL_KEY = new NullDSLKey();

    /**
     * The property key suffix for a DSL and the attribute represented by the
     * current DSLKey instance.
     */
    public final String suffix;

    /**
     * Intentionally only possible to instantiate from subclasses.<br>
     * 
     * The {@code suffix} string is used in combination with the language key to
     * define a unique property name for every property of each DSL.
     * 
     * @param suffix
     *            the suffix used in combination with
     *            {@link DSLDefinition#getLanguageKey()} to serve as preference
     *            store key.
     */
    protected DSLKey(String suffix) {
	this.suffix = suffix;
    }

    /**
     * A convenience method which returns the value for this key for a given DSL
     * from the given preference store.
     * 
     * @param dsl
     * @param store
     * @return a validated attribute
     * @throws NoLegalPropertyFoundException
     *             if property can not be found
     */
    public abstract T getValue(DSLDefinition dsl, IPreferenceStore store)
	    throws NoLegalPropertyFoundException;

    /**
     * A convenience method to set the value for this key for a given DSL in the
     * given preference store.
     * 
     * @param dsl
     * @param store
     */
    public abstract void setValue(DSLDefinition dsl, IPreferenceStore store,
	    T value);

    /**
     * Returns the default value for the {@code DSLKey} of {@code this} object.
     * The default value for one attribute can be the same for all DSLs but may
     * also vary specific to every DSL. Therefore the {@code DSLDefinition} dsl
     * has to be always passed as argument although it might not always be
     * necessary to determine the default value.
     * 
     * @param dsl
     * @param store
     * @return the default value for the specific {@link DSLDefinition}
     *         {@code dsl} and the passed {@link IPreferenceStore} {@code store}
     *         .
     */
    public abstract T getDefault(DSLDefinition dsl, IPreferenceStore store);

    /**
     * @param dsl
     * @return the final preference store key for the given
     *         {@link DSLDefinition} {@code dsl} and the instance of this
     *         {@code DSLKey}
     */
    protected String key(DSLDefinition dsl) {
	return dsl.getKeyFor(this);
    }

    @Override
    public String toString() {
	return getClass().getSimpleName() + "[" + suffix + "]";
    }

    public static boolean getDefaultLanguageActiveValue(IPreferenceStore store) {
        return store
		.getDefaultBoolean(TigerseyePreferenceConstants.DEFAULT_LANGUAGE_ACTIVE_KEY);
    }

    /**
     * Whether the DSL Bundles should be refreshed before a transformation takes
     * place. Depending on the size of a DSL bundle and the amount of different
     * DSL bundles such a operation could cause noticeable performance
     * reduction.
     * 
     * @param store
     *            the Tigerseye store
     * @return <code>true</code> if a DSL bundle should be reloaded and
     *         <code>false</code> otherwise.
     */
    public static boolean isReloadDSLPackageBeforeTransforamtionActive(
	    IPreferenceStore store) {
	return store
		.getBoolean(TigerseyePreferenceConstants.DEFAULT_RELOAD_DSL_CLASSES_BEFORE_TRANSFORMATION_KEY);
    }

    private final static class ExtensionDSLKey extends DSLKey<String> {

	protected ExtensionDSLKey() {
	    super(SUFFIX_EXTENSION);
	}

	@Override
	public String getValue(DSLDefinition dsl, IPreferenceStore store)
		throws NoLegalPropertyFoundException {
	    String extension = store.getString(key(dsl));
	    if (extension.isEmpty())
		throw new NoLegalPropertyFoundException();
	    return extension;
	}

	@Override
	public void setValue(DSLDefinition dsl, IPreferenceStore store,
		String value) {
	    store.setValue(key(dsl), value);
	}

	@Override
	public String getDefault(DSLDefinition dsl, IPreferenceStore store) {
	    return store.getDefaultString(key(dsl));
	}

    }


    private static final class NullDSLKey extends DSLKey<Object> {

	public NullDSLKey() {
	    super("_UNDEFINED_DSLKEY_");
	}

	@Override
	public Object getValue(DSLDefinition dsl, IPreferenceStore store) {
	    return new Object();
	}

	@Override
	public void setValue(DSLDefinition dsl, IPreferenceStore store,
		Object value) {
	}

	@Override
	public Object getDefault(DSLDefinition dsl, IPreferenceStore store) {
	    return new Object();
	}

    };

}
