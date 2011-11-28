package de.tud.stg.popart.builder.test.dsls;

import groovy.lang.Closure;

import java.util.HashMap;

import de.tud.stg.tigerseye.dslsupport.annotations.DSLClass;
import de.tud.stg.tigerseye.dslsupport.annotations.DSLMethod;

/**
 * {@link NumberRepresentationDSL} is a DSL with support for conditional statements in an alternative if-then-else
 * syntax
 * 
 * @author Kamil Erhard
 * 
 */
@DSLClass(whitespaceEscape = " ")
public class NumberRepresentationDSL implements de.tud.stg.tigerseye.dslsupport.DSL {

	public Object eval(HashMap map, Closure cl) {
		cl.setDelegate(this);
		cl.setResolveStrategy(Closure.DELEGATE_FIRST);
		return cl.call();
	}

	
	@DSLMethod(production = "p0  b")
	public void booleanNumber(int num) {

	}

	
	@DSLMethod(production = "p0  h")
	public void hexalNumber(int num) {

	}
}
