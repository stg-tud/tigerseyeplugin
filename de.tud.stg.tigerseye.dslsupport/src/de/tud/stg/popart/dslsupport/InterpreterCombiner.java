/**
 * Copyright 2008, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.popart.dslsupport;

import groovy.lang.Closure;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;

import java.util.Set;
import java.util.Map;

import org.codehaus.groovy.runtime.InvokerHelper;

public class InterpreterCombiner extends Interpreter implements DSL {

	protected final boolean DEBUG = false;

	protected Set<DSL> dslDefinitions;
	protected Map<String, Object> context;

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
			if (!(dsl instanceof Interpreter)) continue; //this is necessary for compability to old DSL implementations that do not inherit the Interpreter class
			((Interpreter)dsl).setBodyDelegate(this);
		}
	}
	//-DINKELAKER-2010-02-16-END

	public InterpreterCombiner(DSL... dslDefinitions) {
		this(new java.util.HashSet<DSL>(java.util.Arrays.asList(dslDefinitions)), new java.util.HashMap<String,Object>());
	}

	public InterpreterCombiner(DSL dslDefinition, Map<String, Object> context) {
		this.dslDefinitions = new java.util.HashSet<DSL>();
		this.dslDefinitions.add(dslDefinition);
		this.context = context;
		setCombinerAsBodyDelegateOfAllInterpreters();
	} 

	public InterpreterCombiner(DSL dslDefinition1, DSL dslDefinition2, Map<String, Object> context) {
		this.dslDefinitions = new java.util.HashSet<DSL>();
		this.dslDefinitions.add(dslDefinition1);
		this.dslDefinitions.add(dslDefinition2);
		this.context = context;
		setCombinerAsBodyDelegateOfAllInterpreters();
	} 

	public InterpreterCombiner(Set<DSL> dslDefinitions) {
		this(dslDefinitions, new java.util.HashMap<String,Object>());
	}

	public InterpreterCombiner(Set<DSL> dslDefinitions, Map<String, Object> context) {
		this.dslDefinitions = dslDefinitions;
		this.context = context;
		setCombinerAsBodyDelegateOfAllInterpreters();
	} 

	public Object eval(Closure dslClosure) {
		dslClosure.setResolveStrategy(Closure.DELEGATE_FIRST); //Closure.DELEGATE_FIRST enables writing into properties defined by DSLs and prevent the creation of a local variable
		dslClosure.setDelegate(this);
		return dslClosure.call();
	}

	public Object methodMissing(String name, Object args) {
		//Necessary, since methodMissing is required to have (string,object) signature.
		for(DSL dsl : dslDefinitions){
			try{
				return InvokerHelper.invokeMethod(dsl, name, args);
			} catch(MissingMethodException e){
				//ignore exception to continue search
//			} catch (Exception e2) {
//				if (DEBUG) System.out.println(this.getClass().getName()+":: error in the implementation of a DSL operation (keyword="+name+",args="+String.valueOf(args)+")");
//				if (DEBUG) System.out.println("--- ERROR IN THE DSL IMPLEMENTATION ---");
//				if (DEBUG) e2.printStackTrace(System.out);
//				if (DEBUG) System.out.println("---------------------------------------");
//				throw new DSLException("Error in the implementation of a DSL operation (keyword=$name,args=$args,dsls=$dslDefinitions).",e2);
			}
		}
		throw new MissingMethodException(name, this.getClass(), (Object[]) args);
	}

	public void propertyMissing(String name, Object value) { 
		if (DEBUG) System.out.println("InterpreterCombiner: propertyMissing "+name+" "+value);
		// TODO should property write access also check the dslDefinitions properties?
		synchronized (this) {
			context.put(name, value);
		}
	}

	public Object propertyMissing(String name) { 
		if (DEBUG) System.out.println("InterpreterCombiner: propertyMissing "+name);
		for(DSL dsl : dslDefinitions){
			try{
				return InvokerHelper.getProperty(dsl, name);
			}catch(MissingPropertyException e){
				//ignore exception to continue search.
			}
		}
		if(context.containsKey(name)){
			return context.get(name);
		} else {
			throw new MissingPropertyException("The advice interpreter was unable to find the requested property.", name, this.getClass());
		}
	}

}
