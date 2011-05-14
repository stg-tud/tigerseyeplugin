/**
 * Copyright 2008, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.popart.dslsupport.policy;

public class AllAssertion extends Assertion {

	private Assertion left;

	private Assertion right;
	
	public AllAssertion(Assertion left, Assertion right) {
		this.left = left;
		this.right = right;
	}

	public String toString() {
		return "<All>$left$right</All>";
	}
}
