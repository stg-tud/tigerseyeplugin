/**
 * Copyright 2008, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.popart.dslsupport;

import java.util.Map;
import java.util.Set;

import de.tud.stg.popart.dslsupport.policy.PolicyDSL;
import de.tud.stg.popart.dslsupport.listsets.ListSetsDSL;
import de.tud.stg.popart.dslsupport.bool.BoolDSL;

public class DSLCreator {

	public static Interpreter getInterpreter(String name, Map<String, Object> context) {
		if (name.equals("policy")) return getInterpreter(new PolicyDSL(),context);
		if (name.equals("listsets")) return getInterpreter(new ListSetsDSL(),context);
		if (name.equals("bool")) return getInterpreter(new BoolDSL(),context);
		throw new RuntimeException("DSL "+name+" is not defined.");
	}
	
	public static Interpreter getInterpreter(DSL dslDefinition, Map<String, Object> context) {
		return new InterpreterCombiner(dslDefinition,context);
	}
	
	public static Interpreter getCombinedInterpreter(DSL dslDefinition1, DSL dslDefinition2, Map<String, Object> context) {
		Set<DSL> dslDefinitions = new java.util.HashSet<DSL>();
		dslDefinitions.add(dslDefinition1);
		dslDefinitions.add(dslDefinition2);
		return new InterpreterCombiner(dslDefinitions,context);
	}
	
	public static Interpreter getCombinedInterpreter(Set<DSL> dslDefinitions, Map<String, Object> context) {
		return new InterpreterCombiner(dslDefinitions,context);
	}
	
	
	
}
