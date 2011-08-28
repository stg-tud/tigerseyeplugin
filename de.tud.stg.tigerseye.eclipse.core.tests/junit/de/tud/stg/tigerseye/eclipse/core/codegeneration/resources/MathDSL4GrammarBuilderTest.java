package de.tud.stg.tigerseye.eclipse.core.codegeneration.resources;

import groovy.lang.Closure;

import java.util.HashMap;

import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;

/**
 * {@link MathDSL4GrammarBuilderTest} is a DSL with some mathematical operations in unicode
 * 
 */
public class MathDSL4GrammarBuilderTest implements de.tud.stg.popart.dslsupport.DSL {

	public Object eval(HashMap map, Closure cl) {
		cl.setDelegate(this);
		cl.setResolveStrategy(Closure.DELEGATE_FIRST);
		return cl.call();
	}
	
	@DSLMethod(production="sum__p0")
	public int sum(int[] elements) {
		int sum = 0;
		for (int i : elements) {
			sum += i;
		}
		return sum;
	}
	
//	@DSLMethod(production="sum__p0__p1")
//	
//	public int sumTwo(int[] elements, int second) {
//		int sum = 0;
//		for (int i : elements) {
//			sum += i;
//		}
//		return sum;
//	}	

}
