package de.tud.stg.tigerseye.eclipse.core.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;
import de.tud.stg.tigerseye.eclipse.core.api.TigerseyeDefaultConstants;

/**
 * TigerseyePreferenceInitializer initializes default preference values for
 * Tigerseye preference pages.
 * 
 */
public class TigerseyePreferenceInitializer extends
	AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {

	IPreferenceStore store = TigerseyeCore.getPreferences();

	store.setDefault(
		TigerseyePreferenceConstants.TIGERSEYE_OUTPUT_FOLDER_PATH_KEY,
		TigerseyeDefaultConstants.DEFAULT_OUTPUT_DIRECTORY_NAME);

	store.setDefault(
		TigerseyePreferenceConstants.DEFAULT_LANGUAGE_ACTIVE_KEY,
		TigerseyeDefaultConstants.DEFAULT_LANGUAGE_ACTIVE_VALUE);

    }

}