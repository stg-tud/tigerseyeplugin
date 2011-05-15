/**
 * Copyright 2008, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.popart.dslsupport.policy;

public abstract class Assertion {
	
	public abstract String toString();

	public Assertion and(Assertion right) {
		return new AllAssertion(this,right);
	}

	public Assertion or(Assertion right) {
		return new ExactlyOnceAssertion(this,right);		
	}
}
