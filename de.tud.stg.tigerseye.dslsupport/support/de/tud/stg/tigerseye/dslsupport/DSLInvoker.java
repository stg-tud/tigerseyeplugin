package de.tud.stg.tigerseye.dslsupport;

import groovy.lang.Closure;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.groovy.runtime.InvokerHelper;

import de.tud.stg.tigerseye.dslsupport.logger.DSLSupportLogger;

public final class DSLInvoker {
	
	private static final DSLSupportLogger logger = new DSLSupportLogger(DSLInvoker.class);
	
	// TODO(Leo_Roos;Nov 18, 2011) 
	// Shouldn't cache instances, they might change between files, need additional information about the file _and_ class to have an exact instance definition
	private static final ConcurrentHashMap<Class<? extends DSL>, DSL> invokers = new ConcurrentHashMap<Class<? extends DSL>, DSL>();

	public static Object eval(Class<? extends DSL> clazz, Closure<?> c) {
		return eval(toList(clazz),c);
	}

	public static Object eval(Class<? extends DSL>[] clazzes, Closure<?> c) {
		return eval(Arrays.asList(clazzes) , c);		
	}
	
	public static Object eval(List<Class<? extends DSL>> clazzes, Closure<?> c) {
		Set<DSL> dsls = new HashSet<DSL>();

		for (Class<? extends DSL> clazz : clazzes) {
			DSL dsl = getDSL(clazz);
			if (dsl != null)
				dsls.add(dsl);
		}

		InterpreterCombiner ic = new InterpreterCombiner(dsls, new HashMap<String, Object>());
		return ic.eval(c);
	}
	
	private static List<Class<? extends DSL>> toList(Class<?>... clazzes) {
		List<Class<? extends DSL>> result = new ArrayList<Class<? extends DSL>>(clazzes.length);
		for (Class<?> c : clazzes) {
			/* Throws exception if not of type DSL */
			Class<? extends DSL> clazz = (Class<? extends DSL>) c;
			result.add(clazz);
		}
		return result;
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
			
		//might still be null
		return (T) object;

	}

	static DSL getInvoker(Class<? extends DSL> clazz) {
		return getDSL(clazz);
	}
	
/*
	private Class<? extends DSL> clazz;

	private DSLInvoker() {
		//Not supposed to be instantiated
	}

	/**
	 * @deprecated use static methods for consistent access
	 //
	@Deprecated
	public DSLInvoker(Class<? extends DSL> clazz) {
		this.clazz = clazz;
	}

	/**
	 * @deprecated use static methods for consistent access
	 
	@Deprecated
	public Object eval(Closure<?> c) {
		if (this.clazz == null)
			throw new IllegalStateException("If instance eval is used the Invoker must have been initialized with a clazz");
		else
			return eval(this.clazz, c);
	}
	*/
	
}
