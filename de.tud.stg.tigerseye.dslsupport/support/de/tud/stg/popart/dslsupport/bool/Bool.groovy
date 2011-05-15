/**
 * Copyright 2008, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.popart.dslsupport.bool;

public abstract class Bool {

	String name;
	
	public static Bool valueOf(boolean value) {
		//return value? new T() : new F(); 
		return null;
	}
	
	public abstract boolean isTrue();

	public abstract boolean isFalse();
	
	public abstract Bool and(Bool other);

	public abstract Bool or(Bool other);
	
	public abstract Bool not();
	
	public String toString() {
		return name;
	}

}
