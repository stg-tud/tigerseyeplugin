package de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.CheckForNull;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.popart.builder.eclipse.EDSL;
import de.tud.stg.tigerseye.eclipse.core.api.DSLNotFoundException;
import de.tud.stg.tigerseye.eclipse.core.api.ILanguageProvider;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.AnnotationExtractor;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileTypeHelper;

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
     * Determines involved DSL names. Those are only dependent on the file name
     * and possible annotations
     * 
     * @param resource
     * @param input
     * @return
     * @throws DSLNotFoundException
     */
    public static List<String> determineInvolvedDSLNames(IFile resource,
	    StringBuffer input, ILanguageProvider languageProvider,
	    FileType interestedInFiletype) throws DSLNotFoundException {
	List<String> determinedDSLNames = new LinkedList<String>();
	int fileExtensionIndex = resource.getName().lastIndexOf(
		interestedInFiletype.srcFileEnding);
	if (fileExtensionIndex < 1) {
	    throw new DSLNotFoundException(
		    "No dsl extension could be determined for " + resource);
	}

	String name = resource.getName();
	FileType typeForSrcResource = FileTypeHelper.getTypeForSrcResource(name);
	if (typeForSrcResource == null) {
	    logger.warn("Resource of unknown type {}", resource);
	    return Collections.emptyList();
	}

	// XXX(Leo_Roos;Nov 1, 2011) Is the exclusive or approach necessary? It
	// would be possible to simply define the involved languages from the
	// file making the decision which dsls to use more dynamic
	if (FileType.TIGERSEYE.equals(typeForSrcResource)) {
	    String[] str = name.substring(0, fileExtensionIndex - 1)
		.split("\\.");
	    Assert.isTrue(str.length > 1);
	    // if (str.length > 1) {
	    for (int i = 1; i < str.length; i++) {
		String dslName = str[i];
		determinedDSLNames.add(dslName);
	    }
	    // }
	} else {
	    List<int[]> edslAnnotations = new LinkedList<int[]>();
	    // Java EDSL Annotation, can be used to determine DSLs in Java file
	    // context or probably in Groovy File Context as well
	    AnnotationExtractor<EDSL> extractor = new AnnotationExtractor<EDSL>(EDSL.class);
	    extractor.setInput(input.toString());

	    // XXX(Leo_Roos;Nov 1, 2011) could do find and add in loop to
	    // support multiple
	    // EDSL statements
	    EDSL annotation = extractor.find();
	    edslAnnotations.add(extractor.getBounds());

	    if (annotation == null) {
		return determinedDSLNames;
	    }

	    String[] dslNames = annotation.value();
	    Collections.addAll(determinedDSLNames, dslNames);

	    // Removes the EDSL annotation from the source file
	    for (int[] b : edslAnnotations) {
		input.delete(b[0], b[1]);
	    }
	}
	return determinedDSLNames;
    }

}
