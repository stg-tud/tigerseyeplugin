package de.tud.stg.tigerseye.eclipse.core.utils;

import java.io.File;

import javax.annotation.Nonnull;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.DSLDefinition;
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
    public String[] getExtensionsForSrcResource(String resourceSrcName) {
	resourceSrcName = new File(resourceSrcName).getName();
	String[] split = resourceSrcName.split("\\.");
	if (split.length < 3) {
	    logger.warn("name: " + resourceSrcName
		    + " is not a valid dsl file name.");
	    return new String[0];
	}
	FileType typeForSrcResource = FileTypeHelper
		.getTypeForSrcResource(resourceSrcName);
	if (typeForSrcResource == null) {
	    return new String[0];
	}
	    String[] resultString = new String[0];
	if (FileType.TIGERSEYE.equals(typeForSrcResource)) {
	    resultString = (String[]) ArrayUtils.subarray(split, 1,
		    split.length - 1);
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
