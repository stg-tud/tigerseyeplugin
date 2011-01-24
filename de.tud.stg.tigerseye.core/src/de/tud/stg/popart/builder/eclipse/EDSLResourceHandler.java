package de.tud.stg.popart.builder.eclipse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import de.tud.stg.popart.builder.core.aterm.CodePrinter;
import de.tud.stg.popart.builder.transformers.AnnotationExtractor;
import de.tud.stg.popart.builder.transformers.Context;
import de.tud.stg.popart.dslsupport.DSL;

public class EDSLResourceHandler extends DSLResourceHandler {


	public EDSLResourceHandler(String fileExtension, String fileNameEnding, CodePrinter prettyPrinter) {
		super(fileExtension, fileNameEnding, prettyPrinter);
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
