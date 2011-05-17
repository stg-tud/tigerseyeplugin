
package de.tud.stg.tigerseye.examples.logo;

import de.tud.stg.tigerseye.*;
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