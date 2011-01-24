package de.tud.stg.tigerseye.core.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.tud.stg.tigerseye.core.TigerseyeCore;

/**
 * TigerseyePreferenceInitializer initializes default preference values for
 * Tigerseye preference pages.
 * 
 */
public class TigerseyePreferenceInitializer extends AbstractPreferenceInitializer {

    public static final String DEFAULT_OUTPUT_DIRECTORY_NAME = "src-popart";
    private static final boolean DEFAULT_LANGUAGE_ACTIVE_VALUE = false;

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
}