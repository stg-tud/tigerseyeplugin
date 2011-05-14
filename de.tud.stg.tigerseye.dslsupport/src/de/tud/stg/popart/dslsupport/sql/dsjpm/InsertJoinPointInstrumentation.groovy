package de.tud.stg.popart.dslsupport.sql.dsjpm;

import de.tud.stg.popart.aspect.extensions.instrumentation.JoinPointInstrumentation;

public class InsertJoinPointInstrumentation extends JoinPointInstrumentation {
	
	public InsertJoinPointInstrumentation() {
		super();
	}
	
	@Override
	protected void prolog() {
		joinPointContext = new HashMap()
		joinPoint = new InsertJoinPoint("pinsert", joinPointContext)
		joinPoint.insertStatement = instrumentationContext.args[0]
		joinPointContext.thisJoinPoint = joinPoint
	}
	
	@Override
	protected void prologForAround() {
		joinPointContext.proceed = {
			instrumentationContext.args = [joinPointContext.insertStatement];
			instrumentationContext.proceed()
		  };
	}
}
