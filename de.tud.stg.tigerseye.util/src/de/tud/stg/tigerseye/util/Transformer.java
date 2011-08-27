package de.tud.stg.tigerseye.util;

/**
 * Transformer class used by {@link ListMap}. This interface is implemented by
 * users of {@link ListMap} to provide the desired transformation functionality
 * for each list element.
 * 
 * @author Leo Roos
 * 
 * @param <A>
 *            transform from this type
 * @param <B>
 *            transform to this type
 */
public interface Transformer<A, B> {

    B transform(A input);
}
