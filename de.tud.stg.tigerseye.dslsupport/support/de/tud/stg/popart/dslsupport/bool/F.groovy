/**
 * Copyright 2008, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.popart.dslsupport.bool;

public class F extends Bool {

	public F() {
		name = "F";
	}
	
	public boolean isTrue() { return false; }

	public boolean isFalse() { return true; }
	
	public Bool and(Bool other) { 
		return new F();
	}

	public Bool or(Bool other) {
		return Bool.valueOf(other.isTrue());;
	}
	
	public Bool not() {
		return new T();
	}

}
