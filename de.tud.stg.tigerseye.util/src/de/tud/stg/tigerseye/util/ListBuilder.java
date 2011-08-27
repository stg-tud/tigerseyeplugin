package de.tud.stg.tigerseye.util;

import java.util.LinkedList;
import java.util.List;

/**
 * Utility class to create type safe lists on a single line. Uses the
 * {@link LinkedList} implementation internally since it assumes that one will
 * usually create short lists with this method.
 * <p>
 * Example to create a two element list:
 * 
 * <pre>
 * List&lt;Integer&gt; intList = ListBuilder.newList(1).add(2).toList();
 * </pre>
 * 
 * @author Leo Roos
 * 
 * @param <T>
 *            type of the list
 */
public class ListBuilder<T> {

	private final LinkedList<T> list;

	private ListBuilder() {
		this.list = new LinkedList<T>();
	}

	public ListBuilder(T firstEl) {
		this();
		add(firstEl);
	}

	public ListBuilder<T> add(T el) {
		this.list.add(el);
		return this;
	}

	public List<T> toList() {
		return this.list;
	}

	public static <T> ListBuilder<T> newList(T element) {
		ListBuilder<T> listGen = new ListBuilder<T>(element);
		return listGen;
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
		return new ListBuilder<T>(element).toList();
	}

}