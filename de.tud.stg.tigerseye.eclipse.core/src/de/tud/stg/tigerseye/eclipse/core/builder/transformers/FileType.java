package de.tud.stg.tigerseye.eclipse.core.builder.transformers;

import de.tud.stg.tigerseye.eclipse.core.api.TransformationType;

/*
 * FIXME need to reorder responsibilities. java groovy tigerseye are resource types, all four are transformation types.
 */
/**
 * Describes different source file types and the corresponding applied file
 * extensions as they are used in the source folder and the translated output
 * folder context.
 * 
 * @author Leo Roos
 */
public enum FileType implements TransformationType {
    JAVA("JAVA", "java.dsl", "java"), //
    GROOVY("GROOVY", "groovy.dsl", "groovy"), //
    TIGERSEYE("TIGERSEYE", "dsl", "dsl.groovy"), //
    DSL("DSL", "notset", "notset"); /*
				     * XXX Actually not a FileType. Renaming
				     * this enumeration to DomainType?
				     */

    /**
     * File extension for this FileType in the Tigerseye source folder context.
     */
    public final String srcFileEnding;
    /**
     * File extension for this FileType in the Tigerseye output folder context,
     * for the transformed files.
     */
    public final String outputFileEnding;
    /**
     * This FileTypes descriptive, unique name.
     */
    public final String name;
    public static final FileType[] RESOURCE_FILE_TYPES = { JAVA, GROOVY, TIGERSEYE };
    public static final FileType[] DSL_FILETYPES = { DSL };

    private FileType(String name, String srcFileEnding, String outPutFileEnding) {
	this.name = name;
	this.srcFileEnding = srcFileEnding;
	outputFileEnding = outPutFileEnding;
    }

    /**
     * @return returns a user friendly description of this FileType, e.g. for
     *         JAVA:
     * 
     *         <pre>
     * FileType:JAVA[java.dsl,java]
     * </pre>
     * 
     *         where JAVA is the FileType {@link #name}, {@code java.dsl} the
     *         {@link #srcFileEnding} and {@code java} the
     *         {@link #outputFileEnding}.
     */
    @Override
    public String toString() {
	return getClass().getSimpleName() + ":" + name + "[" + srcFileEnding
		+ "," + outputFileEnding + "]";
    }

    @Override
    public String getIdentifer() {
	return getClass().getName() + this.name;
    }

}