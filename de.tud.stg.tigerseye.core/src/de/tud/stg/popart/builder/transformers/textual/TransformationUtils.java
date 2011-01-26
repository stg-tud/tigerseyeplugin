package de.tud.stg.popart.builder.transformers.textual;

import java.util.HashSet;
import java.util.Set;

import de.tud.stg.popart.builder.transformers.FileType;
import de.tud.stg.popart.builder.transformers.Transformation;

/**
 * Utility class for {@link Transformation} implementing classes.
 *
 * @author Leo Roos
 *
 */
public final class TransformationUtils {

	public static Set<FileType> getSetForFiletypes(FileType... popart) {
		HashSet<FileType> set = new HashSet<FileType>();
		for (FileType filetype : popart) {
			set.add(filetype);
		}
		return set;
	}

}
