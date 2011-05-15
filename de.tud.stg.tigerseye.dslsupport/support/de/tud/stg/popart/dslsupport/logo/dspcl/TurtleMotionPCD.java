package de.tud.stg.popart.dslsupport.logo.dspcl;

import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.pointcuts.Pointcut;

import de.tud.stg.popart.dslsupport.logo.dsjpm.*;

public class TurtleMotionPCD extends Pointcut {

	public TurtleMotionPCD() {
		super("pmotion");
	}

	@Override
	public boolean match(JoinPoint jp) {
		return 
		  (jp instanceof BackwardJoinPoint) ||
		  (jp instanceof ForwardJoinPoint) ||
		  (jp instanceof LeftJoinPoint) ||
		  (jp instanceof RightJoinPoint);
	}

}
