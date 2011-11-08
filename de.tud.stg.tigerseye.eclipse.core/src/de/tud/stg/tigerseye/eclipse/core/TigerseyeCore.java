package de.tud.stg.tigerseye.eclipse.core;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

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
import de.tud.stg.tigerseye.eclipse.core.preferences.TigerseyePreferenceConstants;

/**
 * Provides access to this plug-ins preference store. Additionally it provides
 * access to registered {@link DSLDefinition}s and
 * {@link de.tud.stg.tigerseye.eclipse.core.api.Transformation} s.
 * 
 * @author Leo Roos
 * 
 */
public class TigerseyeCore {

    private static final String unicodeLookupTablePath = "UnicodeLookupTable.txt";
    /**
     * Cached lookup table, since its computation is costly
     * <p>
     * since multiple threads might access the table at the same time it is made
     * volatile to guarantee correct lazy initialization
     */
    private volatile static UnicodeLookupTable unicodeLookupTable;
    private static ILanguageProvider langaugeProvider;

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
	if (langaugeProvider == null) {
	    updateLanguageProvider();
	}
	return langaugeProvider;
    }

    /**
     * creates an updated language provider and updates the cached instance. The
     * provider reflects possibly changed extension point configurations.
     * 
     * @return a new language provider
     */
    public static ILanguageProvider updateLanguageProvider() {
	langaugeProvider = new LanguageProviderFactory().createLanguageProvider(getPreferences());
	return langaugeProvider;
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
	    return new TransformationProviderImpl(getPreferences(),
		    extensionRegistry.getConfigurationElementsFor(TransformationHandler.ID));
	} else {
	    return new TransformationProviderImpl(getPreferences(), new IConfigurationElement[0]);
	}
    }

    public static UnicodeLookupTable getUnicodeLookupTable() {
	if (unicodeLookupTable == null) {
	    unicodeLookupTable = new UnicodeLookupTable().load(getUnicodeLookupTableResource());
	}
	return unicodeLookupTable;
    }

    /**
     * @return a reader for the unicodeLookupTable resource, which defines the
     *         mapping for special character naming.
     * 
     * @throws IllegalStateException
     *             if lookup table can not be retrieved
     */
    private static Reader getUnicodeLookupTableResource() {
	if (TigerseyeCoreActivator.isRunning()) {
	    // active plug-in -> load as bundle resource
	    TigerseyeCoreActivator plugin = TigerseyeCoreActivator.getDefault();
	    URL entry = plugin.getBundle().getEntry(unicodeLookupTablePath);
	    if (entry == null)
		throw new IllegalStateException("Could not resolve entry for" + unicodeLookupTablePath);
	    try {
		InputStream openStream = entry.openStream();
		return new InputStreamReader(openStream, "UTF-8");
	    } catch (UnsupportedEncodingException e) {
		throw new IllegalStateException(e);
	    } catch (IOException e) {
		throw new IllegalStateException(e);
	    }
	} else {
	    // plug-in not activated try to retrieve from project root
	    try {
		return new FileReader(unicodeLookupTablePath);
	    } catch (FileNotFoundException e) {
		throw new IllegalStateException("Could not resolve resource " + unicodeLookupTablePath);
	    }
	}
    }

    public static String getOutputDirectoryPath() {
	String outputfolder = getPreferences().getString(TigerseyePreferenceConstants.TIGERSEYE_OUTPUT_FOLDER_PATH_KEY);
	return outputfolder;
    }

}
