package de.tud.stg.tigerseye.eclipse.core.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface to consistently handle DSL definitions independent from their
 * definition location.
 * <p>
 * Only unmodifiable values which every DSL definition has are directly
 * accessible <br>
 * Further DSL attributes can be retrieved using the {@link #getValue(DSLKey)}
 * method and set using the {@link #setValue(DSLKey, Object)}. This class
 * provides a preference store key which can be used to directly access the
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
    static final DSLDefinition NULL_DSL = new NULLDSL();

    /**
     * Whether the language has been set to active.
     * 
     * @return <code>true</code> if language is active <code>false</code>
     *         otherwise.
     */
    abstract boolean isActive();

    /**
     * The class path of the class implementing the
     * {@link de.tud.stg.popart.dslsupport.DSL} interface, which represents the
     * evaluation class of this DSL.
     * 
     * @return
     */
    public String getClassPath();

    /**
     * The identifier of the plug-in providing this DSL
     * 
     * @return
     */
    @Deprecated
    public String getContributorSymbolicName();

    /**
     * @return the contributor of this dslDefinition
     */
    DSLContributor getContributor();

    /**
     * The user friendly name of this DSL.
     * 
     * @return
     */
    String getDslName();

    /**
     * Gets the loaded class with fully qualified name {@link #getClassPath()}
     * from contributor described by {@link #getContributor()}. If the class is
     * not load-able as defined by {@link #isDSLClassLoadable()}
     * <code>null</code> will be returned instead. When
     * {@link #isDSLClassLoadable()} is <code>true</code> the loaded class will
     * be returned
     * 
     * @return the loaded Class of this {@code DSLDefinition} or
     *         <code>null</code> if it's not load-able.
     */
    @Nullable
    Class<? extends de.tud.stg.popart.dslsupport.DSL> getDSLClassChecked();

    /**
     * @return <code>true</code> if class can be loaded via
     *         {@link #getDSLClassChecked()}
     */
    public boolean isDSLClassLoadable();

    /**
     * The unique identifier preference store key for this DSL.
     * 
     * @return
     */
    String getLanguageKey();

    /**
     * Returns the preference store key of this DSL for the attribute specified
     * by {@code key}.
     * 
     * @param key
     *            the identifier for the required DSL attribute
     * @return the final preference store key to access the value of the
     *         attribute defined through {@code key} for this DSL.
     */
    String getKeyFor(DSLKey<?> key);

    /**
     * Sets the attribute of this DSL described by {@code key} to its default.
     * 
     * @param key
     */
    void setToDefault(DSLKey<?> key);

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
    <T> void setValue(DSLKey<T> key, T value);

    /**
     * Gets value of type {@code T} from the preference store.
     * 
     * @param <T>
     *            Type of return value.
     * @param key
     *            describing what preference to get for this DSL
     * @return the attribute of type {@code T} for key {@code key} for this DSL.
     * @throws NoLegalPropertyFoundException
     */
    <T> T getValue(DSLKey<T> key) throws NoLegalPropertyFoundException;

    void setActive(boolean active);

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
	public <T> T getValue(DSLKey<T> key)
		throws NoLegalPropertyFoundException {
	    return null;
	}

	@Override
	public boolean isActive() {
	    return false;
	}

	@Override
	public Class<? extends de.tud.stg.popart.dslsupport.DSL> getDSLClassChecked() {
	    return de.tud.stg.popart.dslsupport.DSL.class;
	}

	@Override
	public String getIdentifer() {
	    return "";
	}

	@Override
	public DSLContributor getContributor() {
	    return null;
	}

	@Override
	public void setActive(boolean active) {
	}

	@Override
	public boolean isDSLClassLoadable() {
	    return true;
	}

    }

}