/**
 * Copyright 2008, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.popart.dslsupport.modulo;

import de.tud.stg.popart.dslsupport.*;
import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * This class defines a DSL environment for working with List as mathematical sets policies.
 */
public class ModuloDSL implements DSL {

	def DEBUG = false; 
	
	def modulo;
	
	public ModuloDSL(int modulo) {
		this.modulo = modulo;
	}
	
	public static Interpreter getInterpreter(int modulo, HashMap context) {
		DSLCreator.getInterpreter(new ModuloDSL(modulo),context);
	}

	public Object eval(Closure cl) {
		cl.delegate = this;
		return cl.call();
	}
	
	/* Literals */
	
	/* Operations */
    
	public int add(int a, int b) {
		println "($a+$b)%$modulo"
		return (a+b)%modulo
	}
	
}

