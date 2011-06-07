package de.tud.stg.popart.builder.utils;

import groovy.lang.Closure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.popart.dslsupport.DSL;
import de.tud.stg.popart.dslsupport.InterpreterCombiner;

public class DSLInvoker {
	
	private static final Logger logger = LoggerFactory
			.getLogger(DSLInvoker.class);
	
	private static final DSLInvoker instance = new DSLInvoker();

	private static final Map<Class<? extends DSL>, DSL> invokers = new HashMap<Class<? extends DSL>, DSL>();

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

	/*
	 * FIXME(Leo Roos): Must use the @deprecated method until the runtime
	 * jars, which also package a version of an InterpreterCombiner, are
	 * generated from the same source this class accesses during
	 * development.
	 */
	InterpreterCombiner ic = new InterpreterCombiner(dsls,
		new HashMap<String, Object>());
		return ic.eval(c);
	}

	public static <T extends DSL> T getDSL(Class<T> clazz) {

		DSL object = invokers.get(clazz);

		if (object == null) {
			try {
				object = clazz.newInstance();
			} catch (InstantiationException e) {
				logger.error("Failed to instantiate class", e);
			} catch (IllegalAccessException e) {
				logger.error("Failed to instantiate class", e);
			}
			invokers.put(clazz, object);
		}

		return (T) object;

	}

	public static DSLInvoker getDSLInvoker() {
		return instance;
	}

	public static DSL getInvoker(Class<? extends DSL> clazz) {
		return getDSL(clazz);
	}

	private final Class<? extends DSL> clazz;

	private DSLInvoker() {
		this.clazz = null;
	}

	public DSLInvoker(Class<? extends DSL> clazz) {
		this.clazz = clazz;
	}

	public Object eval(Closure c) {
		if (this.clazz != null)
			return eval(this.clazz, c);
		else
			throw new IllegalStateException("Closure must not be null");
	}

}
