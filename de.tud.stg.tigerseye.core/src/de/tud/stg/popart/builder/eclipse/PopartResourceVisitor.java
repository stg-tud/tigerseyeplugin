package de.tud.stg.popart.builder.eclipse;

import org.eclipse.core.resources.IResource;

import de.tud.stg.popart.builder.core.aterm.PrettyGroovyCodePrinter;
import de.tud.stg.popart.builder.transformers.FileType;

public class PopartResourceVisitor extends ResourceVisitor {

	private static final String PRE_FILE_EXTENSION = FileType.POPART.srcFileEnding;
	private static final String POST_FILE_EXTENSION = FileType.POPART.outputFileEnding;

	@Override
	public DSLResourceHandler newResourceHandler() {
		return new DSLResourceHandler(POST_FILE_EXTENSION, PRE_FILE_EXTENSION, new PrettyGroovyCodePrinter());
	}

	@Override
	protected boolean isInteresstedIn(IResource resource) {
		return resource.getName().endsWith(PRE_FILE_EXTENSION)
		&& !resource.getName().endsWith(FileType.JAVA.srcFileEnding)
		&& !resource.getName().endsWith(FileType.GROOVY.srcFileEnding);
	}
}
