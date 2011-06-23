package de.tud.stg.tigerseye.eclipse.core.preferences;

import static de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TransformationType;

/**
 * {@code TigerseyePreferenceConstants} defines keys to reference values in the
 * preference store.
 * 
 * @author Yevgen Fanshil
 * @author Leonid Melnyk
 * @author Leo Roos
 */
public class TigerseyePreferenceConstants {

    public static final String TIGERSEYE_OUTPUT_FOLDER_PATH_KEY = "de.tud.stg.tigerseye.keyword.outputfolder.path";

    public static final String DEFAULT_LANGUAGE_ACTIVE_KEY = "de.tud.stg.tigerseye.languageisactive.default";

    /*
     * XXX could move default strings where possible to a properties file, seems
     * too much configuration for now
     */
    public static final FileType[] RESOURCE_FILE_TYPES = { JAVA, GROOVY, TIGERSEYE };

    public static final FileType[] DSL_FILETYPES = { DSL };

    public static final String DEFAULT_OUTPUT_DIRECTORY_NAME = "src-tigerseye";

    public static final boolean DEFAULT_LANGUAGE_ACTIVE_VALUE = true;

    public static final boolean DEFAULT_TRANSFORMER_FOR_RESOURCES_ACTIVATION_STATE = true;

    public static final boolean DEFAULT_TRANSFORMER_FOR_DSLS_ACTIVATION_STATE = false;

    public static final Map<TransformationType, Boolean> DEFAULT_TRANSFORMATION_ACTIVATION = getDefaultTransformationValueMap();

    private static Map<TransformationType, Boolean> getDefaultTransformationValueMap() {
	HashMap<TransformationType, Boolean> map = new HashMap<TransformationType, Boolean>();
	for (FileType fileType : TigerseyePreferenceConstants.RESOURCE_FILE_TYPES) {
	    map.put(fileType,
		    TigerseyePreferenceConstants.DEFAULT_TRANSFORMER_FOR_RESOURCES_ACTIVATION_STATE);
	}
	for (FileType fileType : TigerseyePreferenceConstants.DSL_FILETYPES) {
	    map.put(fileType,
		    TigerseyePreferenceConstants.DEFAULT_TRANSFORMER_FOR_DSLS_ACTIVATION_STATE);
	}
	return Collections.unmodifiableMap(map);
    }

}