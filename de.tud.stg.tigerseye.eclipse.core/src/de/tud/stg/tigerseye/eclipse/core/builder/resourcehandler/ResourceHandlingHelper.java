package de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.popart.builder.eclipse.EDSL;
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.api.DSLKey;
import de.tud.stg.tigerseye.eclipse.core.api.DSLNotFoundException;
import de.tud.stg.tigerseye.eclipse.core.api.ILanguageProvider;
import de.tud.stg.tigerseye.eclipse.core.api.NoLegalPropertyFoundException;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.AnnotationExtractor;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;

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

    /**
     * Determines involved DSLs
     * 
     * @param resource
     * @param input
     * @return
     * @throws DSLNotFoundException
     */
    public static Set<DSLDefinition> determineInvolvedDSLs(IFile resource,
	    StringBuffer input, ILanguageProvider languageProvider,
	    FileType interestedInFiletype) throws DSLNotFoundException {
	Set<DSLDefinition> determinedDSLs = new HashSet<DSLDefinition>();

	int fileExtensionIndex = resource.getName().lastIndexOf(
		interestedInFiletype.srcFileEnding);
	if (fileExtensionIndex < 1) {
	    throw new DSLNotFoundException(
		    "No dsl extension could be determined for " + resource);
	}

	String[] str = resource.getName().substring(0, fileExtensionIndex - 1)
		.split("\\.");

	List<int[]> edslAnnotations = new LinkedList<int[]>();

	if (str.length > 1) {
	    for (int i = 1; i < str.length; i++) {
		String dslName = str[i];

		DSLDefinition activeDSLForExtension = languageProvider
			.getActiveDSLForExtension(dslName);
		if (activeDSLForExtension != null) {
		    try {
			addDSLToContext(activeDSLForExtension, determinedDSLs);
		    } catch (NoLegalPropertyFoundException e) {
			throw new DSLNotFoundException(e);
		    }
		}
	    }
	} else {

	    // Java EDSL Annotation, can be used to determine DSLs in Java file
	    // context
	    AnnotationExtractor<EDSL> extractor = new AnnotationExtractor<EDSL>(
		    EDSL.class);
	    extractor.setInput(input.toString());

	    EDSL annotation = extractor.find();
	    edslAnnotations.add(extractor.getBounds());

	    if (annotation == null) {
		return determinedDSLs;
	    }

	    for (String dslName : annotation.value()) {
		DSLDefinition activeDSL = languageProvider
			.getActiveDSLForExtension(dslName);
		if (activeDSL != null) {
		    try {
			addDSLToContext(activeDSL, determinedDSLs);
		    } catch (NoLegalPropertyFoundException e) {
			throw new DSLNotFoundException(e);
		    }
		}
	    }
	}

	// Removes the EDSL annotation from the source file
	for (int[] b : edslAnnotations) {
	    input.delete(b[0], b[1]);
	}

	return determinedDSLs;
    }

    private static void addDSLToContext(@Nonnull DSLDefinition clazz,
	    Set<DSLDefinition> context) throws NoLegalPropertyFoundException {
	context.add(clazz);
	logger.trace("added dsl '{}' to context",
		clazz.getValue(DSLKey.EXTENSION));
    }

}
