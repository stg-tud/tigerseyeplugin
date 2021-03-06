package de.tud.stg.tigerseye.examples.mapdsl;

import groovy.lang.Closure;

import java.util.HashMap;
import java.util.Map;

import de.tud.stg.tigerseye.dslsupport.annotations.DSLParameter;
import de.tud.stg.tigerseye.dslsupport.annotations.DSLMethod;
import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;


/**
 * MapDSL is a DSL with operations to create a map with specified value/key pairs in one single statement
 * 
 * @author Kamil Erhard
 * 
 */
@DSL(whitespaceEscape = " ")
public class MapDSL implements de.tud.stg.tigerseye.dslsupport.DSL {

	public Object eval(HashMap map, Closure cl) {
		cl.setDelegate(this);
		cl.setResolveStrategy(Closure.DELEGATE_FIRST);
		return cl.call();
	}


	
	@DSLMethod(production = "p0 = p1", topLevel=false)
	public <K, V> Entry<K, V> buildEntry(K o, V b) {
		return new Entry<K, V>(o, b);
	}

	
	@DSLMethod(production = "[ p0 , p1 : p2 ]")
	public <K, V> Map<K, V> buildMap(Class<K> keyClass, Class<V> valueClass, Entry<K, V>... entries) {
		HashMap<K, V> map = new HashMap<K, V>();
		for (Entry<K, V> e : entries) {
			map.put(e.getKey(), e.getValue());
		}
		return map;
	}
}
