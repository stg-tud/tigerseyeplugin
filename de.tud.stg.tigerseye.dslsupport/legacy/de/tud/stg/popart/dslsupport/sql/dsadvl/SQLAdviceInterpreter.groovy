package de.tud.stg.popart.dslsupport.sql.dsadvl;

import de.tud.stg.popart.dslsupport.sql.model.UpdateStatement;
import de.tud.stg.popart.dslsupport.sql.model.ColumnReference;
import de.tud.stg.popart.dslsupport.sql.dsjpm.UpdateJoinPoint;
import de.tud.stg.popart.dslsupport.sql.dsjpm.InsertJoinPoint;
import de.tud.stg.popart.dslsupport.sql.dsjpm.UpdateJoinPoint;
import de.tud.stg.popart.dslsupport.sql.model.SetClause;
import de.tud.stg.popart.dslsupport.sql.ISQLConnection;
import de.tud.stg.popart.dslsupport.sql.dsjpm.SelectJoinPoint;
import de.tud.stg.popart.dslsupport.sql.model.AnyColumnReference;
import de.tud.stg.popart.dslsupport.sql.model.AnyTableReference;
import de.tud.stg.popart.dslsupport.sql.model.ColumnReference;
import de.tud.stg.popart.dslsupport.sql.model.Identifier 
import de.tud.stg.popart.dslsupport.sql.model.InsertStatement;
import de.tud.stg.popart.dslsupport.sql.model.TableReference;
import static de.tud.stg.popart.dslsupport.sql.model.AnyTableReference.ANY_TABLE;
import static de.tud.stg.popart.dslsupport.sql.ListHelper.*;

import java.util.Map;
import de.tud.stg.popart.aspect.CCCombiner;
import de.tud.stg.popart.aspect.WrappingProceed;

import groovy.lang.MissingPropertyException;
import de.tud.stg.popart.aspect.ProceedingAdviceDSL;

/**
 * This is the interpreter for the advice language. The advice
 * language supports the modification of SELECT, UPDATE and INSERT
 * statements. 
 */
class SQLAdviceInterpreter extends ProceedingAdviceDSL {
	
	private static final DEBUG = false;
	CCCombiner ccc = null;
	
	public SQLAdviceInterpreter() {
		super();
	}

	public void setCCCombiner(CCCombiner ccc)  {
		println "CCC: "+ccc
		this.ccc = ccc;
	}

	public Map<String, Object> getContext() {
		//println "CONTEXT: "+ccc.getContext()
		return ccc.getContext();
	}
	 
	/**
	 * Proceed the execution of the query / statement without change
	 */
	public Object proceed() {
		/*The old version, which is commented out below, does not work, because Groovy 
		//sometimes tries to execute proceed() on java.util.HashMap */
		//return getContext().proceed()
		WrappingProceed proc = getContext().get("proceed");
		return proc.call();
	}
	
	/**
	 * This method replaces the columns of the query in the select joinpoint.
	 * @param cols
	 *     new columns to use. Use {@link AnyColumnReference#ANY} to include
	 *     all columns of the original query 
	 * @return result of the query
	 * @see #proceed_select(List, List)
	 */
	public Object proceed_select(List<ColumnReference> cols) {
		return proceed_select(cols, [ANY_TABLE]);
	}
	
	public Object proceed_SELECT(List<ColumnReference> cols) {
		return proceed_select(cols, [ANY_TABLE]);
	}
	
	/**
	 * This method replaces both the columns and the tables of the query in the
	 * select joinpoint.
	 * 
     * <p><b>Example:</b><br />
     * <b>PROCEED SELECT ANY, </b> <i>Tab1.Col1</i> <b>FROM</b> <i>Tab1</i></p>
	 * 
	 * @param cols new columns to use. Use {@link AnyColumnReference#ANY} to include
	 *     all columns of the original query
	 * @param tables new tables to use. Use {@link AnyTableReference#ANY_TABLE} to
	 *     include all tables of the original query
	 * @return result of the query
	 */
	public Object proceed_select(List<ColumnReference> cols, List<TableReference> tables) {
		def joinPoint = getContext().thisJoinPoint;
		//This advice is only for select statements
		assert (joinPoint instanceof SelectJoinPoint);
		SelectJoinPoint selectJoinPoint = joinPoint as SelectJoinPoint;
		List<ColumnReference> newColumns = [];
		List<TableReference> newTables = [];
		cols.each {
			if (it instanceof AnyColumnReference) {
				newColumns.addAll(selectJoinPoint.query.selectList);
			} else { newColumns.add(it); }
		};
		tables.each {
			if (it instanceof AnyTableReference) {
				newTables.addAll(selectJoinPoint.query.tableExpression.fromClause.tableReferences);
			} else { newTables.add(it); }
		};
		selectJoinPoint.query.selectList = convertToColumnReferenceList(newColumns);
		selectJoinPoint.query.tableExpression.fromClause.tableReferences = convertToTableReferenceList(newTables);
		return proceed();
	}

	public Object proceed_SELECT(List<ColumnReference> cols, List<TableReference> tables) {
		return proceed_select(cols, tables); 
	}

	/**
	 * This method modifies the update statement of the update joinpoint.
	 * All given clauses will be appended to the update statement
	 * @param additionalClauses additional clauses to append
	 * @return result of the update statement
	 * @see UpdateStatement
	 * @see UpdateJoinPoint
	 */
	public Object proceed_update(List<SetClause> additionalClauses) {
		def joinPoint = getContext().thisJoinPoint;
		//This advice is only for update statements
		assert (joinPoint instanceof UpdateJoinPoint);
		UpdateJoinPoint updateJoinPoint = joinPoint as UpdateJoinPoint;
		updateJoinPoint.updateStatement.setClauseList.addAll(additionalClauses);
		return proceed();
	}

	public Object proceed_UPDATE(List<SetClause> additionalClauses) {
		return proceed_update(additionalClauses);
	}

	/**
	 * This method modifies the insert statement of the insert joinpoint.
	 * All entries of the given map will be added to the insert statement. 
	 * @param additionalValues additional column-value-pairs to add
	 * @return result of the insert statement
	 * @see InsertStatement
	 * @see InsertJoinPoint
	 */
	public Object proceed_insert(Map<String,String> additionalValues) {
		def JoinPoint = getContext().thisJoinPoint;
		//This advice is only for insert statements
		assert (JoinPoint instanceof InsertJoinPoint);
		InsertJoinPoint insertJoinPoint = JoinPoint as InsertJoinPoint;
		insertJoinPoint.insertStatement.values.putAll(additionalValues);
		return proceed();
	}
		
	public Object proceed_INSERT(Map<String,String> additionalValues) {
	    return proceed_insert(additionalValues); 
	}
}