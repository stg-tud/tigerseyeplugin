package de.tud.stg.popart.dslsupport.logo.dspcl;

import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.pointcuts.Pointcut;

import de.tud.stg.popart.dslsupport.logo.dsjpm.*;

public class TurtleMovingForwardPCD extends Pointcut {

	public TurtleMovingForwardPCD() {
		super("pforward");
	}

	@Override
	public boolean match(JoinPoint jp) {
		return (jp instanceof ForwardJoinPoint);
	}

}
