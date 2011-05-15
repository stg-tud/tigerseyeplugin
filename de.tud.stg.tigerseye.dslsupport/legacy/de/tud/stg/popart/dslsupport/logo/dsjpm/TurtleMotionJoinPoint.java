package de.tud.stg.popart.dslsupport.logo.dsjpm;

import java.util.HashMap;

import org.javalogo.Turtle;

import de.tud.stg.popart.joinpoints.JoinPoint;

public abstract class TurtleMotionJoinPoint extends JoinPoint {

	@SuppressWarnings("unchecked")
	public TurtleMotionJoinPoint(String location, HashMap context) {
		super(location, context);
		if (context == null) throw new RuntimeException("context null");
		this.thisTurtle = (Turtle)context.get("thisTurtle");
		this.positionX = Math.round((float)thisTurtle.getPosition().x); 
		this.positionY = Math.round((float)thisTurtle.getPosition().y);
		this.argular = Math.round((float)thisTurtle.getHeading());
		this.color = thisTurtle.getPenColor().getRGB();
	}
	
	private int color;

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	private Turtle thisTurtle;

	public Turtle getTurtle() {
		return thisTurtle;
	}

	public void setTurtle(Turtle thisTurtle) {
		this.thisTurtle = thisTurtle;
	}
	
	private int positionX;

	public int getPositionX() {
		return positionX;
	}

	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}
	
	private int positionY;

	public int getPositionY() {
		return positionY;
	}

	public void setPositionY(int positionY) {
		this.positionY = positionY;
	}
	
	public int argular;

	public int getArgular() {
		return argular;
	}

	public void setArgular(int argular) {
		this.argular = argular;
	}
	
	
}
