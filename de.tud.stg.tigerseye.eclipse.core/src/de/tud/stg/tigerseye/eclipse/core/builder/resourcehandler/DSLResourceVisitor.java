package de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler;

import org.eclipse.core.resources.IResource;

import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.PrettyGroovyCodePrinter;

public class DSLResourceVisitor extends DSLResourceHandler {

    private static final FileType fileType = FileType.TIGERSEYE;

    public DSLResourceVisitor() {
	super(fileType, new PrettyGroovyCodePrinter());
    }

    @Override
    public boolean isInteresstedIn(IResource resource) {
	return resource.getName().endsWith(fileType.srcFileEnding)
		&& !resource.getName().endsWith(FileType.JAVA.srcFileEnding)
		&& !resource.getName().endsWith(FileType.GROOVY.srcFileEnding);
    }
}
