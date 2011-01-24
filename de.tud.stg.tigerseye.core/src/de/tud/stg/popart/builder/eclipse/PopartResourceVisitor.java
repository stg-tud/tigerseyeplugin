package de.tud.stg.popart.builder.eclipse;

import org.eclipse.core.resources.IResource;

import de.tud.stg.popart.builder.core.aterm.PrettyGroovyCodePrinter;
import de.tud.stg.popart.builder.transformers.Filetype;

public class PopartResourceVisitor extends ResourceVisitor {

	private static final String PRE_FILE_EXTENSION = Filetype.POPART.srcFileEnding;
	private static final String POST_FILE_EXTENSION = Filetype.POPART.outputFileEnding;

	@Override
	public DSLResourceHandler newResourceHandler() {
		return new DSLResourceHandler(POST_FILE_EXTENSION, PRE_FILE_EXTENSION, new PrettyGroovyCodePrinter());
	}

	@Override
	protected boolean isInteresstedIn(IResource resource) {
		return resource.getName().endsWith(PRE_FILE_EXTENSION)
		&& !resource.getName().endsWith(Filetype.JAVA.srcFileEnding)
		&& !resource.getName().endsWith(Filetype.GROOVY.srcFileEnding);
	}
}
