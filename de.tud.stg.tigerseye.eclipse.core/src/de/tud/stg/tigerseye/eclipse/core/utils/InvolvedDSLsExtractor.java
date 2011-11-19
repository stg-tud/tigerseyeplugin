package de.tud.stg.tigerseye.eclipse.core.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.dslsupport.annotations.EDSL;
import de.tud.stg.tigerseye.eclipse.core.api.DSLNotFoundException;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.AnnotationExtractor;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileTypeHelper;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.IllegalAnnotationFormat;

/**
 * Determines involved DSLs of a resource. For that the file name and the
 * content is important
 * 
 * @author Leo_Roos
 * 
 */
public class InvolvedDSLsExtractor {

    private static final Logger logger = LoggerFactory.getLogger(InvolvedDSLsExtractor.class);

    private final DSLExtensionsExtractor filenameextractor = new DSLExtensionsExtractor();

    // FIXME(Leo_Roos;Nov 10, 2011) QOD
    public Set<String> determineInvolvedDSLNames(IFile resource, String input) {
	return determineInvolvedDSLNames(resource, new StringBuffer(input));
    }

    /**
     * Determines involved DSL names. Those are only dependent on the file name
     * and the {@link EDSL} annotation.
     * <p>
     * <b>Side effect</b> is that a possible EDSL annotation is removed from the
     * input buffer.
     * 
     * @param resource
     * @param input
     * @return
     * @throws DSLNotFoundException
     */
    public Set<String> determineInvolvedDSLNames(IFile resource, StringBuffer input) {
    
        String resourceName = resource.getName();
    
	FileType resourceType = FileTypeHelper.getTypeForSrcResource(resourceName);
    
        Set<String> determinedDSLNames = new HashSet<String>();
        if (FileType.TIGERSEYE.equals(resourceType)) {
	    String[] extensionsByResourceName = filenameextractor
		    .getExtensionForSrcResource(resourceName, resourceType);
            Collections.addAll(determinedDSLNames, extensionsByResourceName);
        }
    
	List<String> determinedDSLNamesFromAnnotation;
	try {
	    determinedDSLNamesFromAnnotation = extractFromEDSLAnnotation(input);
	} catch (IllegalAnnotationFormat e) {
	    logger.warn("resource {} has illdefined EDSL definition ", resource);
	    determinedDSLNamesFromAnnotation = Collections.emptyList();
	}

        determinedDSLNames.addAll(determinedDSLNamesFromAnnotation);
    
        return determinedDSLNames;
    }

    private @Nonnull
    List<String> extractFromEDSLAnnotation(StringBuffer input) throws IllegalAnnotationFormat {
        List<int[]> edslAnnotations = new LinkedList<int[]>();
        // Java EDSL Annotation, can be used to determine DSLs in Java file
        // context or probably in Groovy File Context as well
        AnnotationExtractor<EDSL> extractor = new AnnotationExtractor<EDSL>(EDSL.class);
        extractor.setInput(input.toString());
    
        // XXX(Leo_Roos;Nov 1, 2011) could do find and add in loop to
        // support multiple
        // EDSL statements
        EDSL annotation = extractor.find();
	if (annotation == null) {
	    return Collections.emptyList();
	} else {
	    // XXX(Leo_Roos;Nov 11, 2011) File modification logic belongs
	    // somewhere else
	    edslAnnotations.add(extractor.getBounds());

	    String[] dslNames = annotation.value();
	    if (dslNames == null)
		throw new IllegalAnnotationFormat(
			"Failed to parse dsl names. Probably illegal format in EDSL description.");

	    List<String> determinedDSLNames = Arrays.asList(dslNames);

	    // Removes the EDSL annotation from the source file
	    for (int[] b : edslAnnotations) {
		input.delete(b[0], b[1]);
	    }
	    return determinedDSLNames;
	}
    }


    /**
     * Extracts involved DSLs of {@code file}
     * 
     * @param file
     *            to extract DSL names from
     * @return all involved DSL names
     */
    public Set<String> getDSLNamesForSrcResource(IFile file) {
	try {
	    String content = IOUtils.toString(new InputStreamReader(file.getContents()));
	    Set<String> determineInvolvedDSLNames = determineInvolvedDSLNames(file, content);
	    return determineInvolvedDSLNames;
	} catch (IOException e) {
	    logger.warn("failed to determine dsls for editor", e);
	} catch (CoreException e) {
	    logger.warn("failed to determine dsls for editor", e);
	}
	return Collections.emptySet();
    }

}
