package de.tud.stg.popart.dslsupport.logo.dsjpm;

import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.IProceed 
import de.tud.stg.popart.aspect.extensions.instrumentation.JoinPointInstrumentation

public class TurtleBackwardJoinPointInstrumentation extends JoinPointInstrumentation {

	protected void prolog() {
		if (DEBUG) println("INSTRUMENTATION (MOP): \t prolog ${instrumentationContext.args[0]}");
		joinPointContext = new HashMap();
		joinPointContext.thisTurtle = instrumentationContext.receiver;
		joinPointContext.steps = instrumentationContext.args[0];
		joinPoint = new BackwardJoinPoint("", joinPointContext);
		joinPointContext.thisJoinPoint = joinPoint;
	}
	
	protected void prologForAround() {
  	    if (DEBUG) println("INSTRUMENTATION (AOP): \t around service call ${joinPointContext.operation}");
		joinPointContext.proceed = { int steps -> 
		  instrumentationContext.args = [joinPointContext.steps]; 
		  instrumentationContext.proceed()
		} as IProceed;
	} 
}
