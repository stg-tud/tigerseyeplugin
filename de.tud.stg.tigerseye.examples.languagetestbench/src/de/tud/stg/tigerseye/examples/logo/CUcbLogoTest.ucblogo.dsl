package de.tud.stg.tigerseye.examples.logo


/**
 * Tigerseye language: de.tud.stg.tigerseye.lang.logo.UCBLogo
 *
 * Declared keywords:
 *  void repeat(int, Closure)
 */


/**
 * The UCBLogo language extends inderectly Simple Logo and
 * introduces the reapeat keyword. So all functionality from Simple Logo
 * plus the repeat is available.
 */
ucblogo(name:'UcbLogoTest'){
	
	x = 10
	repeat(5 , {
		x = x + 1
		repeat( 50, {		
			fd x
			lt 10
		})
	})
	forward 350
}
