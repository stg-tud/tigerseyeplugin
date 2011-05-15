package de.tud.stg.popart.dslsupport.sql.dsjpm

import de.tud.stg.popart.aspect.extensions.instrumentation.JoinPointInstrumentation;

class UpdateJoinPointInstrumentation extends JoinPointInstrumentation {
	
	public UpdateJoinPointInstrumentation() {
		super();
	}
	
	@Override
	protected void prolog() {
		joinPointContext = new HashMap()
		joinPoint = new UpdateJoinPoint("pupdate", joinPointContext)
		joinPoint.updateStatement = instrumentationContext.args[0]
		joinPointContext.thisJoinPoint = joinPoint
	}
	
	@Override
	protected void prologForAround() {
		joinPointContext.proceed = {
			instrumentationContext.args = [joinPointContext.updateStatement];
			instrumentationContext.proceed()
		  };
	}
}
