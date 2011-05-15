/**
 * Copyright 2008, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.popart.dslsupport.trivalent;

public abstract class Trivalent {

	String name;
	
	public static Trivalent valueOf(boolean value) {
		return value? new T() : new F(); 
	}
	
	public abstract boolean isTrue();

	public abstract boolean isFalse();

	public abstract boolean isUnknown();
	
	public abstract Trivalent and(Trivalent other);

	public abstract Trivalent or(Trivalent other);
	
	public abstract Trivalent not();
	
	public String toString() {
		return name;
	}

}
