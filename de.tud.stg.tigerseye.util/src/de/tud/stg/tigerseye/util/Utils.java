package de.tud.stg.tigerseye.util;

import java.util.List;

public class Utils {

	static public void throwIllegalFor(Object unwanted) {
		throw new IllegalArgumentException("Unknown or unhandled value [" + unwanted + "].");
	}
	
	public static <T> ListBuilder<T> newList(T element) {
		return ListBuilder.newList(element);
	}

	/**
	 * Shorthand. Instead of writing
	 * 
	 * <pre>
	 * newList(someObject).toList();
	 * </pre>
	 * 
	 * one can as well write
	 * 
	 * <pre>
	 * single(someObject);
	 * </pre>
	 */
	public static <T> List<T> single(T element) {
		return ListBuilder.single(element);
	}

}
