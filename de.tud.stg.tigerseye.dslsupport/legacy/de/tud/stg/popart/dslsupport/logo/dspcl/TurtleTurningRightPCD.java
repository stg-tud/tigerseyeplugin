package de.tud.stg.popart.dslsupport.logo.dspcl;

import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.pointcuts.Pointcut;

import de.tud.stg.popart.dslsupport.logo.dsjpm.*;

public class TurtleTurningRightPCD extends Pointcut {

	public TurtleTurningRightPCD() {
		super("right");
	}

	@Override
	public boolean match(JoinPoint jp) {
		return (jp instanceof RightJoinPoint);
	}

}
