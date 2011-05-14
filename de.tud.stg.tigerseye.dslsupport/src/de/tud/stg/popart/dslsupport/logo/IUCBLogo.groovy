package de.tud.stg.popart.dslsupport.logo;

import de.tud.stg.popart.dslsupport.*;
import org.javalogo.*;
import java.awt.Color;

/**
 * This interface defines the logo toy language.
 */
interface IUCBLogo extends IConciseLogo {
	/* Literals */

	/* Operations */

	/* Abstraction Operators */
    void repeat(int _times, Closure coreography);
}