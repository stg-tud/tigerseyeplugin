package de.tud.stg.tigerseye.eclipse.core.api;

import org.eclipse.jface.preference.IPreferenceStore;

import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;

public interface ITransformationHandler {

    void setPreferenceStore(IPreferenceStore store);

    /**
     * @return the registered {@link Transformation} object.
     */
    Transformation getTransformation();

    /**
     * @return The user friendly name under which the {@link Transformation}
     *         object of this handler has been registered.
     */
    String getName();

    boolean supports(FileType type);

    /**
     * @return the unique identifier for this Transformation
     */
    String getIdentifier();

    /**
     * Returns a string representing the preference for key for this handler for
     * the passed {@link TransformationType} object
     * 
     * @param identifiable
     *            the object associated to this transformation
     * @return preference key
     */
    String getPreferenceKeyFor(TransformationType identifiable);

    /**
     * @param identifiable
     * @return whether the transformation is active for {@code identifiable}.
     */
    boolean isActiveFor(TransformationType identifiable);

    /**
     * Set the active of this transformation for the passed {@code identifiable}
     * .
     * 
     * @param identifiable
     * @param value
     */
    void setActiveStateFor(TransformationType identifiable, boolean value);

}