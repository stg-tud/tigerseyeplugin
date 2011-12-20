package de.tud.stg.tigerseye.eclipse.core.api;

import javax.annotation.Nonnull;

import org.eclipse.jface.preference.IPreferenceStore;

import de.tud.stg.tigerseye.eclipse.core.internal.PreferenceBool;

public abstract class AbstractTransformatinType implements TransformationType {

    private final PreferenceBool prefBool;
    private final IPreferenceStore store;

    public AbstractTransformatinType(IPreferenceStore store) {
	this.store = store;
	this.prefBool = createPreferenceBool(store);
    }

    protected IPreferenceStore getPreferenceStore() {
	return store;
    }

    protected @Nonnull
    PreferenceBool createPreferenceBool(IPreferenceStore store) {
	return new PreferenceBool(store);
    }

    /**
     * Returns a string representing the preference for key for this handler for
     * the passed {@link TransformationType} object
     * 
     * @param identifiable
     *            the object associated to this transformation
     * @return preference key
     */
    public String getPreferenceKeyFor(ITransformationHandler handler) {
	return getIdentifier() + handler.getIdentifier();
    }

    protected abstract String getIdentifier();

    @Override
    public void setActiveStateFor(ITransformationHandler handler, boolean value) {
	prefBool.setValue(getPreferenceKeyFor(handler), value);
    }

    @Override
    public boolean isActiveFor(ITransformationHandler handler) {
	return prefBool.getValue(getPreferenceKeyFor(handler), getDefaultActiveFor(handler));
    }

}