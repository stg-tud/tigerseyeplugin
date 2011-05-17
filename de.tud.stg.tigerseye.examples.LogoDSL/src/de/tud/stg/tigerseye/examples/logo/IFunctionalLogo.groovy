package de.tud.stg.tigerseye.examples.logo;

import de.tud.stg.tigerseye.*;
import org.javalogo.*;
import java.awt.Color;

/**
 * This interface defines the logo toy language.
 */
interface IFunctionalLogo extends IUCBLogo {
	/* Literals */

	/* Operations */
	Closure app(String name);

	/* Abstraction Operators */
	void fun(String name, Closure body);
}