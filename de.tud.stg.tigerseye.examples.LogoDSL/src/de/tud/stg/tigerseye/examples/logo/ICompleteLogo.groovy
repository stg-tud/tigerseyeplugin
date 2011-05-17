package de.tud.stg.tigerseye.examples.logo;

import de.tud.stg.tigerseye.*;
import org.javalogo.*;
import java.awt.Color;

/**
 * This interface defines the logo toy language.
 */
interface ICompleteLogo extends IConciseLogo {
	/* Literals */

	/* Operations */

	/* Abstraction Operators */
    void repeat(int _times, Closure coreography);
}