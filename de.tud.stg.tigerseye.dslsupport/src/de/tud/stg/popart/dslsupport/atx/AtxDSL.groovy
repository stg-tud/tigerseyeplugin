/**
 * Copyright 2008, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.popart.dslsupport.atx;

import de.tud.stg.popart.dslsupport.*;
import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * This class defines a DSL environment for working with control flow abstraction.
 */
public class AtxDSL {

	def DEBUG = false; 
	
	def dslsupport; 
	
	public static Interpreter getInterpreter(HashMap context) {
	    def instance = new AtxDSL();
		instance.dslsupport = DSLCreator.getInterpreter(instance,context)
		return instance.dslsupport;
	}
	
	private final String BEGIN_LIST_KEY = "beginList"; 
	private final String COMMIT_LIST_KEY = "commitList"; 
	private final String ABORT_LIST_KEY = "abortList"; 

	private List getClosureList(String key) {
		List list = dslsupport.context[key];
		if (list == null) {
			list = new LinkedList();
			dslsupport.context[key] = list;
		}
		return list;
	}

	private void callAllInClosureList(String key) {
	    getClosureList(key).each { cl ->
            assert cl instanceof Closure;
            cl.delegate = dslsupport
            cl.resolvbeStrategy = Closure.DELEGATE_FIRST;
            cl.call();
        }
    }

	
	/* Literals */	
	
	/* Operations */
    
	//CONTROL FLOW ABSTRATIONS	
	public void begin(Closure beginClosure) {
		getClosureList(BEGIN_LIST_KEY).add(beginClosure);
	}
	
	public void commit(Closure commitClosure) {
		getClosureList(COMMIT_LIST_KEY).add(commitClosure);
	}
	
	public void abort(Closure abortClosure) {
		getClosureList(ABORT_LIST_KEY).add(abortClosure);
	}
	
	public void performBegins() {
		callAllInClosureList(BEGIN_LIST_KEY);
	}

	public void performCommits() {
		callAllInClosureList(COMMIT_LIST_KEY);
	}

	public void performAborts() {
		callAllInClosureList(ABORT_LIST_KEY);
	}
	
	//ATX OPERATIONS
	public void alias(String name, Object obj) {
		println "\\--Define alias $name for $obj"
	}

	public void dep(Object from, String type, Object to) {
		println "\\--Dependency $from $type $to"
	}

	public void terminate(Object obj) {
		println "\\--Terminate $obj"
	}
}

