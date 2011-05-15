package de.tud.stg.popart.dslsupport.logo.dsjpm;

import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.IProceed 
import de.tud.stg.popart.aspect.extensions.instrumentation.JoinPointInstrumentation

public class TurtleLeftJoinPointInstrumentation extends JoinPointInstrumentation {

	protected void prolog() {
		if (DEBUG) println("INSTRUMENTATION (MOP): \t prolog ${instrumentationContext.args[0]}");
		joinPointContext = new HashMap();
		joinPointContext.thisTurtle = instrumentationContext.receiver;
		joinPointContext.degrees = instrumentationContext.args[0];
		joinPoint = new LeftJoinPoint("", joinPointContext);
		joinPointContext.thisJoinPoint = joinPoint;
	}
	
}
