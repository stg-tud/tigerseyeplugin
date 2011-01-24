package de.tud.stg.popart.builder.transformers.textual;

import java.util.HashSet;
import java.util.Set;

import de.tud.stg.popart.builder.transformers.Filetype;
import de.tud.stg.popart.builder.transformers.Transformation;

/**
 * Utility class for {@link Transformation} implementing classes.
 *
 * @author Leo Roos
 *
 */
public final class TransformationUtils {

	public static Set<Filetype> getSetForFiletypes(Filetype... popart) {
		HashSet<Filetype> set = new HashSet<Filetype>();
		for (Filetype filetype : popart) {
			set.add(filetype);
		}
		return set;
	}

}
