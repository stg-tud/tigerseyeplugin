package de.tud.stg.tigerseye.examples.logo;

import de.tud.stg.tigerseye.*;
import org.javalogo.*;
import java.awt.Color;

/**
 * This version of Logo defines shortcut keywords.
 */
public class ConciseLogo extends ExtendedLogo 
                         implements IConciseLogo {
	 
	public ConciseLogo() {
		super()
	}
	 
	/* Literals */

	/* Operations */
	public void fd(int n) { forward(n); }
	public void bd(int n) { backward(n); }
	public void rt(int n) { right(n);	}
	public void lt(int n) { left(n); }

	public void ts() { textscreen(); }	
	public void fs() { fullscreen(); }
	public void cs() { cleanscreen(); }
	
	public void ht() { hideturtle(); }
	public void st() { showturtle(); }
	
	public void setpc(int n) { setpencolor(n); }
	public void pu() { penup(); }
	public void pd() { pendown(); }
	
	/* Abstraction Operators */
}