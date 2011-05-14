package de.tud.stg.popart.dslsupport.sql.dsjpm

import de.tud.stg.popart.aspect.extensions.instrumentation.JoinPointInstrumentation;

class SelectJoinPointInstrumentation extends JoinPointInstrumentation {

	public SelectJoinPointInstrumentation() {
		super();
	}
	
	@Override
	protected void prolog() {
		joinPointContext = new HashMap()
		joinPoint = new SelectJoinPoint("pselect", joinPointContext)
		joinPoint.query = instrumentationContext.args[0]
		joinPointContext.thisJoinPoint = joinPoint
	}
	
	@Override
	protected void prologForAround() {
		joinPointContext.proceed = {
			instrumentationContext.args = [joinPointContext.query];
			instrumentationContext.proceed()
		};
	}
}
