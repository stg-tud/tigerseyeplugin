package de.tud.stg.popart.builder.test.logo;

import de.tud.stg.popart.dslsupport.*;
import org.javalogo.*;
import java.awt.Color;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartLiteralKeyword;import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
/**
 * This class implements the logo toy language.
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
	
	public int getBlack() { return Color.BLACK.value; }
	
	public int getBlue() { return Color.BLUE.value; }
	
	public int getRed() { return Color.RED.value; }
	
	public int getGreen() { return Color.GREEN.value; }
	
	public int getYellow() { return Color.YELLOW.value; }
	
	public int getWhite() { return Color.WHITE.value; }

	/* Operations */
	
	public void textscreen()   { throw new IllegalStateException("DSL Operation has not been implemented.") }
	
	public void ts() { textscreen(); }	
	
	public void fullscreen() { throw new IllegalStateException("DSL Operation has not been implemented.") }
	
	public void fs() { fullscreen(); }
	
	public void home() { turtle.home();	}
	
	public void clean() { myTurtleGraphicsWindow.clear(); }
	
	public void cleanscreen() {	clean(); home(); }
	
	public void cs() { cleanscreen(); }
	
	
	public void hideturtle() { turtle.hide(); }
	
	public void ht() { hideturtle(); }
	
	public void showturtle() { turtle.show(); }
	
	public void st() { showturtle(); }
	
	
	public void setpencolor(int n) { turtle.setPenColor(new java.awt.Color(n)); }
	
	public void setpc(int n) { setpencolor(n); }
	
	public void penup() { turtle.penUp(); }
	
	public void pu() { penup(); }
	
	public void pendown() { turtle.penDown(); } 
	
	public void pd() { pendown(); }
		
	public void forward(int n) { turtle.forward(n);	}
	
	public void fd(int n) { forward(n); }	
	public void backward(int n) { turtle.backward(n); }
	
	public void bd(int n) { backward(n); }	
	public void right(int n) { turtle.right(n);	}
	
	public void rt(int n) { right(n);	}	
	public void left(int n) { turtle.left(n); }
	
	public void lt(int n) { left(n); }
	
	public static void main(String[] args) {
		new LogoDSL().eval(name:'Test') {
			
			forward 90
			right 90
			forward 90
		}
	}
}