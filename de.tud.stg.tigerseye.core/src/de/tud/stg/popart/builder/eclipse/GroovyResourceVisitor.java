package de.tud.stg.popart.builder.eclipse;

import org.eclipse.core.resources.IResource;

import de.tud.stg.popart.builder.core.aterm.PrettyGroovyCodePrinter;

public class GroovyResourceVisitor extends ResourceVisitor {

    private static final String PRE_FILE_EXTENSION = "groovy.dsl";
	private static final String POST_FILE_EXTENSION = "groovy";

	@Override
	public DSLResourceHandler newResourceHandler() {
		return new EDSLResourceHandler(POST_FILE_EXTENSION, PRE_FILE_EXTENSION, new PrettyGroovyCodePrinter());
	}

	@Override
	protected boolean isInteresstedIn(IResource resource) {
		return resource.getName().endsWith(PRE_FILE_EXTENSION);
	}
}