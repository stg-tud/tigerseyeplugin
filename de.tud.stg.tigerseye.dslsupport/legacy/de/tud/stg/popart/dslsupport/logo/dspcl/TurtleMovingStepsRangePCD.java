package de.tud.stg.popart.dslsupport.logo.dspcl;

import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.pointcuts.Pointcut;

import de.tud.stg.popart.dslsupport.logo.dsjpm.*;

public class TurtleMovingStepsRangePCD extends Pointcut {

	public TurtleMovingStepsRangePCD(int minSteps, int maxSteps) {
		super("pmoving(minSteps,maxSteps)");
		this.minSteps = minSteps;
		this.maxSteps = maxSteps;
	}
	
	private int minSteps;

	public int getMinSteps() {
		return minSteps;
	}

	public void setMinSteps(int minSteps) {
		this.minSteps = minSteps;
	}

	private int maxSteps;

	public int getMaxSteps() {
		return maxSteps;
	}

	public void setMaxSteps(int maxSteps) {
		this.maxSteps = maxSteps;
	}
	
	@Override
	public boolean match(JoinPoint jp) {
		return 
		  ((jp instanceof BackwardJoinPoint) ||
		  (jp instanceof ForwardJoinPoint)) &&
		  (minSteps <= ((TurtleMoveJoinPoint)jp).getSteps() && 
		  maxSteps >= ((TurtleMoveJoinPoint)jp).getSteps());
	}

}
