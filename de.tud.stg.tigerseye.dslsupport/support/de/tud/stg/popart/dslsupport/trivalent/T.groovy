/**
 * Copyright 2008, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.popart.dslsupport.trivalent;

public class T extends Trivalent {

	public T() {
		name = "T";
	}
	
	public boolean isTrue() { return true; }

	public boolean isFalse() { return false; }

	public boolean isUnknown() { return false; }
	
	public Trivalent and(Trivalent other) { 
		return Trivalent.valueOf(other.isTrue());
	}

	public Trivalent or(Trivalent other) {
		return new T();
	}
	
	public Trivalent not() {
		return new F();
	}

}
