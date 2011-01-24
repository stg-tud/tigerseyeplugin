package de.tud.stg.popart.builder.test.dsls;

import groovy.lang.Closure;

import java.util.HashMap;
import java.util.Map;

import de.tud.stg.popart.builder.core.annotations.DSL;
import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;

/**
 * {@link StatefulDSL} is a small DSL showing the possibility of setting and retrieving variables.
 * 
 * @author Kamil Erhard
 * 
 */
@DSL
public class StatefulDSL implements de.tud.stg.popart.dslsupport.DSL {

	public Object eval(HashMap map, Closure cl) {
		cl.setDelegate(this);
		cl.setResolveStrategy(Closure.DELEGATE_FIRST);
		return cl.call();
	}

	Map<String, Object> variables = new HashMap<String, Object>();

	// @DSLAlias(prettyName = "SELECT_p0_FROM_p1")
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 0)
	public void set_p0_equals_p1(String key, Object value) {
		this.variables.put(key, value);
	}

	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 0)
	public Object get_p0(String key) {
		return this.variables.get(key);
	}

}
