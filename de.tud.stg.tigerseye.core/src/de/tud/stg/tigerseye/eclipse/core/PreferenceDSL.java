package de.tud.stg.tigerseye.eclipse.core;

import org.eclipse.jface.preference.IPreferenceStore;



/**
 * A thin {@link DSLDefinition} wrapper for convenient handling when adjusting
 * corresponding preferences. <br>
 * Subclasses should call {@link #setNeedsStoring()} when appropriate, should
 * override {@link #store()} and {@link #delete()} to perform specific attribute
 * modification to the store but must call the super methods afterwards to
 * ensure a consistent state.
 * 
 * @author Leo Roos
 * 
 */
public abstract class PreferenceDSL {

    private final DSLDefinition dsl;
    private boolean changed;
    private IPreferenceStore store;

    public PreferenceDSL(DSLDefinition dsl, IPreferenceStore store) {
	this.changed = false;
	this.dsl = dsl;
	this.store = store;
    }

    /**
     * subclasses may override the default does nothing
     */
    public void store() {
    }

    public void setPreferenceStore(IPreferenceStore store) {
	this.store = store;
    }

    public DSLDefinition getDsl() {
	return dsl;
    }

    protected void setNeedsStoring() {
	this.changed = true;
    }

    public boolean needsStoring() {
	return changed;
    }

    protected IPreferenceStore getStore() {
	if (store == null)
	    throw new IllegalStateException("No store set");
	return store;
    }

    @Override
    public String toString() {
	return getClass().getName() + "[" + dsl.toString() + "]";
    }

    /**
     * Forwards to the equals method of the underlying {@link DSLDefinition}
     */
    @Override
    public boolean equals(Object obj) {
	if (obj instanceof PreferenceDSL) {
	    PreferenceDSL other = (PreferenceDSL) obj;
	    return this.getDsl().equals(other.getDsl());
	}
	return false;
    }

    @Override
    public int hashCode() {
	return getDsl().hashCode();
    }

}
