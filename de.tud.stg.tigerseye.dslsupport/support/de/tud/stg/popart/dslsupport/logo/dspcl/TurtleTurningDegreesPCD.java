package de.tud.stg.popart.dslsupport.logo.dspcl;

import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.pointcuts.Pointcut;

import de.tud.stg.popart.dslsupport.logo.dsjpm.*;

public class TurtleTurningDegreesPCD extends Pointcut {

	public TurtleTurningDegreesPCD(int degrees) {
		super("turning(degrees)");
		this.degrees = degrees;
	}
	
	private int degrees;

	public int getDegrees() {
		return degrees;
	}

	public void setDegrees(int degrees) {
		this.degrees = degrees;
	}

	@Override
	public boolean match(JoinPoint jp) {
		return 
		  ((jp instanceof LeftJoinPoint) ||
		  (jp instanceof RightJoinPoint)) &&
		  (degrees == ((TurtleTurnJoinPoint)jp).getDegrees());
	}
}
