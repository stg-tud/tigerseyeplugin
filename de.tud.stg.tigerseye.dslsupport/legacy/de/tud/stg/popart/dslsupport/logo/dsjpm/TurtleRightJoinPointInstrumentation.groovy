package de.tud.stg.popart.dslsupport.logo.dsjpm;

import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.IProceed 
import de.tud.stg.popart.aspect.extensions.instrumentation.JoinPointInstrumentation

public class TurtleRightJoinPointInstrumentation extends JoinPointInstrumentation {

	protected void prolog() {
		if (DEBUG) println("INSTRUMENTATION (MOP): \t prolog ${instrumentationContext.args[0]}");
		joinPointContext = new HashMap();
		joinPointContext.thisTurtle = instrumentationContext.receiver;
		joinPointContext.degrees = instrumentationContext.args[0];
		joinPoint = new RightJoinPoint("", joinPointContext);
		joinPointContext.thisJoinPoint = joinPoint;
	}
	
	protected void prologForAround() {
  	    if (DEBUG) println("INSTRUMENTATION (AOP): \t around service call ${joinPointContext.operation}");
		joinPointContext.proceed = { int steps -> 
		  instrumentationContext.args = [joinPointContext.degrees]; 
		  instrumentationContext.proceed()
		} as IProceed;
	} 
}
