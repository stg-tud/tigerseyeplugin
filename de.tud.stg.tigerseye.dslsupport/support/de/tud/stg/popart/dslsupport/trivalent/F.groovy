/**
 * Copyright 2008, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.popart.dslsupport.trivalent;

public class F extends Trivalent {

	public F() {
		name = "F";
	}
	
	public boolean isTrue() { return false; }

	public boolean isFalse() { return true; }
	
	public boolean isUnknown() { return false; }
	
	public Trivalent and(Trivalent other) { 
		if (other.isUnknown()) return new U(); 
		return new F();
	}

	public Trivalent or(Trivalent other) {
		if (other.isUnknown()) return new U(); 
		return Trivalent.valueOf(other.isTrue());;
	}
	
	public Trivalent not() {
		return new T();
	}

}
