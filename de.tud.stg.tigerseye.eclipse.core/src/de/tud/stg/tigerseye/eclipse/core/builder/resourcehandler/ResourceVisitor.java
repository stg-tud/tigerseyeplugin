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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;
import de.tud.stg.tigerseye.eclipse.core.api.DSLDefinition;
import de.tud.stg.tigerseye.eclipse.core.api.DSLNotFoundException;
import de.tud.stg.tigerseye.eclipse.core.api.ILanguageProvider;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileTypeHelper;

public abstract class ResourceVisitor implements IResourceDeltaVisitor {


    public ResourceVisitor() {
	languageProvider = TigerseyeCore.getLanguageProvider();
    }

    private static final Logger logger = LoggerFactory
	    .getLogger(ResourceVisitor.class);
    private final ILanguageProvider languageProvider;

    @Override
    public boolean visit(IResourceDelta delta) {

	// FIXME add default functionality when resource is added or removed
	IResource aResource = delta.getResource();

	IFile file;
	if (!(aResource instanceof IFile)) {
	    logger.debug("Skipping resource {}, since not of type IFile",
		    aResource);
	    return true;
	} else {
	    file = (IFile) aResource;
	}

	boolean canHandleDelta = allDSLsForDeltaAreActive(file);
	if (!canHandleDelta)
	    return true;

	try {
	    if (this.isInteresstedIn(file)) {
		getResourceHandler().handleResource(file);
	    }
	} catch (Exception e) {
	    logger.error("failed vissiting delta {}", delta, e);
	}
	return true;
    }

    // * FIXME(Leo_Roos;Aug 27, 2011) paritally copied from
    // DSLResourceHandler#handleResource
    private boolean allDSLsForDeltaAreActive(IFile resource) {

	IFile srcFile = resource;
	FileType filetype = FileTypeHelper.getTypeForSrcResource(srcFile
		.getName());
	if (filetype == null) {
	    logger.trace("file {} of no interest for transformation",
		    srcFile.getName());
	    return false;
	}
	StringBuffer resourceContent = ResourceHandlingHelper
		.readResource(srcFile);
	if (resourceContent == null) {
	    logger.error("Skipping unhandled resource {}", srcFile);
	    return false;
	}
	Set<DSLDefinition> dslDefinitions = Collections.emptySet();
	try {
	    dslDefinitions = determineInvolvedDSLs(srcFile, resourceContent,
		    languageProvider);
	} catch (DSLNotFoundException e) {
	    logger.debug("Resource {} could not be handled. {}", new Object[] {
		    srcFile, e.noDSLMsg() }, e);
	}
	for (DSLDefinition dslDefinition : dslDefinitions) {
	    if (!dslDefinition.isActive()) {
		return false;
	    }
	}
	return true;
    }

    protected abstract Set<DSLDefinition> determineInvolvedDSLs(IFile srcFile,
	    StringBuffer resourceContent, ILanguageProvider languageProvider2)
	    throws DSLNotFoundException;

    public abstract boolean isInteresstedIn(IResource resource);

    public abstract ResourceHandler getResourceHandler();

}