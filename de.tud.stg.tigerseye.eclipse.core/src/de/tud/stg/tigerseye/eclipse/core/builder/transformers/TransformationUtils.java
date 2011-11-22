package de.tud.stg.tigerseye.eclipse.core.builder.transformers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.tud.stg.tigerseye.eclipse.core.api.Transformation;
import de.tud.stg.tigerseye.eclipse.core.api.TransformationType;

/**
 * Utility class for {@link Transformation} implementing classes.
 * 
 * @author Leo Roos
 * 
 */
public final class TransformationUtils {

    public static Set<TransformationType> getSetForFiletypes(FileType... popart) {
	Set<TransformationType> set = new HashSet<TransformationType>();
	set.addAll(Arrays.asList(popart));
	return set;
    }

}
