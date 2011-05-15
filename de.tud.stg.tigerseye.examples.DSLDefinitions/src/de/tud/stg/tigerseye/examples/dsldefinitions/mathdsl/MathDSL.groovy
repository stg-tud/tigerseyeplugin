package de.tud.stg.tigerseye.examples.dsldefinitions.mathdsl;

import groovy.lang.Closure;

import java.util.HashMap;

import de.tud.stg.popart.builder.core.annotations.DSL;
import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;

/**
 * {@link MathDSL} is a DSL with some mathematical operations in unicode
 * 
 * @author Kamil Erhard
 * 
 */
@DSL
public class MathDSL implements de.tud.stg.popart.dslsupport.DSL {

	public Object eval(HashMap map, Closure cl) {
		cl.setDelegate(this);
		cl.setResolveStrategy(Closure.DELEGATE_FIRST);
		return cl.call();
	}

	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 0)
	public int sum__p0(int[] elements) {
		int sum = 0;
		for (int i : elements) {
			sum += i;
		}
		
		return sum;
	}
}
