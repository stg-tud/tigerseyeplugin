package de.tud.stg.popart.builder.eclipse;

import org.eclipse.core.resources.IResource;

import de.tud.stg.popart.builder.core.aterm.PrettyJavaCodePrinter;
import de.tud.stg.popart.builder.transformers.FileType;

public class JavaResourceVisitor extends ResourceVisitor {

    public static final String PRE_FILE_EXTENSION = FileType.JAVA.srcFileEnding;// "java.dsl";
    private static final String POST_FILE_EXTENSION = FileType.JAVA.outputFileEnding;// "java";

	@Override
	public DSLResourceHandler newResourceHandler() {
		return new EDSLResourceHandler(POST_FILE_EXTENSION, PRE_FILE_EXTENSION, new PrettyJavaCodePrinter());
	}

	@Override
	protected boolean isInteresstedIn(IResource resource) {
		return resource.getName().endsWith(PRE_FILE_EXTENSION);
	}
}
