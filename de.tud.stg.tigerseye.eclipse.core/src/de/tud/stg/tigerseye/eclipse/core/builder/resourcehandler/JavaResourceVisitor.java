package de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler;

import org.eclipse.core.resources.IResource;

import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.PrettyJavaCodePrinter;

public class JavaResourceVisitor extends DSLResourceHandler {

    private static final FileType fileType = FileType.JAVA;

    public JavaResourceVisitor() {
	super(fileType, new PrettyJavaCodePrinter());
    }

    @Override
    public boolean isInteresstedIn(IResource resource) {
	return resource.getName().endsWith(fileType.srcFileEnding);
    }
}
