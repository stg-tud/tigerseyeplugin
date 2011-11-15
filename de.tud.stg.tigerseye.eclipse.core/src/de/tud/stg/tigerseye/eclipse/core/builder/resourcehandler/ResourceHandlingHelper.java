package de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler;

import java.io.IOException;

import javax.annotation.CheckForNull;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceHandlingHelper {

    private static final Logger logger = LoggerFactory
	    .getLogger(ResourceHandlingHelper.class);

    /**
     * FIXME(Leo_Roos;Aug 27, 2011) copied from DSLResourceHandler#readResource
     * 
     * @param resource
     * @return
     */
    public static @CheckForNull
    StringBuffer readResource(IFile resource) {
	try {
	    String stringFromReader = IOUtils.toString(resource.getContents(), resource.getCharset());
	    return new StringBuffer(stringFromReader);
	} catch (IOException e) {
	    logger.error("Failed to read resource.", e);
	} catch (CoreException e) {
	    logger.error("Failed to obtain content of specified resource.", e);
	}
	return null;
    }



}
