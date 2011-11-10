package de.tud.stg.tigerseye.eclipse.core.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler.ResourceHandlingHelper;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileTypeHelper;

/**
 * Extracts all extensions declaring a corresponding {@link DSLDefinition} for
 * the corresponding file.
 * 
 * @author Leo Roos
 * 
 */
@Nonnull
public class DSLExtensionsExtractor {

    private static final Logger logger = LoggerFactory
	    .getLogger(DSLExtensionsExtractor.class);

    public DSLExtensionsExtractor() {
    }

    /**
     * @param resourceSrcName
     *            name to determine DSL extensions from.
     * @return extensions extracted from {@code resourceSrcName}. Result maybe
     *         an empty array.
     */
    public String[] getExtensionsForSrcResource(IFile file) {

	try {
	    String content = IOUtils.toString(new InputStreamReader(file.getContents()));
	    List<String> determineInvolvedDSLNames = ResourceHandlingHelper.determineInvolvedDSLNames(file, content,
		    TigerseyeCore.getLanguageProvider(), FileType.TIGERSEYE);
	    return determineInvolvedDSLNames.toArray(new String[determineInvolvedDSLNames.size()]);
	} catch (IOException e) {
	    logger.warn("failed to determine dsls for editor", e);
	} catch (CoreException e) {
	    logger.warn("failed to determine dsls for editor", e);
	}
	return new String[0];
    }

    public String[] getExtensionsForSrcResource(String resourceSrcName) {
	resourceSrcName = new File(resourceSrcName).getName();
	String[] split = resourceSrcName.split("\\.");

	FileType typeForSrcResource = FileTypeHelper
.getTypeForSrcResource(resourceSrcName);
	if (typeForSrcResource == null) {
	    return new String[0];
	}
	String[] resultString = new String[0];
	if (FileType.TIGERSEYE.equals(typeForSrcResource)) {
	    resultString = (String[]) ArrayUtils.subarray(split, 1, split.length - 1);
	}
	return resultString;
    }

    /**
     * @see #getExtensionsForSrcResource(String)
     */
    public String[] getExtensionsForOutputResource(String resourceSrcName) {
	String outputNameForSourceName = new OutputPathHandler("")
		.getSourceNameForOutputName(resourceSrcName);
	if (outputNameForSourceName == null)
	    return new String[0];
	return getExtensionsForSrcResource(outputNameForSourceName);
    }

}
