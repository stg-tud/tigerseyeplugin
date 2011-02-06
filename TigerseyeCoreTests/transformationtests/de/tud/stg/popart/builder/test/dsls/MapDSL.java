package de.tud.stg.popart.builder.test.dsls;

import groovy.lang.Closure;

import java.util.HashMap;
import java.util.Map;

import de.tud.stg.popart.builder.core.annotations.DSL;
import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;

/**
 * MapDSL is a DSL with operations to create a map with specified value/key pairs in one single statement
 * 
 * @author Kamil Erhard
 * 
 */
@DSL(whitespaceEscape = " ")
public class MapDSL implements de.tud.stg.popart.dslsupport.DSL {

	public Object eval(HashMap map, Closure cl) {
		cl.setDelegate(this);
		cl.setResolveStrategy(Closure.DELEGATE_FIRST);
		return cl.call();
	}

	public static class Entry<K, V> {
		private final K key;
		private final V value;

		public Entry(K key, V value) {
			this.key = key;
			this.value = value;
		}
	}

	// @PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 0)
	// @DSLPrettyMethodName(prettyName = "p0 = p1")
	// private Entry buildEntry(Object o, Object b) {
	// return new Entry(o, b);
	// }
	//
	// @PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 0)
	// @DSLPrettyMethodName(prettyName = "[ p0 ]")
	// public Map buildMap(Entry... entries) {
	// HashMap<Object, Object> map = new HashMap<Object, Object>();
	// for (Entry e : entries) {
	// map.put(e.key, e.value);
	// }
	// return map;
	// }

	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 0)
	@DSLMethod(prettyName = "p0 = p1", topLevel = false)
	public <K, V> Entry<K, V> buildEntry(K o, V b) {
		return new Entry<K, V>(o, b);
	}

	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 0)
	@DSLMethod(prettyName = "[ p0 , p1 : p2 ]")
	public <K, V> Map<K, V> buildMap(Class<K> keyClass, Class<V> valueClass, Entry<K, V>... entries) {
		HashMap<K, V> map = new HashMap<K, V>();
		for (Entry<K, V> e : entries) {
			map.put(e.key, e.value);
		}
		return map;
	}
}
