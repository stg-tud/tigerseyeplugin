package de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler;

import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.CodePrinter;

public class EDSLResourceHandler extends DSLResourceHandler {


    public EDSLResourceHandler(FileType fileType, CodePrinter prettyPrinter) {
	super(fileType, prettyPrinter);
	}

//	@Override
//	protected IFile getFileOutputPath(IResource resource) {
//		IPath srcPath = resource.getProjectRelativePath();
//		IPath resourcePath = srcPath.removeFirstSegments(1);
//
//		IPath newFilePath = new Path(OUTPUT_DIRECTORY).append(resourcePath);
//
//		do {
//			newFilePath = newFilePath.removeFileExtension();
//		} while (!newFilePath.equals(newFilePath.removeFileExtension()));
//
//		IFile file = resource.getProject().getFile(newFilePath.addFileExtension(this.getFileExtension()));
//
//		return file;
//	}
}
