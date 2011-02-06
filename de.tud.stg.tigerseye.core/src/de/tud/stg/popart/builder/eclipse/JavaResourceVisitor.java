package de.tud.stg.popart.builder.eclipse;

import org.eclipse.core.resources.IResource;

import de.tud.stg.popart.builder.core.aterm.PrettyJavaCodePrinter;
import de.tud.stg.popart.builder.transformers.FileType;

public class JavaResourceVisitor extends ResourceVisitor {

    private static final FileType fileType = FileType.JAVA;

	@Override
	public DSLResourceHandler newResourceHandler() {
	return new EDSLResourceHandler(fileType, new PrettyJavaCodePrinter());
	}

	@Override
	protected boolean isInteresstedIn(IResource resource) {
	return resource.getName().endsWith(fileType.srcFileEnding);
	}
}
