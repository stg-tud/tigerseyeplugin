package de.tud.stg.popart.builder.utils;

import groovy.lang.Closure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.tud.stg.popart.dslsupport.DSL;
import de.tud.stg.popart.dslsupport.InterpreterCombiner;

public class DSLInvoker {
	private static final DSLInvoker instance = new DSLInvoker();

	private static final Map<Class<? extends DSL>, DSL> invokers = new HashMap<Class<? extends DSL>, DSL>();

	private final Class<? extends DSL> clazz;

	public static <T extends DSL> T getDSL(Class<T> clazz) {

		DSL object = invokers.get(clazz);

		if (object == null) {
			try {
				object = clazz.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			invokers.put(clazz, object);
		}

		return (T) object;

	}

	private DSLInvoker() {
		this.clazz = null;
	}

	public DSLInvoker(Class<? extends DSL> clazz) {
		this.clazz = clazz;
	}

	public static DSLInvoker getDSLInvoker() {
		return instance;
	}

	public static DSL getInvoker(Class<? extends DSL> clazz) {
		return getDSL(clazz);
	}

	public Object eval(Closure c) {
		if (this.clazz != null)
			return eval(this.clazz, c);
		else
			throw new IllegalStateException();
	}

	public static Object eval(Class<? extends DSL> clazz, Closure c) {
		DSL dsl = getDSL(clazz);

		c.setDelegate(dsl);
		c.setResolveStrategy(Closure.DELEGATE_FIRST);
		return c.call();
	}

	public static Object eval(Class<? extends DSL>[] clazzes, Closure c) {
		List<DSL> dsls = new LinkedList<DSL>();

		for (Class<? extends DSL> clazz : clazzes) {
			dsls.add(getDSL(clazz));
		}

		InterpreterCombiner ic = new InterpreterCombiner(dsls, new HashMap());
		return ic.eval(c);
	}

	public static <T> T[] asArray(T... ts) {
		return ts;
	}

	public static <T> List<T> asList(T... ts) {
		ArrayList<T> list = new ArrayList<T>(ts.length);
		for (T t : ts) {
			list.add(t);
		}
		return list;
	}

	public static void main(String[] args) {
		List<Integer>[] asArray = asArray(new ArrayList<Integer>(), new ArrayList<Integer>(), new ArrayList<Integer>());

		List<ArrayList<Integer>> asList = Arrays.asList(new ArrayList<Integer>(), new ArrayList<Integer>(),
				new ArrayList<Integer>());

	}
}
