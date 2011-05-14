package de.tud.stg.popart.dslsupport.logo.dspcl;

import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.pointcuts.Pointcut;

import de.tud.stg.popart.dslsupport.logo.dsjpm.*;

public class TurtleMovingStepsPCD extends Pointcut {

	public TurtleMovingStepsPCD(int steps) {
		super("pmoving(steps)");
		this.steps = steps;
	}
	private int steps;

	public int getSteps() {
		return steps;
	}

	public void setSteps(int steps) {
		this.steps = steps;
	}
	@Override
	public boolean match(JoinPoint jp) {
		return 
		  ((jp instanceof BackwardJoinPoint) ||
		  (jp instanceof ForwardJoinPoint)) &&
		  (steps == ((TurtleMoveJoinPoint)jp).getSteps());
	}
	
}
