package de.tud.stg.popart.dslsupport.logo;

import de.tud.stg.popart.dslsupport.*;
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