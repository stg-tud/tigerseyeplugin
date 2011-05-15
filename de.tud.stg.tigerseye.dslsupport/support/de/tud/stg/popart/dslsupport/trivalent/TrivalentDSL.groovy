/**
 * Copyright 2008, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.popart.dslsupport.trivalent;

import de.tud.stg.popart.dslsupport.*;
import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * This class defines a DSL environment for working with List as mathematical sets policies.
 */
public class TrivalentDSL implements DSL {

	def DEBUG = false; 
	 
	public static Interpreter getInterpreter(HashMap context) {
		DSLCreator.getInterpreter(new TrivalentDSL(),context)
	}
	
	/* Literals */
	
	public Trivalent T = new T();
	
	public Trivalent F = new F();
	
	public Trivalent U = new U();
	
	/* Operations */
    
	public void puts(String str, Trivalent tri) {
		println "$str $tri";
	}
	
	Trivalent not(Trivalent tri) {
		return tri.not();
	}
	
}

