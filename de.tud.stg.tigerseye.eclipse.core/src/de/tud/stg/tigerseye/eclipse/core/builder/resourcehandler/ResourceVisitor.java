/**
 *
 */
package de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.api.DSLNotFoundException;
import de.tud.stg.tigerseye.eclipse.core.api.ILanguageProvider;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileTypeHelper;
import de.tud.stg.tigerseye.eclipse.core.utils.OutputPathHandler;

public abstract class ResourceVisitor implements IResourceDeltaVisitor {

    public ResourceVisitor() {
	languageProvider = TigerseyeCore.getLanguageProvider();
    }

    private static final Logger logger = LoggerFactory.getLogger(ResourceVisitor.class);
    private final ILanguageProvider languageProvider;

    @Override
    public boolean visit(IResourceDelta delta) {

	IResource aResource = delta.getResource();

	IFile file;
	if (aResource instanceof IFile) {
	    file = (IFile) aResource;
	} else {
	    logger.trace("Skipping resource {}, since not of type IFile", aResource);
	    // Returning true since this will usually mean the resource is a
	    // folder that might contain files that are of interest to me, i.e.
	    // .dsl Files
	    return true;
	}

	int kind = delta.getKind();
	switch (kind) {
	case IResourceDelta.CHANGED:
	    logger.trace("File '{}' changed.", file);
	    return handleChanged(file);
	case IResourceDelta.ADDED:
	    logger.trace("Resource has been added: '{}'", file);
	    // XXX(leo;30.10.2011) maybe invoke builder?
	    break;
	case IResourceDelta.REMOVED:
	    logger.trace(
		    "Resource has been removed: '{}'. Will test whether its Tigerseye dsl file and will delete derived file if it exists.",
		    file);
	    handleRemoved(file);
	    break;
	default:
	    logger.warn("Cannot handle IResourceDelta kind '{}'. Will ignore it.", kind);
	}

	return false;
    }

    private void handleRemoved(IFile file) {
	String fileName = file.getName();
	FileType typeForSrcResource = FileTypeHelper.getTypeForSrcResource(fileName);
	if (FileType.TIGERSEYE.equals(typeForSrcResource)) {
	    OutputPathHandler outputPathHandler = new OutputPathHandler();
	    IFile outputFile = outputPathHandler.getOutputFile(file);
	    if (outputFile.exists()) {
		try {
		    logger.debug("derived File for {} exists. it is {}. Trying to delete it.", file, outputFile);
		    outputFile.delete(false, null);
		} catch (CoreException e) {
		    logger.error("Could not delete file {}", file, e);
		}
	    }
	}
    }

    private boolean handleChanged(IFile file) {

	boolean canHandleDelta = allDSLsForDeltaAreActive(file);
	if (!canHandleDelta)
	    return true;

	try {
	    if (this.isInteresstedIn(file)) {
		getResourceHandler().handleResource(file);
	    }
	} catch (Exception e) {
	    logger.error("failed vissiting file {}", file, e);
	}
	return true;
    }

    // * FIXME(Leo_Roos;Aug 27, 2011) paritally copied from
    // DSLResourceHandler#handleResource
    private boolean allDSLsForDeltaAreActive(IFile resource) {

	IFile srcFile = resource;
	FileType filetype = FileTypeHelper.getTypeForSrcResource(srcFile.getName());
	if (filetype == null) {
	    logger.trace("file {} of no interest for transformation", srcFile.getName());
	    return false;
	}
	StringBuffer resourceContent = ResourceHandlingHelper.readResource(srcFile);
	if (resourceContent == null) {
	    logger.error("Skipping unhandled resource {}", srcFile);
	    return false;
	}
	Set<DSLDefinition> dslDefinitions = Collections.emptySet();
	try {
	    dslDefinitions = determineInvolvedDSLs(srcFile, resourceContent, languageProvider);
	} catch (DSLNotFoundException e) {
	    logger.debug("Resource {} could not be handled. {}", new Object[] { srcFile, e.noDSLMsg() }, e);
	}
	for (DSLDefinition dslDefinition : dslDefinitions) {
	    if (!dslDefinition.isActive()) {
		return false;
	    }
	}
	return true;
    }

    protected abstract Set<DSLDefinition> determineInvolvedDSLs(IFile srcFile, StringBuffer resourceContent,
	    ILanguageProvider languageProvider2) throws DSLNotFoundException;

    public abstract boolean isInteresstedIn(IResource resource);

    public abstract ResourceHandler getResourceHandler();

}