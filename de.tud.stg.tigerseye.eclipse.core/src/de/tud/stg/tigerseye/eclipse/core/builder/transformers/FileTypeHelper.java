package de.tud.stg.tigerseye.eclipse.core.builder.transformers;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.CheckForNull;

public class FileTypeHelper {

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
        List<FileType> srcFileEndings = Arrays.asList(FileType.values());
	Collections.sort(srcFileEndings, longestSrcFileEndingFirstComparator());
        for (FileType filetype : srcFileEndings) {
	    // XXX(Leo_Roos;Nov 11, 2011) should the "." be left here hard coded
	    // or should it be made configurable
	    if (resourceName.endsWith("." + filetype.srcFileEnding)) {
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
     * @see #getTypeForSrcResource(String)
     */
    public static @CheckForNull
    FileType getTypeForOutputResource(String resourceName) {
        if (resourceName == null) {
            return null;
        }
        List<FileType> outPutFileEndings = Arrays.asList(FileType.values());
        Collections.sort(outPutFileEndings,
		longestOutPutFileEndingFirstComparator());
        for (FileType filetype : outPutFileEndings) {
            if (resourceName.endsWith(filetype.outputFileEnding)) {
        	return filetype;
            }
        }
        return null;
    }

}
