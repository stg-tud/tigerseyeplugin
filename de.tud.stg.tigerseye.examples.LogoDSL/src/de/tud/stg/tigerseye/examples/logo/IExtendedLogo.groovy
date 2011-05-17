package de.tud.stg.tigerseye.examples.logo;

import de.tud.stg.tigerseye.*;
import org.javalogo.*;
import java.awt.Color;

/**
 * This interface defines the logo toy language.
 */
interface IExtendedLogo extends ISimpleLogo {
	/* Literals */

	/* Operations */
	void textscreen();
	void fullscreen();
	void home();
	void clean();
	void cleanscreen();
	void hideturtle();
	void showturtle();
	void setpencolor(int n);
	void penup();
	void pendown(); 
	
	/* Abstraction Operators */
}