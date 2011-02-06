package de.tud.stg.tigerseye.eclipse.core;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;

import de.tud.stg.popart.builder.transformers.Transformation;
import de.tud.stg.tigerseye.eclipse.core.internal.LanguageProviderImpl;
import de.tud.stg.tigerseye.eclipse.core.internal.TransformationProviderImpl;

/**
 * Provides access to this plug-ins preference store. Additionally it provides
 * access to registered {@link DSLDefinition}s and {@link Transformation}s.
 * 
 * @author Leo Roos
 * 
 */
public class TigerseyeCore {

    public static IPreferenceStore getPreferences() {
	return TigerseyeCoreActivator.getDefault().getPreferenceStore();
    }

    /**
     * Provides the object which gives access to registered DSLs. Clients should
     * not cache the language provider, since it might change when new DSL
     * plug-ins are added or old ones removed.
     * 
     * @return an updated language provider
     */
    public static ILanguageProvider getLanguageProvider() {
	return new LanguageProviderImpl(TigerseyeCore.getPreferences(),
		Platform.getExtensionRegistry().getConfigurationElementsFor(
			"de.tud.stg.tigerseye.dslDefinitions"));
    }

    /**
     * Returns {@link ITransformationProvider} which provides access to
     * registered transformations. The provider represents the configuration at
     * the time of the method call. Therefore clients should not cache the
     * provider.
     * 
     * @return {@link ITransformationProvider} for currently configured
     *         {@link Transformation}s
     */
    public static ITransformationProvider getTransformationProvider() {
	return new TransformationProviderImpl(getPreferences(), Platform
		.getExtensionRegistry().getConfigurationElementsFor(
			TransformationHandler.ID));
    }

}
