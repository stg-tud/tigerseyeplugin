package de.tud.stg.tigerseye.eclipse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.eclipse.core.runtime.Plugin;

import de.tud.stg.tigerseye.eclipse.dslsupport.TigerseyeSupportActivator;

//FIXME refactoring and tests
public class TigerseyeLibraryProvider extends Plugin {

	public static final String PLUGIN_ID = "de.tud.stg.tigerseye";

	private static TigerseyeLibraryProvider plugin;

	private static final String path = "resources/MathClassEx-12.txt";;

	public TigerseyeLibraryProvider() {
		plugin = this;
	}

	public static TigerseyeLibraryProvider getDefault() {
		return plugin;
	}
	
	/**
	 * @return a reader for the MathClassEx-11 resource, which defines the mapping for special character naming.
	 */
	public static InputStreamReader getMathClassEx11(){
		
		URL entry = getDefault().getBundle().getEntry(path);
		if(entry == null)
			throw new IllegalStateException("Could not resolve entry for" + path);
		try {
			InputStream openStream = entry.openStream();
			return new InputStreamReader(openStream, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException();
		} catch (IOException e) {
			throw new IllegalStateException();
		}
	}
	

	/**
	 * @return the Files representing the minimal dependencies for a project with the
	 *         Tigerseye nature.
	 * @throws IOException if a problem occurred while resolving the locations of the runtime libraries. 
	 */
	public static File[] getTigerseyeRuntimeLibraries() throws IOException {
		File runtimeSupportJar = TigerseyeSupportActivator.getDefault().getRuntimeSupportJar();
		return new File[]{runtimeSupportJar};
	}

}
