package de.tud.stg.tigerseye.examples.dsldefinitions.foreachsyntaxdsl;

import groovy.lang.Closure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import de.tud.stg.popart.builder.core.annotations.DSL;
import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;

/**
 * {@link ForEachSyntaxDSL} is a DSL with support for an forEach statement (without using the built in java one)
 * 
 * @author Kamil Erhard
 * 
 */
@DSL(whitespaceEscape = " ")
public class ForEachSyntaxDSL implements de.tud.stg.popart.dslsupport.DSL {

	public Object eval(HashMap map, Closure cl) {
		cl.setDelegate(this);
		cl.setResolveStrategy(Closure.DELEGATE_FIRST);
		return cl.call();
	}

	@DSLMethod(prettyName = "for ( p0  p1 : p2 ) { p3 }")
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 0)
	public <T> void forEach(Class<T> t, String var, Iterable<T> list, Closure c) {
		this.iterate(var, list.iterator(), c);
	}

	@DSLMethod(prettyName = "for ( p0  p1 : p2 ) { p3 }")
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 0)
	public <T> void forEach(Class<T> t, String var, T[] array, Closure c) {

		this.iterate(var, Arrays.asList(array).iterator(), c);
	}

	private <T> void iterate(String var, Iterator<T> it, Closure c) {
		while (it.hasNext()) {
			T next = it.next();
			c.setProperty(var, next);
			c.call();
		}
	}
}
