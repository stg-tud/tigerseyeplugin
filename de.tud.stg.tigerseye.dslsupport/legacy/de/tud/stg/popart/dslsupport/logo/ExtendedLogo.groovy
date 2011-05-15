package de.tud.stg.popart.dslsupport.logo;

import de.tud.stg.popart.dslsupport.*;
import org.javalogo.*;
import java.awt.Color;

/**
 * This version of Logo defined all keyword of the Logo language.
 */
public class ExtendedLogo extends SimpleLogo 
                          implements IExtendedLogo {
	 
	public ExtendedLogo() {
		super();
	}
	 
	int getpencolor() { return turtle.getPenColor().value; }

	/* Literals */

	/* Operations */
	public void textscreen()   { throw new IllegalStateException("DSL Operation has not been implemented.") }
	public void fullscreen() { throw new IllegalStateException("DSL Operation has not been implemented.") }
	public void home() { turtle.home();	}
	public void clean() { myTurtleGraphicsWindow.clear(); }
	public void cleanscreen() {	clean(); home(); }
	
	public void hideturtle() { turtle.hide(); }
	public void showturtle() { turtle.show(); }
	
	public void setpencolor(int n) { turtle.setPenColor(new java.awt.Color(n)); }
	public void penup() { turtle.penUp(); }
	public void pendown() { turtle.penDown(); } 
	
	/* Abstraction Operators */
}