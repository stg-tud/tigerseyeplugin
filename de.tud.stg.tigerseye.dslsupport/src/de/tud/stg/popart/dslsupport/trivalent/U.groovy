/**
 * Copyright 2008, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.popart.dslsupport.trivalent;

public class U extends Trivalent {

	public U() {
		name = "U";
	}
	
	public boolean isTrue() { return false; }

	public boolean isFalse() { return false; }
	
	public boolean isUnknown() { return true; }
	
	public Trivalent and(Trivalent other) { 
		return new U();
	}

	public Trivalent or(Trivalent other) {
		return new U();
	}
	
	public Trivalent not() {
		return new U();
	}

}
