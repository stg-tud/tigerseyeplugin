/**
 * Copyright 2008, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.popart.dslsupport;

import groovy.lang.Closure;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;

import java.util.List;
import java.util.Set;

import org.codehaus.groovy.runtime.InvokerHelper;

public class DSLInvoker {

	private final static boolean DEBUG = false; 
//	================================
//	Methods below are no longer used
//	================================
//	/**
//	 * Tries to invoke the first found keyword method in DSL definition list.
//	 */
//	public static Object invokeMethodOnDslDefs(List dslDefinitions, String name, Object args) {
//		if (DEBUG) println "DSLInvoker.invokeMethodOnDslDefs(): \t try to invoke "+name+" "+args+" on dslDefs";
//		Object result = null;
//		boolean found = false;
//		
//		Object dslDefinition = null;
//
//		Iterator it = dslDefinitions.iterator();
//        while (it.hasNext()) {
//			dslDefinition = it.next();
//			if (DEBUG) println "DSLInvoker.invokeMethodOnDslDefs(): \t try to invoke "+name+" "+args+" on "+dslDefinition;
//		    try {
//		    	//result = tryInvokeMethodOnDslDefinition(dslDefinition, name, args);
//		    	result = InvokerHelper.invokeMethod(dslDefinition,name, args);
//		    	found = true;
//		    } catch (MissingMethodException e1) {
//		    	//do nothing try next dsl
//		    }
//	    	if (found) break;
//		}
//		if (!found) {
//		    //if (DEBUG) println "DSLInvoker.invokeMethodOnDslDefs(): \t not found "+name+" "+args+" in dslDefs.";
//			//throw new MissingMethodException(name,DSLInvoker.class,args);
//			throw new RuntimeException(); //TODO Check exception
//		} else {
//  	        if (DEBUG) println "DSLInvoker.invokeMethodOnDslDefs(): \t found "+name+" "+args+" in "+dslDefinition.class;
//		    return result;
//		}
//	}
//
//	/**
//	 * Tries to invoke the keyword method on a DSL definition.
//	 */
//	private static Object tryInvokeMethodOnDslDefinition(Object dslDefinition, String name, Object args) {
//	    if (DEBUG) println "DSLInvoker.tryInvokeMethodOnDslDefinition(): \t try to invoke on "+name+" "+dslDefinition.class;
//	    try {
//	    	Object result = InvokerHelper.invokeMethod(dslDefinition,name, args);
//		    if (DEBUG) println "DSLInvoker.tryInvokeMethodOnDslDefinition(): \t found "+name+"="+result+" in "+dslDefinition.class;
//	        return result
//		} catch (MissingMethodException e1) {
//		    if (DEBUG) println "DSLInvoker.tryInvokeMethodOnDslDefinition(): \t not found "+name+" "+args+" on "+dslDefinition.class;
//			//do nothing an try next dsl definition
//		}
//	}
//	
//	/**
//	 * Tries to get the first found keyword literal in DSL definition list.
//	 */
//	public static Object getPropertyOnDslDefs(List dslDefinitions, String _property) {
//		Object result = null;
//		boolean found = false;
//		
//		Object dslDefinition = null;
//
//		Iterator it = dslDefinitions.iterator();
//		if (DEBUG && (!it.hasNext())) println "DSLInvoker: empty DSL list";
//        while (it.hasNext()) {
//			dslDefinition = it.next();
//			if (DEBUG) println "DSLInvoker.getPropertyOnDslDefs(): \t try to invoke "+_property+" on "+dslDefinition.class;
//		    try {
//		    	//result = tryGetPropertyOnDslDefinition(dslDefinition, _property);
//		    	result = InvokerHelper.getProperty(dslDefinition,_property);
//		    	found = true;
//		    } catch (MissingPropertyException e1) {
//				//do nothing an try next dsl definition
//		    }
//	    	if (found) break;
//		}
//		if (!found) {
//		    //if (DEBUG) println "DSLInvoker.invokeMethodOnDslDefs(): \t not found "+_property+" in dslDefs.";
//			//throw new MissingPropertyException(_property,DSLInvoker.class);
//			throw new RuntimeException();
//		} else {
//  	        if (DEBUG) println "DSLInvoker.getPropertyOnDslDefs(): \t found "+_property+" in "+dslDefinition.class;
//		    return result;
//		}
//	}
//		
//	/**
//	 * Tries to get the keyword literal in DSL definition.
//	 */
//	private static Object tryGetPropertyOnDslDefinition(Object dslDefinition, String _property){
//	    if (DEBUG) println "DSLInvoker.tryGetPropertyOnDslDefinition(): \t try getting "+_property+" from "+dslDefinition.class;
//		
//		try {
//			Object result = InvokerHelper.getProperty(dslDefinition,_property);
//		    if (DEBUG) println "DSLInvoker.tryGetPropertyOnDslDefinition(): \t found "+_property+"="+result+" in "+dslDefinition.class;
//		    return result;
//		} catch (MissingPropertyException e00) {
//			//do nothing an try next dsl definition
//		}
//	}
//	================================
//	Methods above are no longer used
//	================================
	
	/**
	 * Zips the result of all keyword methods in DSL definition list.
	 */
	public static Object zipMethodOnDslDefs(Set<DSL> dslDefinitions, Closure zipWithClosure, String name, Object args) {
		if (DEBUG) System.out.println("DSLInvoker.zipMethodOnDslDefs(): \t zips method results of "+name+" "+args+" on dslDefs");
		assert zipWithClosure.getMaximumNumberOfParameters() == dslDefinitions.size();

		List results = new java.util.LinkedList(); 
		

		for(Object dslDefinition : dslDefinitions){
			if (DEBUG) System.out.println("DSLInvoker.zipMethodOnDslDefs(): \t try to invoke "+name+" "+args+" on "+dslDefinition);
		    try {
		    	Object result = InvokerHelper.invokeMethod(dslDefinition,name, args);
		    	results.add(result);
		    } catch (MissingMethodException e1) {
		    	results.add(null);
		    }
		}
		
		if (DEBUG) System.out.println("DSLInvoker.zipMethodOnDslDefs(): \t results=$results");
        Object result = zipWithClosure.call(results.toArray());	
        if (DEBUG) System.out.println("DSLInvoker.zipMethodOnDslDefs(): \t final result=$result");
        return result;
	}
	
	/**
	 * Zips the value of all keyword literal in DSL definition list.
	 */
	public static Object zipPropertyOnDslDefs(Set<DSL> dslDefinitions, Closure zipWithClosure, String _property) {
		if (DEBUG) System.out.println("DSLInvoker.zipMethodOnDslDefs(): \t zips property values of "+_property+" on dslDefs");
		assert zipWithClosure.getMaximumNumberOfParameters() == dslDefinitions.size();
		
		List results = new java.util.LinkedList();
		
		for(Object dslDefinition : dslDefinitions){
			if (DEBUG) System.out.println("DSLInvoker.zipPropertyOnDslDefs(): \t try to invoke "+_property+" on "+dslDefinition.getClass());
		    try {
		    	Object result = InvokerHelper.getProperty(dslDefinition,_property);
		    	results.add(result);
		    } catch (MissingPropertyException e1) {
				results.add(null);
		    }
		}
  	    if (DEBUG) System.out.println("DSLInvoker.zipPropertyOnDslDefs(): \t results="+results);
  	    Object result = zipWithClosure.call(results.toArray());
  	  	if (DEBUG) System.out.println("DSLInvoker.zipPropertyOnDslDefs(): \t final result="+result);
		return result;
	}
		
}
