package de.tud.stg.tigerseye.eclipse.core.internal;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import de.tud.stg.tigerseye.eclipse.core.api.ClassLoaderStrategy;

public class BundleClassloaderStrategy implements ClassLoaderStrategy {

    private final String contributor;

    public BundleClassloaderStrategy(String contributor) {
	this.contributor = contributor;
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
	Bundle bundle = Platform.getBundle(contributor);
	if (bundle == null)
	    throw new ClassNotFoundException("Could not access bundle " + contributor + " to load class " + className);
	try {
	    Class<?> loadClass = bundle.loadClass(className);
	    return loadClass;
	} catch (ClassNotFoundException e) {
	    throw e;
	} catch (Throwable e) {
	    throw new ClassNotFoundException("Unexpected problem while trying to load class " + className
		    + " of bundle " + bundle + ":" + e.getMessage(), e);
	}
    }

}
