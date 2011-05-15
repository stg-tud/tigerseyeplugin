/**
 * Copyright 2008, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.popart.dslsupport;

import java.util.Map;
import java.util.Set;

import groovy.lang.Closure;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;

public class ZipWithDSL extends InterpreterCombiner {
	
	private final boolean DEBUG = false;

	protected Closure zipWithClosure;
	
	public ZipWithDSL(DSL dslDefinition, Closure zipWithClosure, Map<String,Object> context) {
		super(dslDefinition,context);
		this.zipWithClosure = zipWithClosure;
	} 
	
	public ZipWithDSL(Set<DSL> dslDefinitions, Closure zipWithClosure, Map<String,Object> context) {
		super(dslDefinitions,context);
		this.zipWithClosure = zipWithClosure;
	} 
	
	public ZipWithDSL(DSL dslDefinition1, DSL dslDefinition2, Closure zipWithClosure, Map<String,Object> context) {
		super(dslDefinition1, dslDefinition2, context);
		this.zipWithClosure = zipWithClosure;
	} 
	
	public Object eval(Closure dslClosure) {
		dslClosure.setResolveStrategy(Closure.DELEGATE_FIRST); //Closure.DELEGATE_FIRST enables writing into properties defined by DSLs and prevent the creation of a local variable
		dslClosure.setDelegate(this);
		return dslClosure.call();
	}
	
    public Object methodMissing(String name, Object args) {
		if (DEBUG) System.out.println("ZipWithDSL: methodMissing "+name+" "+java.util.Arrays.toString((Object[])args));
		try {
		    return DSLInvoker.zipMethodOnDslDefs(dslDefinitions,zipWithClosure,name,args);
        } catch (MissingMethodException e1) {
            if (DEBUG) System.out.println("ZipWithDSL: methodMissing "+name+" "+java.util.Arrays.toString((Object[])args));
            throw new MissingMethodException(name,this.getClass(),(Object[])args);
        }  catch (Exception e2) {
          if (DEBUG) System.out.println("ZipWithDSL: error in the implementation of a DSL operation (keyword="+name+",args="+java.util.Arrays.toString((Object[])args)+").");
          if (DEBUG) System.out.println("--- ERROR IN THE DSL IMPLEMENTATION ---");
          if (DEBUG) e2.printStackTrace(System.out);
          if (DEBUG) System.out.println("---------------------------------------");
          throw new DSLException("Error in the implementation of a DSL operation (keyword=$name,args=$args).",e2);
        }
	}
    
    public void propertyMissing(String name, Object value) { 
		if (DEBUG) System.out.println("ZipWithDSL: propertyMissing $name $value");
    	context.put(name,value); 
    }
    
    public Object propertyMissing(String name) { 
		if (DEBUG) System.out.println("ZipWithDSL: propertyMissing "+name);
		try {
			return DSLInvoker.zipPropertyOnDslDefs(dslDefinitions,zipWithClosure,name);
		} catch (Exception e1) {
			if(context.containsKey(name)){
				return context.get(name);
			}else{
				throw new MissingPropertyException(name,this.getClass());
			}
		}
    }
    
}
