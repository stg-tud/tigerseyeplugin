/**
 * Copyright 2008, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.popart.dslsupport.policy;

public class IntegrityAssertion extends Assertion {
	
	private SAMLAssertion nestedAssertion;
	
	public IntegrityAssertion(SAMLAssertion nestedAssertion) {
		this.nestedAssertion = nestedAssertion;
	}
	
	public String toString() {
		return "<Integrity>$nestedAssertion</Integrity>"
	}

}
