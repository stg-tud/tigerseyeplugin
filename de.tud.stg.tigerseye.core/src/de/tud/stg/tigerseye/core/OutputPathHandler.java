package de.tud.stg.tigerseye.core;

import javax.annotation.Nonnull;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import de.tud.stg.popart.builder.transformers.Filetype;

public class OutputPathHandler {
    
    private final Filetype filetype;
    private String localOutputDirectoryName;

    public OutputPathHandler(@Nonnull Filetype filetype) {
	this.filetype = filetype;
	this.localOutputDirectoryName = TigerseyeCore.getOutputDirectoryPath();
    }

    public OutputPathHandler(Filetype filetype, String outPutDir) {
	// Avoid calling TigerseyeCore if not necessary
	this.filetype = filetype;
	this.localOutputDirectoryName = outPutDir;
    }

    public IFile getProjectRelativeOutputFile(IResource resource) {
        IPath projectRelativePath = resource.getProjectRelativePath();
        IPath newProjRelPath = getProjectRelativePath(projectRelativePath);
        IFile outputFile = resource.getProject().getFile(newProjRelPath);
        return outputFile;
    }

    public IPath getProjectRelativePath(IPath projectRelativeSrcPath) {
	// FIXME the assumption that the source folder is always only exactly
	// the first element might be wrong.
        IPath srcRelativePath = projectRelativeSrcPath.removeFirstSegments(1);
        IPath outputPath = getSrcRelativeOutputPath(
        	srcRelativePath);
        IPath newProjRelPath = new Path(localOutputDirectoryName).append(outputPath);
        return newProjRelPath;
    }

    /**
     * Returns the output name for the resource {@code srcRelativePath}
     * according to the applied naming conversions.
     * 
     * @param srcRelativePath path relative to the source folder (i.e. the path analogous to the fully qualified name of a source file)
     *  root of the currently builded project 
     * @return the transformed name
     * 
     */
    public IPath getSrcRelativeOutputPath(IPath srcRelativePath) {
    
	IPath srcPath = srcRelativePath;
        String resourceFileEnding = filetype.srcFileEnding;
        String outputFileExtension = filetype.outputFileEnding;
    
        if (!srcPath.lastSegment().endsWith(resourceFileEnding))
            throw new IllegalArgumentException(
        	    "Expected file of with ending \"" + resourceFileEnding
        		    + "\" but was " + srcPath);
    
        String resourcefileName = srcPath.lastSegment();
        int endingInclusiveLastDot = resourceFileEnding.length() + 1;
        String srcUnitDslsName = resourcefileName.substring(0,
        	resourcefileName.length() - endingInclusiveLastDot);
        int firstDot = srcUnitDslsName.indexOf('.');
        String srcUnitName;
        if (firstDot > 0) {
            srcUnitName = srcUnitDslsName.substring(0, firstDot);
            String dslExtensions = srcUnitDslsName.substring(firstDot);
            String dotLessExtensions = dslExtensions.replaceAll("\\.", "_");
            srcUnitName += "$" + dotLessExtensions + "_";
        } else {
            srcUnitName = srcUnitDslsName;
        }
        String outputSrcUnitName;
        if (!outputFileExtension.contains(".")) {
            srcUnitName += ".";/*
			        * A resource of Filetype Popart(DSL) gets a
			        * dsl.groovy extension and hence must not have
			        * any further dots in his resulting file name in
			        * order to avoid complaints from the groovy
			        * launcher which subsequently handles the
			        * generated file.
			        */
        }
    
        outputSrcUnitName = srcUnitName.concat(outputFileExtension);
        IPath outputSrcFileName = srcPath.removeLastSegments(1)
        	.append(outputSrcUnitName);
        return outputSrcFileName;
    }

    /**
     * The used output directory to determine the target directory can be locally adjusted
     * @param localOutputDirectoryName
     */
    public void setLocalOutputDirectoryName(String localOutputDirectoryName) {
	this.localOutputDirectoryName = localOutputDirectoryName;
    }


}
