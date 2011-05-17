package de.tud.stg.tigerseye.examples.logo;

import org.javalogo.Turtle;

class TracingLogo extends FunctionalLogo implements ITracingLogo {

	public TracingLogo() {
		super();
	}

	@Override
	public void show(String str) {
		println str;
	}

	@Override
	public void trace() {
		println "${this.turtle.name}: x=${Math.round(turtle.position.x)}, y=${Math.round(turtle.position.y)}, orientation=${(-turtle.heading+90)%360}, color=$turtle.penColor, ..."
	}

}
