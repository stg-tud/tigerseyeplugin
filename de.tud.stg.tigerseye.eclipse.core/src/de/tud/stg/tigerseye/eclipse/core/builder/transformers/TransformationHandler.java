package de.tud.stg.tigerseye.eclipse.core.builder.transformers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.eclipse.jface.preference.IPreferenceStore;

import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.api.ITransformationHandler;
import de.tud.stg.tigerseye.eclipse.core.api.TigerseyeDefaultConstants;
import de.tud.stg.tigerseye.eclipse.core.api.Transformation;
import de.tud.stg.tigerseye.eclipse.core.api.TransformationType;

/**
 * This class wraps actual {@link Transformation} objects and provides access to
 * meta data such as preference values.
 * 
 * @author Leo Roos
 * 
 */
@Nonnull
public class TransformationHandler implements ITransformationHandler {

    private static final Map<TransformationType, Boolean> DEFAULT_TRANSFORMATION_ACTIVATION = getDefaultTransformationValueMap();

    /**
     * The id for the transformations extension point
     */
    public static final String ID = "de.tud.stg.tigerseye.transformers";
    /**
     * The default value
     * 
     * @param identifiable
     * @return
     */
    public static boolean getDefaultActivationStateFor(TransformationType identifiable) {
	Boolean defBool = TransformationHandler.DEFAULT_TRANSFORMATION_ACTIVATION
		.get(identifiable);
	return defBool == null ? false : defBool;
    }
    public static Map<TransformationType, Boolean> getDefaultTransformationValueMap() {
        HashMap<TransformationType, Boolean> map = new HashMap<TransformationType, Boolean>();
        for (FileType fileType : FileType.RESOURCE_FILE_TYPES) {
            map.put(fileType,
        	    TigerseyeDefaultConstants.DEFAULT_TRANSFORMER_FOR_RESOURCES_ACTIVATION_STATE);
        }
        for (FileType fileType : FileType.DSL_FILETYPES) {
            map.put(fileType,
        	    TigerseyeDefaultConstants.DEFAULT_TRANSFORMER_FOR_DSLS_ACTIVATION_STATE);
        }
        return Collections.unmodifiableMap(map);
    }

    private final String contributor;

    private final String name;

    private IPreferenceStore store;

    private final Transformation transformation;

    public TransformationHandler(String contributor, String name,
	    Transformation transformation) {
	this.contributor = contributor;
	this.name = name;
	this.transformation = transformation;
    }

    /* (non-Javadoc)
     * @see de.tud.stg.tigerseye.eclipse.core.builder.transformers.ITransformationHandler#getIdentifier()
     */
    @Override
    public String getIdentifier() {
	return this.contributor + getTransformation().getClass().getName();
    }

    /* (non-Javadoc)
     * @see de.tud.stg.tigerseye.eclipse.core.builder.transformers.ITransformationHandler#getName()
     */
    @Override
    public String getName() {
	return this.name;
    }

    /* (non-Javadoc)
     * @see de.tud.stg.tigerseye.eclipse.core.builder.transformers.ITransformationHandler#getPreferenceKeyFor(de.tud.stg.tigerseye.eclipse.core.api.TransformationType)
     */
    @Override
    public String getPreferenceKeyFor(TransformationType identifiable) {
	return getIdentifier() + identifiable.getIdentifer();
    }

    private IPreferenceStore getStore() {
	if (store == null)
	    throw new IllegalStateException(
		    "Preference store has not been initialized");
	return store;
    }

    /* (non-Javadoc)
     * @see de.tud.stg.tigerseye.eclipse.core.builder.transformers.ITransformationHandler#getTransformation()
     */
    @Override
    public Transformation getTransformation() {
	return this.transformation;
    }

    /* (non-Javadoc)
     * @see de.tud.stg.tigerseye.eclipse.core.builder.transformers.ITransformationHandler#isActiveFor(de.tud.stg.tigerseye.eclipse.core.api.TransformationType)
     */
    @Override
    public boolean isActiveFor(TransformationType identifiable) {
	if (!supports(identifiable))
	    return false;
	String preferenceKeyFor = getPreferenceKeyFor(identifiable);
	if (!getStore().contains(preferenceKeyFor)) {
	    getStore()
		    .setDefault(preferenceKeyFor, getDefaultActivationStateFor(identifiable));
	}
	boolean active = getStore().getBoolean(preferenceKeyFor);
	return active;
    }

    /* (non-Javadoc)
     * @see de.tud.stg.tigerseye.eclipse.core.builder.transformers.ITransformationHandler#setActiveStateFor(de.tud.stg.tigerseye.eclipse.core.api.TransformationType, boolean)
     */
    @Override
    public void setActiveStateFor(TransformationType identifiable, boolean value) {
	getStore().setValue(getPreferenceKeyFor(identifiable), value);
    }

    /* (non-Javadoc)
     * @see de.tud.stg.tigerseye.eclipse.core.builder.transformers.ITransformationHandler#setPreferenceStore(org.eclipse.jface.preference.IPreferenceStore)
     */
    @Override
    public void setPreferenceStore(IPreferenceStore store) {
	this.store = store;
    }

    /* (non-Javadoc)
     * @see de.tud.stg.tigerseye.eclipse.core.builder.transformers.ITransformationHandler#supports(de.tud.stg.tigerseye.eclipse.core.api.TransformationType)
     */
    @Override
    public boolean supports(TransformationType type) {
	Set<TransformationType> supportedFileTypes = getTransformation()
		.getSupportedFileTypes();
	/*
	 * FIXME: consider refactoring for FileType split into physical
	 * representation (file extension) an conceptual (a DSL Language
	 * provider); When is the type neither Filetype nor DSLDefinitionImpl
	 */
	if (type instanceof DSLDefinition)
	    return supportedFileTypes.contains(FileType.DSL);
	//
	if (type instanceof FileType)
	    return supportedFileTypes.contains(type);

	throw new IllegalArgumentException(
		"An object of not anticipated kind has been passed: " + type);
    }

    @Override
    public String toString() {
	return getClass().getSimpleName() + "[" + getName() + "]";
    }

}
