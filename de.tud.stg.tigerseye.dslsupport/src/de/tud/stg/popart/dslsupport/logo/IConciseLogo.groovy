package de.tud.stg.popart.dslsupport.logo;

import de.tud.stg.popart.dslsupport.*;
import org.javalogo.*;
import java.awt.Color;

/**
 * This interface defines the logo toy language.
 */
interface IConciseLogo extends IExtendedLogo {
	/* Lietrals */

	/* Operations */
	void ts();	
	void fs();
	void cs();
	void ht();
	void st();
	void pu();
	void pd();
	void fd(int n);
	void bd(int n);
	void rt(int n);
	void lt(int n);
	
	/* Abstraction Operators */

}