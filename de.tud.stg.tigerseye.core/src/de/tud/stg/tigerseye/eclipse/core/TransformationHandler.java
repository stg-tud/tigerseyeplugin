package de.tud.stg.tigerseye.eclipse.core;

import javax.annotation.Nonnull;

import org.eclipse.jface.preference.IPreferenceStore;

import de.tud.stg.popart.builder.transformers.FileType;
import de.tud.stg.popart.builder.transformers.Transformation;
import de.tud.stg.popart.builder.transformers.TransformationType;
import de.tud.stg.tigerseye.eclipse.core.preferences.TigerseyePreferenceInitializer;

/**
 * This class wraps actual {@link Transformation} objects and provides access to
 * meta data such as preference values.
 * 
 * @author Leo Roos
 * 
 */
@Nonnull
public class TransformationHandler {

    /**
     * The id for the transformations extension point
     */
    public static final String ID = "de.tud.stg.tigerseye.transformers";

    private final Transformation transformation;
    private final String name;
    private final String contributor;

    private IPreferenceStore store;

    public TransformationHandler(String contributor, String name,
	    Transformation transformation) {
	this.contributor = contributor;
	this.name = name;
	this.transformation = transformation;
    }

    public void setPreferenceStore(IPreferenceStore store) {
	this.store = store;
    }

    /**
     * @return the registered {@link Transformation} object.
     */
    public Transformation getTransformation() {
	return this.transformation;
    }

    /**
     * @return The user friendly name under which the {@link Transformation}
     *         object of this handler has been registered.
     */
    public String getName() {
	return this.name;
    }

    public boolean supports(TransformationType type) {
	return getTransformation().getSupportedFileTypes().contains(
		type.getTransformationCategory());
    }

    /**
     * @return the unique identifier for this Transformation
     */
    public String getIdentifier() {
	return this.contributor + getTransformation().getClass().getName();
    }

    /**
     * Returns a string representing the preference for key for this handler for
     * the passed {@link TransformationType} object
     * 
     * @param identifiable
     *            the object associated to this transformation
     * @return preference key
     */
    public String getPreferenceKeyFor(TransformationType identifiable) {
	return getIdentifier() + identifiable.getIdentifer();
    }

    /**
     * @param identifiable
     * @return whether the transformation is active for {@code identifiable}.
     */
    public boolean isActiveFor(TransformationType identifiable) {
	if (!supports(identifiable))
	    return false;
	String preferenceKeyFor = getPreferenceKeyFor(identifiable);
	if (!getStore().contains(preferenceKeyFor)) {
	    getStore()
.setDefault(preferenceKeyFor,
		    getDefaultFor(identifiable.getTransformationCategory()));
	}
	boolean active = getStore().getBoolean(
		preferenceKeyFor);
	return active;
    }

    /**
     * The default value
     * 
     * @param identifiable
     * @return
     */
    public static boolean getDefaultFor(FileType identifiable) {
	Boolean defBool = TigerseyePreferenceInitializer.DEFAULT_TRANSFORMATION_ACTIVATION
		.get(identifiable);		
	return defBool == null ? false : defBool;
    }

    /**
     * Set the active of this transformation for the passed {@code identifiable}
     * .
     * 
     * @param identifiable
     * @param value
     */
    public void setActiveStateFor(TransformationType identifiable, boolean value) {
	getStore().setValue(getPreferenceKeyFor(identifiable), value);
    }


    private IPreferenceStore getStore() {
	if (store == null)
	    throw new IllegalStateException(
		    "Preference store has not been initialized");
	return store;
    }

    @Override
    public String toString() {
	return getClass().getSimpleName() + "[" + getName() + "]";
    }

}
