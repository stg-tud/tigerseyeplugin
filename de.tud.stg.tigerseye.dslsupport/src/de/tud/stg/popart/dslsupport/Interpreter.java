/**
 * Copyright 2009, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.popart.dslsupport;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.MetaClass;

/**
 * The Interpreter must be a GroovyObject so its missingMethod implementations
 * etc. will be invoked. So we inherit {@link GroovyObjectSupport}, which makes
 * provides the default implementations for GroovyObjects in Java.
 */
public class Interpreter extends GroovyObjectSupport implements DSL {
    protected Object bodyDelegate = this; 
	
    @Override
	public void setMetaClass(MetaClass mc) { 
	    super.setMetaClass(mc);
	    bodyDelegate = mc; 
	} 
	
	public void setBodyDelegate(Interpreter bd) { 
	    bodyDelegate = bd; 
	} 
	
	public Object eval(Closure cl) {
		cl.setDelegate(bodyDelegate);
		return cl.call();
	}
	
	public Interpreter add(Interpreter other) {
		return new InterpreterCombiner(this, other, new java.util.HashMap<String,Object>());
	}
}
