package de.tud.stg.tigerseye.eclipse.core.preferences;

import static de.tud.stg.popart.builder.transformers.FileType.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.tud.stg.popart.builder.transformers.FileType;
import de.tud.stg.popart.builder.transformers.TransformationType;
import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;

/**
 * TigerseyePreferenceInitializer initializes default preference values for
 * Tigerseye preference pages.
 * 
 */
public class TigerseyePreferenceInitializer extends
	AbstractPreferenceInitializer {

    public static final FileType[] RESOURCE_FILE_TYPES = { JAVA, GROOVY, POPART };
    public static final FileType[] DSL_FILETYPES = { DSL };
    public static final String DEFAULT_OUTPUT_DIRECTORY_NAME = "src-tigerseye";
    public static final boolean DEFAULT_LANGUAGE_ACTIVE_VALUE = false;
    public static final boolean DEFAULT_TRANSFORMER_FOR_RESOURCES_ACTIVATION_STATE = true;
    public static final boolean DEFAULT_TRANSFORMER_FOR_DSLS_ACTIVATION_STATE = false;

    public static final Map<TransformationType, Boolean> DEFAULT_TRANSFORMATION_ACTIVATION = getDefaultTransformationValueMap();

    @Override
    public void initializeDefaultPreferences() {

	IPreferenceStore store = TigerseyeCore.getPreferences();

	store.setDefault(
		TigerseyePreferenceConstants.TIGERSEYE_OUTPUT_FOLDER_PATH,
		DEFAULT_OUTPUT_DIRECTORY_NAME);

	store.setDefault(
		TigerseyePreferenceConstants.DEFAULT_LANGUAGE_ACTIVE_KEY,
		DEFAULT_LANGUAGE_ACTIVE_VALUE);

    }

    private static Map<TransformationType, Boolean> getDefaultTransformationValueMap() {
	HashMap<TransformationType, Boolean> map = new HashMap<TransformationType, Boolean>();
	for (FileType fileType : RESOURCE_FILE_TYPES) {
	    map.put(fileType,
		    TigerseyePreferenceInitializer.DEFAULT_TRANSFORMER_FOR_RESOURCES_ACTIVATION_STATE);
	}
	for (FileType fileType : DSL_FILETYPES) {
	    map.put(fileType,
		    TigerseyePreferenceInitializer.DEFAULT_TRANSFORMER_FOR_DSLS_ACTIVATION_STATE);
	}
	return Collections.unmodifiableMap(map);
    }


}