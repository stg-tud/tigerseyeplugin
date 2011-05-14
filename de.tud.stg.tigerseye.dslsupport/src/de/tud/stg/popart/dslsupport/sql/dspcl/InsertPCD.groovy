package de.tud.stg.popart.dslsupport.sql.dspcl;

import de.tud.stg.popart.dslsupport.sql.dsjpm.InsertJoinPoint;
import de.tud.stg.popart.dslsupport.sql.model.QualifiedName;
import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.pointcuts.Pointcut;

/**
 * Pointcut for insert joinpoints
 * @see SQLPointcutInterpreter#pinsert()
 */
public class InsertPCD extends Pointcut {
	QualifiedName tableName;
	
	public InsertPCD() {
		super("pinsert");
	}
	
	public InsertPCD(QualifiedName tableName) {
		super("pselect(table)");
		this.tableName = tableName;
	}

	@Override
	public boolean match(JoinPoint jp) {
		if (!(jp instanceof InsertJoinPoint)) return false;
		if (tableName == null) return true;
		def insertJp = jp as InsertJoinPoint;
		return tableName == insertJp.insertStatement.tableName;
	}
}
