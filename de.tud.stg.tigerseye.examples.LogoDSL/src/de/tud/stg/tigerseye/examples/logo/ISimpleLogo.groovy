package de.tud.stg.tigerseye.examples.logo;

import de.tud.stg.popart.dslsupport.DSL;
import de.tud.stg.tigerseye.*;

import org.javalogo.*;
import java.awt.Color;

/**
 * This interface defines the logo toy language.
 */
interface ISimpleLogo extends DSL {
	/* Literals */
	int getBlack();
	int getBlue();
	int getRed();
	int getGreen();
	int getYellow();
	int getWhite();
		
	/* Operations */
	void forward(int n);
	void backward(int n);
	void right(int n);
	void left(int n);
	
	/* Abstraction Operators */
	void turtle(HashMap params, Closure coreography);	
}