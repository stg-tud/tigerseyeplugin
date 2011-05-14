package de.tud.stg.popart.dslsupport.logo;

import de.tud.stg.popart.dslsupport.*;
import org.javalogo.*;
import java.awt.Color;

/**
 * This class implements the logo toy language.
 */
public class UCBLogo extends ConciseLogo implements IUCBLogo {
	 
	public UCBLogo() {
		super();
	}
	
	/* Literals */

	/* Operations */
	
	/* Abstraction Operators */
	void repeat(int _times, Closure choreography) {
    	choreography.delegate = bodyDelegate;
        _times.times {
            choreography.call();
        }
    }	
}