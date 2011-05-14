/**
 * Copyright 2008, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.popart.dslsupport.policy;

import de.tud.stg.popart.dslsupport.*;

/**
 * This class defines a DSL environment for defining WS-SecurityPolicy policies.
 */
public class PolicyDSL implements DSL {

	def DEBUG = false; 
	 
	public static Interpreter getInterpreter(HashMap context) {
		return DSLCreator.getInterpreter(new PolicyDSL(),context)
	}
	
	/* Literals */
	
	/**	Implements keyword <code>SAML</code>. */
	public Assertion getSAML() {
    	/*
		if (DEBUG) println "SAML Assertion created";
    	return "<saml><assertion></assertion></saml>";
    	*/
    	return new SAMLAssertion();
    }
	
	/**	Implements keyword <code>Confidentiality</code>. */
	public Assertion getConfidentiality() {
    	return new ConfidentialityAssertion();
    }
	
	/* Operations */
	
	/**	Implements keyword <code>Integrity</code>. */
	public Assertion integrity(SAMLAssertion nestedAssertion) {
    	return new IntegrityAssertion(nestedAssertion);
    }
	
    /** Implements keyword <code>wrapExactlyOnce</code>. */
    public String wrapExactlyOnce(assertion) {
        return "<ExactlyOnce>$assertion</ExactlyOnce>";
    }
    
    /** Implements keyword <code>convertToPolicy</code>. */
    public String convertToPolicy(assertion) {
        return "<Policy>$assertion</Policy>";
    }
    
}
