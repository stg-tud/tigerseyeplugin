package de.tud.stg.tigerseye.eclipse.core.internal;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import de.tud.stg.tigerseye.eclipse.core.api.ClassLoaderStrategy;
import de.tud.stg.tigerseye.eclipse.core.api.TigerseyeRuntimeException;

public class BundleClassloaderStrategy implements ClassLoaderStrategy {

    private final String contributor;

    public BundleClassloaderStrategy(String contributor) {
	this.contributor = contributor;
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
	Bundle bundle = Platform.getBundle(contributor);
	if (bundle == null)
	    throw new TigerseyeRuntimeException("Could not access bundle "
		    + contributor + " to load class " + className);
	Class<?> loadClass = bundle.loadClass(className);
	return loadClass;
    }

}
