package de.tud.stg.tigerseye.eclipse.core;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.jface.preference.IPreferenceStore;

import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.api.ILanguageProvider;
import de.tud.stg.tigerseye.eclipse.core.api.ITransformationProvider;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TransformationHandler;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.UnicodeLookupTable;
import de.tud.stg.tigerseye.eclipse.core.internal.LanguageProviderFactory;
import de.tud.stg.tigerseye.eclipse.core.internal.TransformationProviderImpl;

/**
 * Provides access to this plug-ins preference store. Additionally it provides
 * access to registered {@link DSLDefinition}s and
 * {@link de.tud.stg.tigerseye.eclipse.core.api.Transformation} s.
 * 
 * @author Leo Roos
 * 
 */
public class TigerseyeCore {

    /**
     * Cached lookup table, since its computation is costly
     */
    private static UnicodeLookupTable unicodeLookupTable;

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
	if (!TigerseyeCoreActivator.getDefault().isActiveDSLsLinked()) {
	    TigerseyeCoreActivator.getDefault().linkActiveDSLProjectsIntoWorkspace();
	}
	return new LanguageProviderFactory()
		.createLanguageProvider(getPreferences());
    }

    /**
     * Returns {@link ITransformationProvider} which provides access to
     * registered transformations. The provider represents the configuration at
     * the time of the method call. Therefore clients should not cache the
     * provider.
     * 
     * @return {@link ITransformationProvider} for currently configured
     *         {@link de.tud.stg.tigerseye.eclipse.core.api.Transformation} s
     */
    public static ITransformationProvider getTransformationProvider() {
	IExtensionRegistry extensionRegistry = RegistryFactory.getRegistry();
	if (extensionRegistry != null) {
	    return new TransformationProviderImpl(
		    getPreferences(),
		    extensionRegistry
			    .getConfigurationElementsFor(TransformationHandler.ID));
	} else {
	    return new TransformationProviderImpl(getPreferences(),
		    new IConfigurationElement[0]);
	}
    }

    public static UnicodeLookupTable getUnicodeLookupTable() {
	if (unicodeLookupTable == null) {
	    unicodeLookupTable = new UnicodeLookupTable()
		    .load(TigerseyeCoreActivator
			    .getUnicodeLookupTableResource());
	}
	return unicodeLookupTable;
    }

}
