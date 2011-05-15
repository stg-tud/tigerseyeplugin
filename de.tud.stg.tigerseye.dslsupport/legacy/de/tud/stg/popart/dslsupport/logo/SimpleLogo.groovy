package de.tud.stg.popart.dslsupport.logo;

import de.tud.stg.popart.dslsupport.*;
import org.javalogo.*;
import java.awt.Color;

/**
 * This class implements a simplified version of the toy language Logo.
 */
public class SimpleLogo extends Interpreter 
                        implements ISimpleLogo {
	 
	def DEBUG = false; 
	
	protected volatile TurtleGraphicsWindow myTurtleGraphicsWindow;
	protected Turtle turtle;
	
	public SimpleLogo() {
		myTurtleGraphicsWindow = new TurtleGraphicsWindow();
        myTurtleGraphicsWindow.setTitle("Logo EDSL (based on JavaLogo) "); //Set the windows title
        myTurtleGraphicsWindow.show(); //Display the window
	}
	 
	public Object eval(HashMap map, Closure cl) {
		assert this.myTurtleGraphicsWindow != null; 
		turtle(map,cl);
	}
	
	public Turtle getTurtle() {
		return turtle;
	}
	
	public TurtleGraphicsWindow getCanvas() {
		return myTurtleGraphicsWindow;
	}
	
	/* Literals */
	public int getBlack() { return Color.BLACK.value; }
	public int getBlue() { return Color.BLUE.value; }
	public int getRed() { return Color.RED.value; }
	public int getGreen() { return Color.GREEN.value; }
	public int getYellow() { return Color.YELLOW.value; }
	public int getWhite() { return Color.WHITE.value; }

	/* Operations */
	public void forward(int n) { turtle.forward(n);	}
	public void backward(int n) { turtle.backward(n); }
	public void right(int n) {
		if (DEBUG) println("Turtle.right($n) before turning, headings is $turtle.heading");
		turtle.right(n);	
		if (DEBUG) println("Turtle.right($n) after turning,headings is $turtle.heading");
	}
	public void left(int n) { turtle.left(n); }

	/* Abstraction Operators */
	public void turtle(HashMap params, Closure choreography) {
	  if (DEBUG) println("Abstraction operator: turtle");
		
	  String name = params.name;
	  if (name == null) {
		  name = "Noname";
	  }
		
	  int color = params.color;
	  if (color == null) {
		  color = Color.BLACK.value;
	  }
	  
      turtle = new Turtle(name,new Color(color)); //Create a turtle
      myTurtleGraphicsWindow.add(turtle); //Put bob in our window so bob has a place to draw
      
      choreography.delegate = super.bodyDelegate;
      choreography.call();
	}
}