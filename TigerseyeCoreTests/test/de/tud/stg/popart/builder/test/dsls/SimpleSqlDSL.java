package de.tud.stg.popart.builder.test.dsls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import groovy.lang.Closure;

import java.util.HashMap;

import de.tud.stg.popart.builder.core.annotations.DSL;
import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;

/**
 * {@link SimpleSqlDSL} is a small DSL modelling a very simple subset of SQL operations
 * 
 * @author Kamil Erhard
 * 
 */

public class SimpleSqlDSL implements de.tud.stg.popart.dslsupport.DSL {
private static final Logger logger = LoggerFactory.getLogger(SimpleSqlDSL.class);


	public Object eval(HashMap map, Closure cl) {
		cl.setDelegate(this);
		cl.setResolveStrategy(Closure.DELEGATE_FIRST);
		return cl.call();
	}

	@DSLMethod(prettyName = "SELECT__p0__FROM__p1")
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 0)
	public void selectFrom(String[] columns, String[] tables) {
		logger.info("sending QUERY");
	}

	@DSLMethod(prettyName = "SELECT__p0__FROM__p1__WHERE__p2")
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 0)
	public void selectFromWhere(String[] columns, String[] tables, @DSL(arrayDelimiter = "AND") String[] checks) {
		logger.info("sending QUERY");
	}
}
