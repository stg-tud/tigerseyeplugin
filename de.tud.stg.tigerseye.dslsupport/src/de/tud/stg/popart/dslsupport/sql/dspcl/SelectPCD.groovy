package de.tud.stg.popart.dslsupport.sql.dspcl

import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.pointcuts.Pointcut;
import de.tud.stg.popart.dslsupport.sql.dsjpm.SelectJoinPoint;
import de.tud.stg.popart.dslsupport.sql.model.AnyColumnReference;
import de.tud.stg.popart.dslsupport.sql.model.ColumnReference;
import static de.tud.stg.popart.dslsupport.sql.model.AnyColumnReference.ANY;

/**
 * Pointcut for select joinpoints
 * @see SQLPointcutInterpreter#pselect(java.util.List)
 */
class SelectPCD extends Pointcut {
	List<ColumnReference> selectList = []
	
	SelectPCD() {
		super("pselect")
	}
	
	SelectPCD(List<ColumnReference> selectList) {
		super("pselect(selectList)")
		this.selectList = selectList
	}
	
	@Override
	public boolean match(JoinPoint jp) {
		if (!(jp instanceof SelectJoinPoint)) return false
		def selectJp = jp as SelectJoinPoint
		if (selectList.contains(ANY)) {
			return selectJp.query.selectList.containsAll(selectList - [ANY]);
		} else {
			return selectJp.query.selectList.containsAll(selectList);
			/*
			//-DINKELAKER-2011-04-04-BEGIN
			//Refactoring: the selection of the columns from the pointcut is used to filter the queries 
			// (pointcut columns must be subset of queries tables)  
			//The two lists are permutations of each other.
			return selectJp.query.selectList.containsAll(selectList) && selectList.containsAll(selectJp.query.selectList);
		    //-DINKELAKER-2011-04-04-END
			*/
		}
	}
}
