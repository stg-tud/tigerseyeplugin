/**
 * Copyright 2008, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.popart.dslsupport.bool;

public class T extends Bool {

	public T() {
		name = "T";
	}
	
	public boolean isTrue() { return true; }

	public boolean isFalse() { return false; }
	
	public Bool and(Bool other) { 
		return Bool.valueOf(other.isTrue());
	}

	public Bool or(Bool other) {
		return new T();
	}
	
	public Bool not() {
		return new F();
	}

}
