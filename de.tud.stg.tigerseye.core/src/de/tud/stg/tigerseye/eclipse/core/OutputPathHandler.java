package de.tud.stg.tigerseye.eclipse.core;

import javax.annotation.CheckForNull;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import de.tud.stg.popart.builder.transformers.FileType;

/**
 * Central unit to perform the filename translation between Tigerseye source
 * files and its transformed output files.
 * 
 * @author Leo Roos
 * 
 * @see FileType
 */
public class OutputPathHandler {

    private static final String dotReplacement = "_";
    private static final String beginningDSLPrefixString = "$";
    private String localOutputDirectoryName;

    public OutputPathHandler() {
	this.localOutputDirectoryName = TigerseyeRuntime.getOutputDirectoryPath();
    }

    public OutputPathHandler(String outPutDir) {
	// Avoiding calling TigerseyeCore if not necessary
	this.localOutputDirectoryName = outPutDir;
    }

    public @CheckForNull
    IFile getOutputFile(IFile srcFile) {
	// FIXME the assumption that the source folder is always only exactly
	// the first element might be wrong.
	IPath projectRelativePath = srcFile.getProjectRelativePath();
	IPath delegatedPath = getSrcRelativeOutputPath(projectRelativePath
			.removeFirstSegments(1));
	IPath newProjRelPath = new Path(localOutputDirectoryName)
		.append(delegatedPath);
	IFile delegatedFile = srcFile.getProject().getFile(newProjRelPath);
	return delegatedFile;
    }

    /**
     * Returns the output name for the resource {@code srcRelativePath}
     * according to the applied naming conversions.
     * 
     * @param srcRelativePath
     *            path relative to the source folder (i.e. the path analogous to
     *            the fully qualified name of a source file) root of the
     *            currently builded project
     * @return the transformed {@code srcRelativePath}
     * @see getOutputNameForSourceName(String)
     */
    public
    IPath getSrcRelativeOutputPath(IPath srcRelativePath) {

	IPath srcPath = srcRelativePath;
	String resourcefileName = srcPath.lastSegment();
	String outputSrcUnitName = getOutputNameForSourceName(resourcefileName);
	IPath outputSrcFileName = srcPath.removeLastSegments(1).append(
		outputSrcUnitName);
	return outputSrcFileName;
    }

    /**
     * Transforms given fileName (without any path) to an output folder style
     * name.
     * 
     * @param srcResourceFileName
     * @return transformed output folder name for {@code srcResourceFileName} or
     *         <code>null</code> if none can be determined.
     */
    public @CheckForNull
    String getOutputNameForSourceName(String srcResourceFileName) {
	FileType filetype = FileType.getTypeForSrcResource(srcResourceFileName);
	if (filetype == null)
	    return null;

	String resourceFileEnding = filetype.srcFileEnding;
	String outputFileExtension = filetype.outputFileEnding;
	int endingInclusiveLastDot = resourceFileEnding.length() + 1;
	String srcUnitDslsName = srcResourceFileName.substring(0,
		srcResourceFileName.length() - endingInclusiveLastDot);
	int firstDot = srcUnitDslsName.indexOf('.');
	String srcUnitName;
	if (firstDot > 0) {
	    srcUnitName = srcUnitDslsName.substring(0, firstDot);
	    String dslExtensions = srcUnitDslsName.substring(firstDot);
	    String dotLessExtensions = dslExtensions.replaceAll("\\.",
		    dotReplacement);
	    srcUnitName += beginningDSLPrefixString + dotLessExtensions
		    + dotReplacement;
	} else {
	    srcUnitName = srcUnitDslsName;
	}
	String outputSrcUnitName;
	if (!outputFileExtension.contains(".")) {
	    srcUnitName += ".";/*
			        * A resource of FileType Popart gets a
			        * dsl.groovy extension and hence must not have
			        * any further dots in his resulting file name in
			        * order to avoid complaints from the groovy
			        * launcher which subsequently handles the
			        * generated file. XXX after having adjusted the
			        * launch process, is this still a problem?
			        */
	}

	outputSrcUnitName = srcUnitName.concat(outputFileExtension);
	return outputSrcUnitName;
    }

    /**
     * The used output directory to determine the target directory can be
     * locally adjusted
     * 
     * @param localOutputDirectoryName
     */
    public void setLocalOutputDirectoryName(String localOutputDirectoryName) {
	this.localOutputDirectoryName = localOutputDirectoryName;
    }

    /**
     * Transforms the output style name of a DSL resource to its expected source
     * name.
     * 
     * @param outputName
     *            the output folder name
     * @return the source name for {@code outputName} or <code>null</code> if
     *         none can be determined.
     * @throws IllegalArgumentException
     *             <ul>
     *             <li>If {@code outputName} has illegal format.</li>
     *             </ul>
     */
    public @CheckForNull
    String getSourceNameForOutputName(String outputName) {
	FileType actualType = FileType.getTypeForOutputResource(outputName);
	if (actualType == null)
	    return null;
	if (outputName.substring(outputName.indexOf(".") + 1).contains("."))
	    throw new IllegalArgumentException(
		    "Expected at most one dot in file name but was: "
			    + outputName);
	return transformOutputToSrcName(outputName, actualType);
    }

    private String transformOutputToSrcName(String outputName, FileType filetype) {
	int extensionIndex = outputName.indexOf(".");
	String withoutExtension = outputName.substring(0, extensionIndex);
	if (FileType.JAVA.equals(filetype) || FileType.GROOVY.equals(filetype)) {
	    return withoutExtension + "." + filetype.srcFileEnding;
	}
	int beginOfDsls = withoutExtension.indexOf(beginningDSLPrefixString);
	String pureTypeName = withoutExtension.substring(0, beginOfDsls);
	String[] split = withoutExtension.substring(beginOfDsls + 1).split(
		dotReplacement);
	StringBuilder transformedName = new StringBuilder(pureTypeName);
	for (String extension : split) {
	    if (!extension.isEmpty()) {
		transformedName.append(".").append(extension);
	    }
	}
	return transformedName.toString();
    }

}
