package de.tud.stg.tigerseye.examples.logo;

import de.tud.stg.popart.dslsupport.*;
import org.javalogo.*;
import java.awt.Color;

/**
 * This interface defines the logo toy language.
 */
interface ILogoDSL extends DSL {
	int getBlack();
	int getBlue();
	int getRed();
	int getGreen();
	int getYellow();
	int getWhite();
		
	/* Operations */
	void textscreen();
	void ts();	
	void fullscreen();
	void fs();
	void home();
	void clean();
	void cleanscreen();
	void cs();
	void hideturtle();
	void ht();
	void showturtle();
	void st();
	void setpencolor(int n);
	void setpc(int n);
	void penup();
	void pu();
	void pendown(); 
	void pd();
	void forward(int n);
	void fd(int n);
	void backward(int n);
	void bd(int n);
	void right(int n);
	void rt(int n);
	void left(int n);
	void lt(int n);
}