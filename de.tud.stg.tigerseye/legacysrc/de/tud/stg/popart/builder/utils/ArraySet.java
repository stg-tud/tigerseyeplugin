package de.tud.stg.popart.builder.utils;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ArraySet<T> extends AbstractList<T> implements Set<T> {

	private final ArrayList<T> arrayList = new ArrayList<T>();
	private final Set<T> set = new HashSet<T>();

	@Override
	public T get(int index) {
		return this.arrayList.get(index);
	}

	@Override
	public int size() {
		return this.arrayList.size();
	}

	@Override
	public void add(int index, T element) {
		boolean changed = this.set.add(element);
		if (changed) {
			this.arrayList.add(index, element);
		}
	};

	@Override
	public boolean add(T element) {
		boolean changed = this.set.add(element);
		if (changed) {
			this.arrayList.add(element);
		}

		return changed;
	};
}
