package de.tud.stg.tigerseye.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to create type safe lists on a single line.
 * 
 * @author Leo Roos
 * 
 * @param <T>
 *            type of the list
 */
public class ListBuilder<T> {

	private final ArrayList<T> list;

	private ListBuilder() {
		this.list = new ArrayList<T>();
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

	public static <T> ListBuilder<T> begin(T element) {
		ListBuilder<T> listGen = new ListBuilder<T>(element);
		return listGen;
	}
	
	public static <T> List<T> single(T element){
	    return new ListBuilder<T>(element).toList();
	}

}