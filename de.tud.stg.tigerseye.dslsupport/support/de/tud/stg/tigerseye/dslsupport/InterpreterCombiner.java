/**
 * Copyright 2008, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.tigerseye.dslsupport;

import groovy.lang.Closure;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.groovy.runtime.InvokerHelper;

import de.tud.stg.tigerseye.dslsupport.logger.DSLSupportLogger;

public class InterpreterCombiner extends Interpreter /*implements DSL // redundant*/ {
	
	private static final DSLSupportLogger logger = new DSLSupportLogger(InterpreterCombiner.class);
	
	protected Set<DSL> dslDefinitions;
	//Should be volatile considering JVMs out-of-order-writes  
	protected volatile Map<String, Object> context;

	// BEGIN:[ OR-2009-07-25: Thread-Safety Modification ]:::::::::::::::::::
	public void setContext(Map<String, Object> context) {
		synchronized (this) {
			this.context = context;
		}
	}

	public Map<String, Object> getContext() {
		synchronized (this) {
			return this.context;
		}
	}
	// END:::[ OR-2009-07-25: Thread-Safety Modification ]:::::::::::::::::::

	//-DINKELAKER-2010-02-16-BEGIN
	private void setCombinerAsBodyDelegateOfAllInterpreters() {
		for(DSL dsl : dslDefinitions){
			if (!(dsl instanceof Interpreter)) continue; //this is necessary for compatibility to old DSL implementations that do not inherit the Interpreter class
			((Interpreter)dsl).setBodyDelegate(this);
		}
	}
	//-DINKELAKER-2010-02-16-END

	public InterpreterCombiner(DSL... dslDefinitions) {
		this(asSet(dslDefinitions), new HashMap<String,Object>());
	}

	public InterpreterCombiner(DSL dslDefinition, Map<String, Object> context) {
		this(asSet(dslDefinition),context);
	} 

	public InterpreterCombiner(DSL dslDefinition1, DSL dslDefinition2, Map<String, Object> context) {
		this(asSet(dslDefinition1, dslDefinition2),context);
	} 

	public InterpreterCombiner(Set<DSL> dslDefinitions) {
		this(dslDefinitions, new java.util.HashMap<String,Object>());
	}

	/**
	 * Convenience constructor.
	 * 
	 * @param dsls
	 *            List of DSLs will be transformed into a Set
	 * @param context
	 */
	public InterpreterCombiner(List<DSL> dsls, Map<String, Object> context) {
		this(new HashSet<DSL>(dsls), context);
	}

	public InterpreterCombiner(Set<DSL> dslDefinitions, Map<String, Object> context) {
		this.dslDefinitions = dslDefinitions;
		this.context = context;
		setCombinerAsBodyDelegateOfAllInterpreters();
	} 

	private static HashSet<DSL> asSet(DSL... dsls) {
		HashSet<DSL> dslSet = new HashSet<DSL>();
		for (DSL dsl : dsls) {
			dslSet.add(dsl);
		}
		return dslSet;
	}

	@Override
	public Object eval(Closure<?> dslClosure) {
		dslClosure.setResolveStrategy(Closure.DELEGATE_FIRST); //Closure.DELEGATE_FIRST enables writing into properties defined by DSLs and prevent the creation of a local variable
		dslClosure.setDelegate(this);
		return dslClosure.call();
	}

	/**
	 * Searches through all DSLs and invokes the first suitable method.
	 * 
	 * @param name
	 *            of the method to invoke
	 * @param args
	 *            arguments of the method
	 * @return
	 */
	public Object methodMissing(String name, Object args) {
		// Necessary, since methodMissing is required to have (string,object)
		// signature.
		for (DSL dsl : dslDefinitions) {
			try {
				return InvokerHelper.invokeMethod(dsl, name, args);
			} catch (MissingMethodException e) {
				if (logger.isDebugEnabled()) {
					logger.debug("DSL " + dsl + " not applicable for method " + name + " args");
				}
			}
		}
		//No DSL defined method
		throw new MissingMethodException(name, this.getClass(), (Object[]) args);
	}

	public void propertyMissing(String name, Object value) { 
		logger.debug("InterpreterCombiner: propertyMissing "+name+" "+value);
		// TODO should property write access also check the dslDefinitions properties?
		synchronized (this) {
			context.put(name, value);
		}
	}

	public Object propertyMissing(String name) { 
		logger.debug("InterpreterCombiner: propertyMissing "+name);
		for(DSL dsl : dslDefinitions){
			try{
				return InvokerHelper.getProperty(dsl, name);
			}catch(MissingPropertyException e){
				//ignore exception to continue search.
			}
		}
		if(context.containsKey(name)){
			//XXX(Leo_Roos;Nov 18, 2011) Not Thread safe (inconsistent to other uses of context
			return context.get(name); 
		} else {
			throw new MissingPropertyException("The advice interpreter was unable to find the requested property.", name, this.getClass());
		}
	}

}
