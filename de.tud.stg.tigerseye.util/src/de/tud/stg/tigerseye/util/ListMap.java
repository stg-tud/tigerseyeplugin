package de.tud.stg.tigerseye.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Supports a <i>map</i> style list transformation as known from functional
 * languages. The transformation functionality is defined in the passed
 * {@link Transformer}.
 * 
 * @author Leo Roos
 * 
 */
public class ListMap {

	public static <A, B> List<B> map(Collection<A> arrayList,
			Transformer<A, B> t) {
		ArrayList<B> result = new ArrayList<B>(arrayList.size());
		for (A a : arrayList) {
			result.add(t.transform(a));
		}
		return result;
	}
	
	
	public static <A, B> List<B> map(Transformer<A, B> t, A ...arrayList ) {
		ArrayList<B> result = new ArrayList<B>(arrayList.length);
		for (A a : arrayList) {
			result.add(t.transform(a));
		}
		return result;
	}

}
