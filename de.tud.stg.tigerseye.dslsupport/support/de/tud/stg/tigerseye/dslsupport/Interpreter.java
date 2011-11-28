/**
 * Copyright 2009, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.tigerseye.dslsupport;

import java.util.HashMap;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.MetaClass;

/**
 * The Interpreter must be a GroovyObject so its missingMethod implementations
 * etc. will be invoked. So we inherit {@link GroovyObjectSupport}, which 
 * is the default implementations for GroovyObjects in Java.
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
	
	public Object eval(Closure<?> cl) {
		cl.setDelegate(bodyDelegate);
		return cl.call();
	}
	
	//XXX (Leo Roos; Jun 28, 2011): Added this method since it is the most often applied used case.
	public Object eval(@SuppressWarnings("rawtypes") HashMap map, Closure<?> cl) {
		cl.setDelegate(this);
		cl.setResolveStrategy(Closure.DELEGATE_FIRST);
		return cl.call();
	}
}
