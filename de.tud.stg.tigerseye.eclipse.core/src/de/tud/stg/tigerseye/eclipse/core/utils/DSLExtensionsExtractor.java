package de.tud.stg.tigerseye.eclipse.core.utils;

import java.io.File;

import javax.annotation.Nonnull;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
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

    public String[] getExtensionsForSrcResource(String resourceSrcName) {
	resourceSrcName = new File(resourceSrcName).getName();

	FileType typeForSrcResource = FileTypeHelper
.getTypeForSrcResource(resourceSrcName);
	return getExtensionForSrcResource(resourceSrcName, typeForSrcResource);
    }

    public String[] getExtensionForSrcResource(String resourceSrcName, FileType typeForSrcResource) {
	String[] split = resourceSrcName.split("\\.");
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
