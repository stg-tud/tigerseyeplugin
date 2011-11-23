package de.tud.stg.tigerseye.eclipse.core.builder.transformers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.tud.stg.tigerseye.eclipse.core.api.Transformation;

/**
 * Utility class for {@link Transformation} implementing classes.
 * 
 * @author Leo Roos
 * 
 */
public final class TransformationUtils {

    public static final Set<FileType> FILE_TYPE_SET = getSetForFiletypes(FileType.values());

    public static Set<FileType> getSetForFiletypes(FileType... fts) {
	Set<FileType> set = new HashSet<FileType>();
	set.addAll(Arrays.asList(fts));
	return set;
    }

}
