package de.tud.stg.tigerseye.examples.logo


/**
 * Tigerseye language: de.tud.stg.tigerseye.lang.logo.ConciseLogo
 *
 * Declared keywords:
 *  void bd(int)
 *  void cs()
 *  void fd(int)
 *  void fs()
 *  void ht()
 *  void lt(int)
 *  void pd()
 *  void pu()
 *  void rt(int)
 *  void setpc(int)
 *  void st()
 *  void ts()
 */


/**
 * The Concise Logo language adds shortened names for the operations
 * already supported by Simple Logo.
 */

conciselogo(name:'BConciseLogo'){
	// This is the example from ASimpleLogoTest
	left 30
	forward 150
	left 120
	forward 150
	left 120
	forward 150	
	
	//A similar example in shortened form as to that in ASimpleLogoTest:
	lt 90
	bd 15
	rt 30
	bd 150
	rt 120
	bd 150
	rt 120
	bd 150
	
	
}
