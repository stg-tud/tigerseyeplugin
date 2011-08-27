package de.tud.stg.tigerseye.eclipse.core.builder.resourcehandler;

import org.eclipse.core.resources.IResource;

import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.aterm.PrettyJavaCodePrinter;

public class GroovyResourceVisitor extends DSLResourceHandler {

    public static final FileType fileType = FileType.GROOVY;

    public GroovyResourceVisitor() {
	super(fileType, new PrettyJavaCodePrinter());
    }

    @Override
    public boolean isInteresstedIn(IResource resource) {
	return resource.getName().endsWith(fileType.srcFileEnding);
    }
}