package de.tud.stg.tigerseye.examples.logo;

import groovy.lang.Closure;
import java.util.HashMap;
import de.tud.stg.popart.dslsupport.Interpreter
import de.tud.stg.tigerseye.*;
import org.javalogo.*;
import java.awt.Color;

/**
 * This version of Logo simulates costs of drawing operation by slowing them down by 100 ms.
 */ 
public abstract class AbstractFunctionalLogo extends Interpreter implements IFunctionalLogo {
	protected HashMap<String,Closure> functionNamesToClosure;
		 
	public AbstractFunctionalLogo() {
		functionNamesToClosure = new HashMap<String,Closure>();
	}
	
	public AbstractFunctionalLogo(HashMap<String,Closure> enclosedFunctionNamesToClosure) {
		functionNamesToClosure = new HashMap<String,Closure>();
		putAllFunctionNamesToClosure(enclosedFunctionNamesToClosure);
	}
	
	public HashMap<String,Closure> getFunctionNamesToClosure() {
		return functionNamesToClosure;
	}
	
	public void setFunctionNamesToClosure(HashMap<String,Closure> map) {
		this.functionNamesToClosure = map;
	}
	
	public void putAllFunctionNamesToClosure(HashMap<String,Closure> enclosedFunctionNamesToClosure) {
		synchronized (enclosedFunctionNamesToClosure) {
			enclosedFunctionNamesToClosure.keySet().each { String key ->
		       Closure value = enclosedFunctionNamesToClosure.get(key).clone(); //must clone to allow adjusting the delegate
		       functionNamesToClosure.put(key,value);
		    }
		}
	}
	
	/* Literals */

	/* Operations */
	public Closure app(String name) {
		assert name != null;
		assert !name.isEmpty();
		Closure function = functionNamesToClosure.get(name)
		assert function != null;
		return function;
	}
	
	/* Abstraction Operators */
	public void fun(String name, Closure body) {
		synchronized (functionNamesToClosure) {
		  functionNamesToClosure.put(name, body);
		}
	}
	
	/* Inline Meta Level */
	private Object methodMissing(String name, Object args) {
        def function = app(name); 
		if (function != null) {
            function.call(*args);
		} else {
			throw new MissingMethodException(name, this.class, args);
	    }
	}
	
	public int getBlack() { return 0; }
	public int getBlue() { return 0; }
	public int getRed() { return 0; }
	public int getGreen() { return 0; }
	public int getYellow() { return 0; }
	public int getWhite() { return 0; }
	
	public abstract void forward(int n);
	public abstract void backward(int n);
	public abstract void right(int n);
	public abstract void left(int n);
	
	public void textscreen() {}
	public void fullscreen() {}
	public void home() {}
	public void clean() {}
	public void cleanscreen() {}
	public void hideturtle() {}
	public void showturtle() {}
	public void setpencolor(int n) {}
	public void penup() {}
	public void pendown() {}
	
	public void ts() {}
	public void fs() {}
	public void cs() {}
	public void ht() {}
	public void st() {}
	public void pu() {}
	public void pd() {}
	public void fd(int n) { forward(n); }
	public void bd(int n) { backward(n); }
	public void rt(int n) { right(n); }
	public void lt(int n) { left(n); }
	
	public void turtle(HashMap params, Closure coreography) {}	
}