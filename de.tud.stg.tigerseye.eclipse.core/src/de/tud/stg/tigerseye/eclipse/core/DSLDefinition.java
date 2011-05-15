package de.tud.stg.tigerseye.eclipse.core;

import javax.annotation.Nonnull;

import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TransformationType;
import de.tud.stg.tigerseye.eclipse.core.runtime.TigerseyeRuntimeException;

/**
 * General interface to handle registered DSLs between plug-in components. <br>
 * Only unmodifiable values which every DSL definition has are directly
 * accessible <br>
 * Further DSL attributes can be retrieved using the {@link #getValue(DSLKey)}
 * method and set using the {@link #setValue(DSLKey, Object)}. This class
 * provides a preference store key which can be used to directly accessing the
 * preference store for requested attributes through {@link #getKeyFor(DSLKey)}.
 * But the use of the getValue and setValue methods is recommended.<br>
 * 
 * @see {@link DSLKey}
 * 
 * @author Leo Roos
 * 
 */
@Nonnull
public interface DSLDefinition extends TransformationType {

    /**
     * DSL with no behavior or state.
     */
    public static final DSLDefinition NULL_DSL = new NULLDSL();

    /**
     * Whether the language has been set to active.
     * 
     * @return <code>true</code> if language is active <code>false</code>
     *         otherwise.
     */
    public abstract boolean isActive();

    /**
     * The class path of the class implementing the
     * {@link de.tud.stg.popart.dslsupport.DSL} interface, which represents the
     * evaluation class of this DSL.
     * 
     * @return
     */
    public abstract String getClassPath();

    /**
     * The identifier of the plug-in providing this DSL
     * 
     * @return
     */
    public abstract String getContributorSymbolicName();

    /**
     * The user friendly name of this DSL.
     * 
     * @return
     */
    public String getDslName();

    /**
     * Loads class with fully qualified name {@link #getClassPath()} from bundle
     * described by {@link #getContributorSymbolicName()}.
     * 
     * @return the loaded Class of this {@code DSLDefinition}
     * @throws TigerseyeRuntimeException
     *             if class can not be loaded, which will encapsulate a
     *             {@link ClassNotFoundException}. This will usually be
     *             prevented because the existence of the class should have been
     *             checked during initialization.
     */
    public abstract Class<? extends de.tud.stg.popart.dslsupport.DSL> loadClass();

    /**
     * The unique identifier preference store key for this DSL.
     * 
     * @return
     */
    public abstract String getLanguageKey();

    /**
     * Returns the preference store key of this DSL for the attribute specified
     * by {@code key}.
     * 
     * @param key
     *            the identifier for the required DSL attribute
     * @return the final preference store key to access the value of the
     *         attribute defined through {@code key} for this DSL.
     */
    public abstract String getKeyFor(DSLKey<?> key);


    /**
     * Sets the attribute of this DSL described by {@code key} to its default.
     * 
     * @param key
     */
    public void setToDefault(DSLKey<?> key);

    /**
     * Set attribute of type {@code T} identified through {@code key} for this
     * DSL.
     * 
     * @param <T>
     *            Type of attribute to set.
     * @param key
     *            describing what preference to set for this DSL
     * @param value
     *            the value to set for key {@code key} of this DSL.
     */
    public <T> void setValue(DSLKey<T> key, T value);

    /**
     * Gets value of type {@code T} from the preference store.
     * 
     * @param <T>
     *            Type of return value.
     * @param key
     *            describing what preference to get for this DSL
     * @return the attribute of type {@code T} for key {@code key} for this DSL.
     * @throws NoLegalPropertyFound
     */
    public <T> T getValue(DSLKey<T> key) throws NoLegalPropertyFound;

    static class NULLDSL implements DSLDefinition {
	private NULLDSL() {
	}

	@Override
	public String getLanguageKey() {
	    return "";
	}

	@Override
	public String getKeyFor(DSLKey<?> key) {
	    return "";
	}

	@Override
	public String getDslName() {
	    return "";
	}

	@Override
	public String getContributorSymbolicName() {
	    return "";
	}

	@Override
	public String getClassPath() {
	    return "";
	}

	@Override
	public void setToDefault(DSLKey<?> key) {
	}

	@Override
	public <T> void setValue(DSLKey<T> key, T value) {
	}

	@Override
	public <T> T getValue(DSLKey<T> key) throws NoLegalPropertyFound {
	    return null;
	}

	@Override
	public boolean isActive() {
	    return false;
	}

	@Override
	public Class<? extends de.tud.stg.popart.dslsupport.DSL> loadClass() {
	    return de.tud.stg.popart.dslsupport.DSL.class;
	}

	@Override
	public String getIdentifer() {
	    return "";
	}

    }

}