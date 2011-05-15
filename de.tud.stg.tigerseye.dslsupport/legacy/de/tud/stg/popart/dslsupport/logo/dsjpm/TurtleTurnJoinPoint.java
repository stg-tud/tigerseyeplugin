package de.tud.stg.popart.dslsupport.logo.dsjpm;

import java.util.HashMap;

public abstract class TurtleTurnJoinPoint extends TurtleMotionJoinPoint {

	@SuppressWarnings("unchecked")
	public TurtleTurnJoinPoint(String location, HashMap context) {
		super(location, context);
		this.degrees = (Integer)context.get("degrees");
	}

	private int degrees;
	
	public int getDegrees() {
		return degrees;
	}

	public void setDegrees(int degrees) {
		this.degrees = degrees;
	}

}
