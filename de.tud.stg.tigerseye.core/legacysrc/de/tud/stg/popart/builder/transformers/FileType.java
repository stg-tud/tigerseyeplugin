package de.tud.stg.popart.builder.transformers;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.CheckForNull;
/*
 * FIXME need to reorder responsibilities. java groovy popart are resource types, all four are transformation types.
 */
/**
 * Describes different source file types and the corresponding applied file
 * extensions as they are used in the source folder and the translated output
 * folder context.
 * 
 * @author Leo Roos
 */
public enum FileType implements TransformationType{
	JAVA("JAVA", "java.dsl", "java"), //
	GROOVY("GROOVY", "groovy.dsl", "groovy"), //
	POPART("TIGERSEYE", "dsl", "dsl.groovy"), //
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

	private FileType(String name, String srcFileEnding, String outPutFileEnding) {
		this.name = name;
		this.srcFileEnding = srcFileEnding;
		outputFileEnding = outPutFileEnding;
	}

	/**
	 * Tries to determine the {@link FileType} for given {@code resourceName}.
	 * Since the file endings are ambiguous the assumption is made, that the
	 * {@link FileType} with the longest internal string representation of a
	 * file ending that matches the file ending of {@code resourceName} is the
	 * correct, searched for, type.
	 * 
	 * @param resourceName
	 * @return the corresponding {@link FileType} to {@code resourceName} or
	 *         <code>null</code> if no matching type could be found.
	 */
	public static @CheckForNull
	FileType getTypeForSrcResource(String resourceName) {
		if (resourceName == null) {
			return null;
		}
		List<FileType> srcFileEndings = Arrays.asList(values());
		Collections.sort(srcFileEndings, longestSrcFileEndingFirstComparator());
		for (FileType filetype : srcFileEndings) {
			if (resourceName.endsWith(filetype.srcFileEnding)) {
				return filetype;
			}
		}
		return null;
	}

	/**
	 * @see #getTypeForSrcResource(String)
	 */
	public static @CheckForNull
	FileType getTypeForOutputResource(String resourceName) {
		if (resourceName == null) {
			return null;
		}
		List<FileType> outPutFileEndings = Arrays.asList(values());
		Collections.sort(outPutFileEndings,
				longestOutPutFileEndingFirstComparator());
		for (FileType filetype : outPutFileEndings) {
			if (resourceName.endsWith(filetype.outputFileEnding)) {
				return filetype;
			}
		}
		return null;
	}

	private static Comparator<FileType> longestSrcFileEndingFirstComparator() {
		return new Comparator<FileType>() {
			@Override
			public int compare(FileType o1, FileType o2) {
				return longestFirst(o1.srcFileEnding, o2.srcFileEnding);
			}

		};
	}

	private static Comparator<FileType> longestOutPutFileEndingFirstComparator() {
		return new Comparator<FileType>() {
			@Override
			public int compare(FileType o1, FileType o2) {
				return longestFirst(o1.outputFileEnding, o2.outputFileEnding);
			}
		};
	}

	private static int longestFirst(String o1Ending, String o2Ending) {
		return -1 * (o1Ending.length() - o2Ending.length());
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

    @Override
    public FileType getTransformationCategory() {
	return this;
    }
}