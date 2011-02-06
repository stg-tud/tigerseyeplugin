package de.tud.stg.popart.builder.eclipse;

import de.tud.stg.popart.builder.core.aterm.CodePrinter;
import de.tud.stg.popart.builder.transformers.FileType;

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
