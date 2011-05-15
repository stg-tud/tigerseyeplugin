package de.tud.stg.tigerseye.examples.logo;

import de.tud.stg.popart.dslsupport.*;

import org.javalogo.*;
import java.awt.Color;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartLiteralKeyword;import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
/**
 * This class implements the Logo toy language.
 */
public class LogoDSL extends Interpreter implements ILogoDSL {
	 
	def DEBUG = true; 
	
	private TurtleGraphicsWindow myTurtleGraphicsWindow;
	private Turtle turtle;
	
	public LogoDSL() {
		
	}
	
	public Object eval(HashMap map, Closure cl) {
		if (myTurtleGraphicsWindow == null) {
			myTurtleGraphicsWindow = new TurtleGraphicsWindow();
	        myTurtleGraphicsWindow.setTitle("TurtleDSL (based on JavaLogo) "); //Set the windows title
	        myTurtleGraphicsWindow.show(); //Display the window
	        turtle = new Turtle("Noname",java.awt.Color.BLACK); //Create a turtle
	        myTurtleGraphicsWindow.add(turtle); //Put bob in our window so bob has a place to draw
		}
		
		turtle.setName(map.name);
		cl.delegate = this;
		cl.resolveStrategy = Closure.DELEGATE_FIRST;
		cl.call();
	}
	
	/* Literals */
	@PopartType(clazz=PopartLiteralKeyword.class,breakpointPossible=0)
	public int getBlack() { return Color.BLACK.value; }
	@PopartType(clazz=PopartLiteralKeyword.class,breakpointPossible=0)
	public int getBlue() { return Color.BLUE.value; }
	@PopartType(clazz=PopartLiteralKeyword.class,breakpointPossible=0)
	public int getRed() { return Color.RED.value; }
	@PopartType(clazz=PopartLiteralKeyword.class,breakpointPossible=0)
	public int getGreen() { return Color.GREEN.value; }
	@PopartType(clazz=PopartLiteralKeyword.class,breakpointPossible=0)
	public int getYellow() { return Color.YELLOW.value; }
	@PopartType(clazz=PopartLiteralKeyword.class,breakpointPossible=0)
	public int getWhite() { return Color.WHITE.value; }

	/* Operations */
	@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void textscreen()   { throw new IllegalStateException("DSL Operation has not been implemented.") }
	@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void ts() { textscreen(); }	
	@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void fullscreen() { throw new IllegalStateException("DSL Operation has not been implemented.") }
	@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void fs() { fullscreen(); }
	@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void home() { turtle.home();	}
	@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void clean() { myTurtleGraphicsWindow.clear(); }
	@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void cleanscreen() {	clean(); home(); }
	@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void cs() { cleanscreen(); }
	
	@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void hideturtle() { turtle.hide(); }
	@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void ht() { hideturtle(); }
	@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void showturtle() { turtle.show(); }
	@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void st() { showturtle(); }
	
	@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void setpencolor(int n) { turtle.setPenColor(new java.awt.Color(n)); }
	@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void setpc(int n) { setpencolor(n); }
	@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void penup() { turtle.penUp(); }
	@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void pu() { penup(); }
	@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void pendown() { turtle.penDown(); } 
	@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void pd() { pendown(); }
		@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void forward(int n) { turtle.forward(n);	}
	@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void fd(int n) { forward(n); }	@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void backward(int n) { turtle.backward(n); }
	@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void bd(int n) { backward(n); }	@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void right(int n) { turtle.right(n);	}
	@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void rt(int n) { right(n);	}	@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void left(int n) { turtle.left(n); }
	@PopartType(clazz=PopartOperationKeyword.class,breakpointPossible=0)
	public void lt(int n) { left(n); }
	
}