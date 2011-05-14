package de.tud.stg.popart.dslsupport.sql.dspcl

import de.tud.stg.popart.dslsupport.sql.dsjpm.UpdateJoinPoint;
import de.tud.stg.popart.dslsupport.sql.model.QualifiedName;
import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.pointcuts.Pointcut;

/**
 * Poincut for update joinpoints
 * @see SQLPointcutInterpreter#pupdate()
 */
class UpdatePCD extends Pointcut {
	QualifiedName tableName;
	
	public UpdatePCD() {
		super("pupdate");
	}
	
	public UpdatePCD(QualifiedName table) {
		super("pupdate(table)");
		this.tableName = table;
	}
	
	@Override
	public boolean match(JoinPoint jp) {
		if (!(jp instanceof UpdateJoinPoint)) return false;
		def updateJp = jp as UpdateJoinPoint;
		if (tableName == null) return true;
		return tableName == updateJp.updateStatement.tableName;
	}
}
