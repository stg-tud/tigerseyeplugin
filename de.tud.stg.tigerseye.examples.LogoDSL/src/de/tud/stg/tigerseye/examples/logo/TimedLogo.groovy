package de.tud.stg.tigerseye.examples.logo;

import de.tud.stg.tigerseye.*;
import org.javalogo.*;
import java.awt.Color;

/**
 * This version of Logo simulates costs of drawing operation by slowing them down by 100 ms.
 */
public class TimedLogo  extends UCBLogo implements IUCBLogo {
	 
	public TimedLogo() {
		super();
	}
	
	/* Literals */

	/* Operations */
	public void forward(int n) { Thread.sleep(2*n); super.forward(n); }
	public void backward(int n) { Thread.sleep(2*n); super.backward(n); }
	public void right(int n) { Thread.sleep(1*n); super.right(n);	}
	public void left(int n) { Thread.sleep(1*n); super.left(n); }
	
	/* Abstraction Operators */
}