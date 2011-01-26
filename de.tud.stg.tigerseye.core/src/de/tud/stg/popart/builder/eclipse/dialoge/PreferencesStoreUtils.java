package de.tud.stg.popart.builder.eclipse.dialoge;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.preference.IPreferenceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.popart.builder.transformers.FileType;

public class PreferencesStoreUtils {
private static final Logger logger = LoggerFactory.getLogger(PreferencesStoreUtils.class);


	private static final String ROOT = "transformerConfiguration";

	public static void storeConfiguration(IPreferenceStore prefs,
			String extension, Map<String, Boolean> map) {

		if (isHostLanguageExtension(extension)) {
			logger.info("storing map for " + extension);
			storeHostLanguageConfiguration(prefs, extension, map);
			return;
		}

		int numDSLExtensions = prefs.getInt(ROOT + "_numDSLs");

		if (numDSLExtensions == IPreferenceStore.INT_DEFAULT_DEFAULT) {
			numDSLExtensions = 0;
		}

		for (int i = 0; i < numDSLExtensions; i++) {
			String dslName = prefs.getString(ROOT + "_DSL_" + i + "_name");

			if (dslName.equals(extension)) {
				int numTransformers = map.size();
				prefs.setValue(ROOT + "_DSL_" + i + "_numTransformers", numTransformers);

				int j = 0;
				for (Entry<String, Boolean> entry : map.entrySet()) {
					prefs.setValue(ROOT + "_DSL_TRANSFORMER_" + j + "_name", entry.getKey());
					prefs.setValue(ROOT + "_DSL_TRANSFORMER_" + j + "_checked", entry.getValue());
					j++;
				}

				return;
			}
		}

		numDSLExtensions++;
		prefs.setValue(ROOT + "_numDSLs", numDSLExtensions);
		prefs.setValue(ROOT + "_DSL_" + (numDSLExtensions - 1) + "_name", extension);

		storeConfiguration(prefs, extension, map);
	}

	private static boolean isHostLanguageExtension(String extension) {
		return extension.equals(FileType.JAVA.name()) || extension.equals(FileType.GROOVY.name())
				|| extension.equals(FileType.POPART.name());
	}

	private static void storeHostLanguageConfiguration(IPreferenceStore prefs,
			String extension, Map<String, Boolean> map) {
		int numTransformers = map.size();
		prefs.setValue(ROOT + "_" + extension + "_numTransformers", numTransformers);

		int j = 0;
		for (Entry<String, Boolean> entry : map.entrySet()) {
			prefs.setValue(ROOT + "_" + extension + "_TRANSFORMER_" + j + "_name", entry.getKey());
			prefs.setValue(ROOT + "_" + extension + "_TRANSFORMER_" + j + "_checked", entry.getValue());
			j++;
		}
	}

	public static Map<String, Map<String, Boolean>> getConfiguration(
			IPreferenceStore prefs) {
		Map<String, Map<String, Boolean>> map = new HashMap<String, Map<String, Boolean>>();

		int numDSLExtensions = prefs.getInt(ROOT + "_numDSLs");

		for (int i = 0; i < numDSLExtensions; i++) {
			String dslName = prefs.getString(ROOT + "_DSL_" + i + "_name");
			int numTransformers = prefs.getInt(ROOT + "_DSL_" + i + "_numTransformers");

			Map<String, Boolean> dslMap = new HashMap<String, Boolean>();
			map.put(dslName, dslMap);

			for (int j = 0; j < numTransformers; j++) {
				String transformerName = prefs.getString(ROOT + "_DSL_TRANSFORMER_" + j + "_name");
				boolean checked = prefs.getBoolean(ROOT + "_DSL_TRANSFORMER_" + j + "_checked");

				dslMap.put(transformerName, checked);
			}
		}

	return getHostLanguageConfiguration(prefs, map);
	}

    private static Map<String, Map<String, Boolean>> getHostLanguageConfiguration(
	    IPreferenceStore prefs,
			Map<String, Map<String, Boolean>> map) {
		FileType[] types = { FileType.JAVA, FileType.GROOVY, FileType.POPART };

		for (FileType ft : types) {
			int numTransformers = prefs.getInt(ROOT + "_" + ft.name() + "_numTransformers");

			Map<String, Boolean> dslMap = new HashMap<String, Boolean>();
			map.put(ft.name(), dslMap);

			for (int j = 0; j < numTransformers; j++) {
				String transformerName = prefs.getString(ROOT + "_" + ft.name() + "_TRANSFORMER_" + j + "_name");
				boolean checked = prefs.getBoolean(ROOT + "_" + ft.name() + "_TRANSFORMER_" + j + "_checked");

				dslMap.put(transformerName, checked);
			}
		}
	return map;
	}
}
