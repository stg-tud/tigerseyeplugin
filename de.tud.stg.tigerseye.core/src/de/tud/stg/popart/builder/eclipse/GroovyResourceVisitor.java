package de.tud.stg.popart.builder.eclipse;

import org.eclipse.core.resources.IResource;

import de.tud.stg.popart.builder.core.aterm.PrettyGroovyCodePrinter;
import de.tud.stg.popart.builder.transformers.FileType;

public class GroovyResourceVisitor extends ResourceVisitor {

    private static final FileType fileType = FileType.GROOVY;

	@Override
	public DSLResourceHandler newResourceHandler() {
	return new EDSLResourceHandler(fileType, new PrettyGroovyCodePrinter());
	}

	@Override
	protected boolean isInteresstedIn(IResource resource) {
	return resource.getName().endsWith(fileType.srcFileEnding);
	}
}