package de.tud.stg.tigerseye.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Supports a <i>map</i> style list transformation as known from functional
 * languages. The transformation functionality is defined in the passed
 * {@link LMTransformer}.
 * 
 * @author Leo Roos
 * 
 */
public class ListMap {

    public static <A, B> List<B> map(List<A> arrayList, LMTransformer<A, B> t) {
	ArrayList<B> result = new ArrayList<B>(arrayList.size());
	for (A a : arrayList) {
	    result.add(t.transform(a));
	}
	return result;
    }

}
