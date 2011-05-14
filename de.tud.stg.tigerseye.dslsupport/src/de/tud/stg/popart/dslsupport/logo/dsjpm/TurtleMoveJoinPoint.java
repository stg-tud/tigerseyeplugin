package de.tud.stg.popart.dslsupport.logo.dsjpm;

import java.util.HashMap;

public class TurtleMoveJoinPoint extends TurtleMotionJoinPoint {

	@SuppressWarnings("unchecked")
	public TurtleMoveJoinPoint(String location, HashMap context) {
		super(location, context);
		this.steps = (Integer)context.get("steps");
	}

	private int steps;
	
	public int getSteps() {
		return steps;
	}

	public void setSteps(int steps) {
		this.steps = steps;
	}
}
