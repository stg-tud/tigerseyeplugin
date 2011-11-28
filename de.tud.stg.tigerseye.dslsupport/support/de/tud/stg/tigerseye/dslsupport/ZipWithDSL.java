/**
 * Copyright 2008, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.tigerseye.dslsupport;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.groovy.runtime.InvokerHelper;

import de.tud.stg.tigerseye.dslsupport.logger.DSLSupportLogger;

import groovy.lang.Closure;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;

public class ZipWithDSL extends InterpreterCombiner {
	
	private static final DSLSupportLogger logger = new DSLSupportLogger(ZipWithDSL.class);

	protected Closure<?> zipWithClosure;
	
	public ZipWithDSL(DSL dslDefinition, Closure<?> zipWithClosure, Map<String,Object> context) {
		super(dslDefinition,context);
		this.zipWithClosure = zipWithClosure;
	} 
	
	public ZipWithDSL(Set<DSL> dslDefinitions, Closure<?> zipWithClosure, Map<String,Object> context) {
		super(dslDefinitions,context);
		this.zipWithClosure = zipWithClosure;
	} 
	
	public ZipWithDSL(DSL dslDefinition1, DSL dslDefinition2, Closure<?> zipWithClosure, Map<String,Object> context) {
		super(dslDefinition1, dslDefinition2, context);
		this.zipWithClosure = zipWithClosure;
	} 
	
	@Override
	public Object eval(Closure<?> dslClosure) {
		dslClosure.setResolveStrategy(Closure.DELEGATE_FIRST); //Closure.DELEGATE_FIRST enables writing into properties defined by DSLs and prevent the creation of a local variable
		dslClosure.setDelegate(this);
		return dslClosure.call();
	}
	
    public Object methodMissing(String name, Object args) {
		logger.debug("ZipWithDSL: methodMissing "+name+" "+java.util.Arrays.toString((Object[])args));
		try {
		    return zipMethodOnDslDefs(dslDefinitions,zipWithClosure,name,args);
        } catch (MissingMethodException e1) {
        	logger.debug("ZipWithDSL: methodMissing "+name+" "+java.util.Arrays.toString((Object[])args));
            throw new MissingMethodException(name,this.getClass(),(Object[])args);
        }  catch (Exception e2) {
          logger.debug("ZipWithDSL: error in the implementation of a DSL operation (keyword="+name+",args="+java.util.Arrays.toString((Object[])args)+").");
          logger.debug("--- ERROR IN THE DSL IMPLEMENTATION ---");
          logger.debug(",",e2);
          logger.debug("---------------------------------------");
          throw new DSLException("Error in the implementation of a DSL operation (keyword=$name,args=$args).",e2);
        }
	}
    
    public void propertyMissing(String name, Object value) { 
		logger.debug("ZipWithDSL: propertyMissing $name $value");
    	context.put(name,value); 
    }
    
    public Object propertyMissing(String name) { 
		logger.debug("ZipWithDSL: propertyMissing "+name);
		try {
			return zipPropertyOnDslDefs(dslDefinitions,zipWithClosure,name);
		} catch (Exception e1) {
			if(context.containsKey(name)){
				return context.get(name);
			}else{
				throw new MissingPropertyException(name,this.getClass());
			}
		}
    }
    
    
	/**
	 * Zips the result of all keyword methods in DSL definition list.
	 * moved here from second DSLInvoker class since only used here
	 */
	public static Object zipMethodOnDslDefs(Set<DSL> dslDefinitions, Closure zipWithClosure, String name, Object args) {
		logger.debug("DSLInvoker.zipMethodOnDslDefs(): \t zips method results of "+name+" "+args+" on dslDefs");
		assert zipWithClosure.getMaximumNumberOfParameters() == dslDefinitions.size();

		List results = new java.util.LinkedList(); 
		

		for(Object dslDefinition : dslDefinitions){
			logger.debug("DSLInvoker.zipMethodOnDslDefs(): \t try to invoke "+name+" "+args+" on "+dslDefinition);
		    try {
		    	Object result = InvokerHelper.invokeMethod(dslDefinition,name, args);
		    	results.add(result);
		    } catch (MissingMethodException e1) {
		    	results.add(null);
		    }
		}
		
		logger.debug("DSLInvoker.zipMethodOnDslDefs(): \t results=$results");
        Object result = zipWithClosure.call(results.toArray());	
        logger.debug("DSLInvoker.zipMethodOnDslDefs(): \t final result=$result");
        return result;
	}
	
	/**
	 * Zips the value of all keyword literal in DSL definition list.
	 * moved here from second DSLInvoker class since only used here
	 */
	public static Object zipPropertyOnDslDefs(Set<DSL> dslDefinitions, Closure zipWithClosure, String _property) {
		logger.debug("DSLInvoker.zipMethodOnDslDefs(): \t zips property values of "+_property+" on dslDefs");
		assert zipWithClosure.getMaximumNumberOfParameters() == dslDefinitions.size();
		
		List results = new java.util.LinkedList();
		
		for(Object dslDefinition : dslDefinitions){
			logger.debug("DSLInvoker.zipPropertyOnDslDefs(): \t try to invoke "+_property+" on "+dslDefinition.getClass());
		    try {
		    	Object result = InvokerHelper.getProperty(dslDefinition,_property);
		    	results.add(result);
		    } catch (MissingPropertyException e1) {
				results.add(null);
		    }
		}
  	    logger.debug("DSLInvoker.zipPropertyOnDslDefs(): \t results="+results);
  	    Object result = zipWithClosure.call(results.toArray());
  	  	logger.debug("DSLInvoker.zipPropertyOnDslDefs(): \t final result="+result);
		return result;
	}
    
}
