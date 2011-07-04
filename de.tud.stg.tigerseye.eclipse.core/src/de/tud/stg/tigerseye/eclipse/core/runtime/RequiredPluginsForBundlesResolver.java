package de.tud.stg.tigerseye.eclipse.core.runtime;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation to resolve classpath for installed bundles as proposed on
 * the <a
 * href="http://www.eclipse.org/forums/index.php/mv/tree/102702/#page_top"
 * >Eclipse Community Forums</a> web site.
 * 
 * @author Leo Roos
 * 
 */
public class RequiredPluginsForBundlesResolver {

    private static final Logger logger = LoggerFactory
	    .getLogger(RequiredPluginsForBundlesResolver.class);

    /**
     * Compute the classpath entries for the symbolic plugin name provided.
     * 
     * @param pluginID
     *            The plugin on which the classpath is to be calculated.
     * @return A collection of classpath entries in URL format such as <code>
     * file:///C:/path/app.jar </code>
     * @throws IOException
     *             when a plugin dependency is not found.
     * @throws BundleException
     *             when a plugin dependency is not found.
     */
    public static Collection<String> getBundleClassPath(String pluginID)
	    throws IOException, BundleException {
	/*
	 * no duplicates, preserve order
	 */
	Collection<String> result = new LinkedHashSet<String>();
	// FIXME(Leo Roos;Jul 3, 2011) only quick and dirty. Change the actual
	// return value to be checked or without URL conversions anywhere in
	// this code
	Collection<String> asAbsolutePahts = new ArrayList<String>();
	Collection<String> bundleClassPathURLs = getBundleClassPath(pluginID,
		result, 1);
	for (String string : bundleClassPathURLs) {
	    URL url = new URL(string);
	    asAbsolutePahts.add(url.getFile());
	}
	return asAbsolutePahts;
    }

    private static Collection<String> getBundleClassPath(String pluginID,
	    Collection<String> result, int nestinglevel) throws IOException,
	    BundleException {
	Bundle bundle = Platform.getBundle(pluginID);
	if (bundle == null)
	    throw new BundleException(pluginID
		    + " cannot be retrieved fromthe Platform");
	// first the entries from this plugin itself
	result.addAll(getClassPath(bundle));
	// next the entries from dependent plugins
	String requires = (String) bundle.getHeaders().get(
		Constants.REQUIRE_BUNDLE);
	ManifestElement[] elements = ManifestElement.parseHeader(
		Constants.REQUIRE_BUNDLE, requires);
	if (elements != null) {
	    // ignore elements that are not reexported?
	    for (int i = 0; i < elements.length; ++i) {
		ManifestElement element = elements[i];
		Bundle requiredBundle = Platform.getBundle(element.getValue());
		if (requiredBundle == null) {
		    logger.error(pluginID + " requires bundle"
			    + element.getValue()
			    + " which cannot be retrieved from the Platform");
		}
		if (nestinglevel == 1) {
		    getBundleClassPath(requiredBundle.getSymbolicName(),
			    result, nestinglevel + 1);
		} else {
		    String[] visibility = element
			    .getDirectives(Constants.VISIBILITY_DIRECTIVE);
		    if (visibility != null
			    && visibility[0].equalsIgnoreCase("reexport")) {
			getBundleClassPath(requiredBundle.getSymbolicName(),
				result, nestinglevel + 1);
		    }
		}
	    }
	}
	return result;
    }

    private static Collection<String> getClassPath(Bundle bundle)
	    throws IOException, BundleException {
	Collection<String> result = new ArrayList<String>();
	String requires = (String) bundle.getHeaders().get(
		Constants.BUNDLE_CLASSPATH);
	if (requires == null)
	    requires = ".";
	ManifestElement[] elements = ManifestElement.parseHeader(
		Constants.BUNDLE_CLASSPATH, requires);
	if (elements != null) {
	    for (int i = 0; i < elements.length; ++i) {
		ManifestElement element = elements[i];
		String value = element.getValue();
		if (".".equals(value))
		    value = "/";
		URL url = bundle.getEntry(value);
		if (url != null) {
		    URL resolvedURL = FileLocator.resolve(url);
		    String filestring = FileLocator.toFileURL(resolvedURL)
			    .getFile();
		    File f = new File(filestring);
		    // URL requires trailing / if a directory
		    if (f.isDirectory() && !filestring.endsWith("/"))
			filestring += "/";
		    result.add("file://" + filestring);
		}
	    }
	}
	return result;
    }

}