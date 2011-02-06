package de.tud.stg.tigerseye.examples.simplesql;


import groovy.lang.Closure;

import java.util.Arrays;
import java.util.HashMap;

import de.tud.stg.popart.builder.core.annotations.DSL;
import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;

/**
 * {@link SimpleSqlDSL} is a small DSL modeling a very simple subset of SQL operations
 *
 * @author Kamil Erhard
 *
 */

public class SimpleSqlDSL implements de.tud.stg.popart.dslsupport.DSL {

	public Object eval(@SuppressWarnings("rawtypes") HashMap map, Closure cl) {
		cl.setDelegate(this);
		cl.setResolveStrategy(Closure.DELEGATE_FIRST);
		return cl.call();
	}

	@DSLMethod(prettyName = "SELECT__p0__FROM__p1")
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 0)
	public void selectFrom(String[] columns, String[] tables) {
		System.out.println("SimpleSqlDSL.selectFrom()"
				+ Arrays.toString(columns) + Arrays.toString(tables));
	}

	@DSLMethod(prettyName = "SELECT__p0__FROM__p1__WHERE__p2")
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 0)
	public void selectFromWhere(String[] columns, String[] tables, @DSL(arrayDelimiter = "AND") String[] checks) {
		System.out.println("SimpleSqlDSL.selectFromWhere()"
				+ Arrays.toString(columns) + Arrays.toString(tables)
				+ Arrays.toString(checks));
	}
}
