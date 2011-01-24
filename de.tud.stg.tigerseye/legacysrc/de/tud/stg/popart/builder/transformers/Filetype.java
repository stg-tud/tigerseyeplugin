package de.tud.stg.popart.builder.transformers;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.CheckForNull;

/**
 * Describes different source file types and the corresponding applied file
 * extensions as they are used in the source and the translated context.
 * 
 * @author Leo Roos
 */
public enum Filetype {
	JAVA("JAVA", "java.dsl", "java"), // FIXME change implementation to handle
										// pure java files with Java launcher
	GROOVY("GROOVY", "groovy.dsl", "groovy"), //
	POPART("POPART", "dsl", "dsl.groovy"), //
	DSL("DSL", "notset", "notset"); /*
									 * XXX When is that used instead of
									 * POPART?
									 */

	public final String srcFileEnding;
	public final String outputFileEnding;
	public final String name;

	private Filetype(String name, String srcFileEnding,
			String outPutFileEnding) {
		this.name = name;
		this.srcFileEnding = srcFileEnding;
		outputFileEnding = outPutFileEnding;
	}

	/**
	 * Tries to determine the {@link Filetype} for given
	 * <code>resourceName</code>. Since the file endings are ambiguous the
	 * assumption is made, that the {@link Filetype} with the longest
	 * internal string representation of a file ending ending that matches
	 * the file ending of <code>resourceName</code> is the correct, searched for, type.
	 *
	 * @param resourceName
	 * @return the corresponding {@link Filetype} to
	 *         <code>resourceName</code> or <code>null</code> if no matching
	 *         type could be found.
	 */
	public static @CheckForNull
	Filetype getTypeForSrcResource(String resourceName) {
		if (resourceName == null) {
			return null;
		}
		List<Filetype> srcFileEndings = Arrays.asList(values());
		Collections.sort(srcFileEndings,
				longestSrcFileEndingFirstComparator());
		for (Filetype filetype : srcFileEndings) {
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
	Filetype getTypeForOutputResource(String resourceName) {
		if (resourceName == null) {
			return null;
		}
		List<Filetype> outPutFileEndings = Arrays.asList(values());
		Collections.sort(outPutFileEndings,
				longestOutPutFileEndingFirstComparator());
		for (Filetype filetype : outPutFileEndings) {
			if (resourceName.endsWith(filetype.outputFileEnding)) {
				return filetype;
			}
		}
		return null;
	}

	private static Comparator<Filetype> longestSrcFileEndingFirstComparator() {
		return new Comparator<Filetype>() {
			@Override
			public int compare(Filetype o1, Filetype o2) {
				return longestFirst(o1.srcFileEnding, o2.srcFileEnding);
			}

		};
	}

	private static Comparator<Filetype> longestOutPutFileEndingFirstComparator() {
		return new Comparator<Filetype>() {
			@Override
			public int compare(Filetype o1, Filetype o2) {
				return longestFirst(o1.outputFileEnding, o2.outputFileEnding);				
			}
		};
	}
	

	private static int longestFirst(String o1Ending, String o2Ending) {
		return -1
				* (o1Ending.length() - o2Ending
						.length());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+":" + name + "[" + srcFileEnding + ","
				+ outputFileEnding + "]";
	}
}