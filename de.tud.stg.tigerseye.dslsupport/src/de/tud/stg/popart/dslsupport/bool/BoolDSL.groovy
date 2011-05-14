/**
 * Copyright 2008, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.popart.dslsupport.bool;

import de.tud.stg.popart.dslsupport.*;
import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * This class defines a DSL environment for working with int .
 */
public class BoolDSL implements DSL {

	def DEBUG = false; 
		
	public static Interpreter getInterpreter(HashMap context) {
		DSLCreator.getInterpreter(new BoolDSL(),context)
	}
	
	/* Literals */
	
	
	/* Operations */
    
	
}

