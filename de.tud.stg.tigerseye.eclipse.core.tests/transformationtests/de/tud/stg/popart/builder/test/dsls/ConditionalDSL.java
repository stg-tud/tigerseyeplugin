package de.tud.stg.popart.builder.test.dsls;

import groovy.lang.Closure;

import java.util.HashMap;

import de.tud.stg.popart.builder.core.annotations.DSLClass;
import de.tud.stg.popart.builder.core.annotations.DSLMethod;

/**
 * {@link ConditionalDSL} is a DSL with support for conditional statements in an alternative if-then-else syntax
 * 
 * @author Kamil Erhard
 * 
 */
@DSLClass(whitespaceEscape = " ")
public class ConditionalDSL implements de.tud.stg.popart.dslsupport.DSL {

	public Object eval(HashMap map, Closure cl) {
		cl.setDelegate(this);
		cl.setResolveStrategy(Closure.DELEGATE_FIRST);
		return cl.call();
	}

	
	@DSLMethod(production = "if  (  p0  )  then  {  p1  }  else  {  p2  }")
	public boolean ifThenElse(boolean check, Closure thenBlock, Closure elseBlock) {
		if (check) {
			thenBlock.call();
		} else {
			elseBlock.call();
		}

		return check;
	}
}
