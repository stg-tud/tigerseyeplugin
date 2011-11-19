package de.tud.stg.popart.builder.test.dsls;
import groovy.lang.Closure;

import java.util.Arrays;
import java.util.HashMap;

import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;
import de.tud.stg.tigerseye.dslsupport.annotations.DSLMethod;
import de.tud.stg.tigerseye.dslsupport.annotations.DSLParameter;

/**
 * {@link SimpleSqlDSL} is a small DSL modeling a very simple subset of SQL operations
 * 
 * @author Kamil Erhard
 * 
 */
public class SimpleSqlDSL implements de.tud.stg.tigerseye.dslsupport.DSL {


	public Object eval(HashMap map, Closure cl) {
		cl.setDelegate(this);
		cl.setResolveStrategy(Closure.DELEGATE_FIRST);
		return cl.call();
	}

	@DSLMethod(production = "SELECT__p0__FROM__p1")	
	public String selectFrom(String[] columns, String[] tables) {
		return "sending QUERY: SELECT " + str(columns) + " FROM " + str(tables);
	}

	@DSLMethod(production = "SELECT__p0__FROM__p1__WHERE__p2")	
	public String selectFromWhere(String[] columns, String[] tables, @DSLParameter(arrayDelimiter = "AND") String[] checks) {
		return "sending QUERY: SELECT " + str(columns) + " FROM " + str(tables) + "WHERE" + " AND_concatenatedarray: " + str(checks);
	}
	
	private static String str(String[] strs){
		return Arrays.toString(strs);
	}
}
