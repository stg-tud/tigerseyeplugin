package de.tud.stg.tigerseye.eclipse.core.runtime;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;

/**
 * Thin wrapper object around the utility class {@link FileLocator}.
 * Additionally provides some other utility functions.
 * 
 * @author Leo Roos
 * 
 */
public class FileLocatorWrapper {

    /**
     * @see org.eclipse.core.runtime.FileLocator#getBundleFile(Bundle)
     */
    public File getBundleFile(Bundle bundle) throws IOException {
	return FileLocator.getBundleFile(bundle);
    }



}
