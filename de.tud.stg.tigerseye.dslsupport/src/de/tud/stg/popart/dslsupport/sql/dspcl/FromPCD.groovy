package de.tud.stg.popart.dslsupport.sql.dspcl;

import java.util.List;

import de.tud.stg.popart.dslsupport.sql.dsjpm.SelectJoinPoint;
import de.tud.stg.popart.dslsupport.sql.model.AnyTableReference;
import de.tud.stg.popart.dslsupport.sql.model.TableReference;
import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.pointcuts.Pointcut;

import static de.tud.stg.popart.dslsupport.sql.model.AnyTableReference.ANY_TABLE;

/**
 * Pointcut for select joinpoints
 * @see SQLPointcutInterpreter#pfrom(List)
 */
public class FromPCD extends Pointcut {
	List<TableReference> tableReferences = []
	
	public FromPCD() {
		super("pfrom");
	}
	
	public FromPCD(List<TableReference> tableReferences) {
		super("pfrom(tableReferences)")
		this.tableReferences = tableReferences
	}

	@Override
	public boolean match(JoinPoint jp) {
		if (!(jp instanceof SelectJoinPoint)) return false
		def selectJp = jp as SelectJoinPoint
		if (tableReferences.contains(ANY_TABLE)) {
			return selectJp.query.tableExpression.fromClause.tableReferences.containsAll(tableReferences - [ANY_TABLE]);
		} else {
			return selectJp.query.tableExpression.fromClause.tableReferences.containsAll(tableReferences);
			/*
			//-DINKELAKER-2011-04-04-BEGIN
			//Refactoring: the selection of the tables from the pointcut is used to filter the queries 
			// (pointcut tables must be subset of queries tables)  
			//The two lists are permutations of each other.
			return (selectJp.query.tableExpression.fromClause.tableReferences.containsAll(tableReferences)
				&& tableReferences.containsAll(selectJp.query.tableExpression.fromClause.tableReferences));
		    //-DINKELAKER-2011-04-04-END
			*/
			
		}
	}
}
