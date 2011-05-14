/**
 * Copyright 2008, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.popart.dslsupport;

import java.util.Map;

import de.tud.stg.popart.aspect.Aspect;

public interface ContextDSL extends DSL {
	void setContext(Map<String, Object> map);
	Map<String,Object> getContext();
	
	void setCurrentAspect(Aspect instance);
}
