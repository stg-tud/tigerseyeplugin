package de.tud.stg.popart.dslsupport.logo.dspcl;

import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.pointcuts.Pointcut;

import de.tud.stg.popart.dslsupport.logo.dsjpm.*;

public class TurtleTurningDegreesRangePCD extends Pointcut {

	public TurtleTurningDegreesRangePCD(int minDegrees, int maxDegrees) {
		super("pturning(minDegrees,maxDegrees)");
		this.minDegrees = minDegrees;
		this.maxDegrees = maxDegrees;
	}
	
	private int minDegrees;

	public int getMinDegrees() {
		return minDegrees;
	}

	public void setMinDegrees(int minDegrees) {
		this.minDegrees = minDegrees;
	}

	private int maxDegrees;

	public int getMaxDegrees() {
		return maxDegrees;
	}

	public void setMaxDegrees(int maxDegrees) {
		this.maxDegrees = maxDegrees;
	}

	@Override
	public boolean match(JoinPoint jp) {
		return 
		  ((jp instanceof LeftJoinPoint) ||
		  (jp instanceof RightJoinPoint)) &&
		  (minDegrees <= ((TurtleTurnJoinPoint)jp).getDegrees() && 
		   maxDegrees >= ((TurtleTurnJoinPoint)jp).getDegrees());
	}
}
