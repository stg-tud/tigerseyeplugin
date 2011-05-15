package de.tud.stg.popart.dslsupport.logo.dspcl;

import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.pointcuts.Pointcut;

import de.tud.stg.popart.dslsupport.logo.dsjpm.*;

public class TurtleTurningPCD extends Pointcut {

	public TurtleTurningPCD() {
		super("turning");
	}

	@Override
	public boolean match(JoinPoint jp) {
		return 
		  (jp instanceof LeftJoinPoint) ||
		  (jp instanceof RightJoinPoint);
	}

}
