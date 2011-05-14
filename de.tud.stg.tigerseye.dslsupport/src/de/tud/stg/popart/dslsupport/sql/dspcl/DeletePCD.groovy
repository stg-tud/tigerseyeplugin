package de.tud.stg.popart.dslsupport.sql.dspcl;

import de.tud.stg.popart.dslsupport.sql.dsjpm.DeleteJoinPoint;
import de.tud.stg.popart.dslsupport.sql.model.QualifiedName;
import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.pointcuts.Pointcut;

/**
 * Pointcut for delete joinpoints
 * @see SQLPointcutInterpreter#pdelete()
 */
public class DeletePCD extends Pointcut {
	QualifiedName tableName;
	
	public DeletePCD() {
		super("pdelete");
	}
	
	public DeletePCD(QualifiedName table) {
		super("pdelete(table)");
		this.tableName = table;
	}
	
	@Override
	public boolean match(JoinPoint jp) {
		if (!(jp instanceof DeleteJoinPoint)) return false;
		def deleteJp = jp as DeleteJoinPoint;
		if (tableName == null) return true;
		return tableName == deleteJp.deleteStatement.tableName;
	}
}
