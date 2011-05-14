/**
 * Copyright 2008, Darmstadt University of Technology
 * GNU GENERAL PUBLIC LICENSE version 2.0
 * @author Tom Dinkelaker
 **/
package de.tud.stg.popart.dslsupport;

public class DSLException extends RuntimeException {
	
	public DSLException(String msg, Exception reason) {
		super(msg,reason);
	}

}
