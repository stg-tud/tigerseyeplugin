package de.tud.stg.popart.builder.test.dsls;

import groovy.lang.Closure;

import java.util.HashMap;

import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartLiteralKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;

/**
 * {@link PrefixDSL} is a DSL with support for conditional statements in an alternative if-then-else syntax
 * 
 * @author Kamil Erhard
 * 
 */
public class PrefixDSL implements de.tud.stg.popart.dslsupport.DSL {

	public Object eval(HashMap map, Closure cl) {
		cl.setDelegate(this);
		cl.setResolveStrategy(Closure.DELEGATE_FIRST);
		return cl.call();
	}

	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 0)
	public int start__state__p0(int i) {
		return 0;
	}

	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 0)
	public int state__p0(int i) {
		return 0;
	}

	@PopartType(clazz = PopartLiteralKeyword.class, breakpointPossible = 0)
	public int getStart() {
		return 0;
	}
}
