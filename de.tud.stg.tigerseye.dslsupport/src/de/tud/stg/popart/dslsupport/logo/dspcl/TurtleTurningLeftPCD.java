package de.tud.stg.popart.dslsupport.logo.dspcl;

import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.pointcuts.Pointcut;

import de.tud.stg.popart.dslsupport.logo.dsjpm.*;

public class TurtleTurningLeftPCD extends Pointcut {

	public TurtleTurningLeftPCD() {
		super("left");
	}

	@Override
	public boolean match(JoinPoint jp) {
		return 
		  (jp instanceof LeftJoinPoint);
	}
}
