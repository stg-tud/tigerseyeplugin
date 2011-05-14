package de.tud.stg.popart.dslsupport.sql.dsjpm;

import de.tud.stg.popart.aspect.extensions.instrumentation.JoinPointInstrumentation;

public class DeleteJoinPointInstrumentation extends JoinPointInstrumentation {
	
	public DeleteJoinPointInstrumentation() {
		super();
	}
	
	@Override
	protected void prolog() {
		joinPointContext = new HashMap();
		joinPoint = new DeleteJoinPoint("pdelete", joinPointContext);
		joinPoint.deleteStatement = instrumentationContext.args[0];
		joinPointContext.thisJoinPoint = joinPoint;
	}
	
	@Override
	protected void prologForAround() {
		joinPointContext.proceed = {
			instrumentationContext.args = [joinPointContext.deleteStatement];
			instrumentationContext.proceed()
		  };
	}
}
