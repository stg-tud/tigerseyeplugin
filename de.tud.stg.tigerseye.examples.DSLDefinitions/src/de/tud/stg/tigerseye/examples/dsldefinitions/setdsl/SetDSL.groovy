package de.tud.stg.tigerseye.examples.dsldefinitions.setdsl;

import groovy.lang.Closure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import de.tud.stg.popart.builder.core.annotations.DSL;
import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;

/**
 * {@link SetDSL} is a small DSL showing the possibility of modelling mathematical sets
 * 
 * @author Kamil Erhard
 * 
 */
@DSL(whitespaceEscape = " ")
public class SetDSL implements de.tud.stg.popart.dslsupport.DSL {

	public Object eval(HashMap map, Closure cl) {
		cl.setDelegate(this);
		cl.setResolveStrategy(Closure.DELEGATE_FIRST);
		return cl.call();
	}

	@DSLMethod(prettyName = "p0 ⋃ p1")
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 0)
	public Set union(Set a, Set b) {
		HashSet set = new HashSet(a);
		set.addAll(b);
		return set;
	}

	@DSLMethod(prettyName = "p0 ⋂ p1")
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 0)
	public Set intersection(Set a, Set b) {
		HashSet set = new HashSet(a);
		set.addAll(b);
		return set;
	}

	@DSLMethod(prettyName = "{ p0 }")
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 0)
	public Set asSet(String[] elements) {
		return new HashSet(Arrays.asList(elements));
	}
}
