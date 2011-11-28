package de.tud.stg.popart.builder.test.dsls;

import static org.junit.Assert.fail;
import groovy.lang.Closure;

import java.util.HashMap;

import org.junit.Test;

import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;
import de.tud.stg.tigerseye.dslsupport.annotations.DSLMethod;

/**
 * {@link MathDSL} is a DSL with some mathematical operations in unicode
 * 
 * @author Kamil Erhard
 * 
 */
public class MathDSL implements de.tud.stg.tigerseye.dslsupport.DSL {

	public Object eval(HashMap map, Closure cl) {
		cl.setDelegate(this);
		cl.setResolveStrategy(Closure.DELEGATE_FIRST);
		return cl.call();
	}
	
	@DSLMethod(production="sum__p0", isUnicodeEncoding=true)
	public int sum__p0(int[] elements) {
		int sum = 0;
		for (int i : elements) {
			sum += i;
		}
		return sum;
	}	

	public static void main(String[] args) {

	}
}
