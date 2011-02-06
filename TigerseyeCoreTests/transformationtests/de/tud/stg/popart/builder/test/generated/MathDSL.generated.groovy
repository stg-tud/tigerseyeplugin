package de.tud.stg.popart.builder.test.generated;

import de.tud.stg.popart.builder.utils.DSLInvoker;
import de.tud.stg.popart.builder.test.dsls.MathDSL;
	 
new DSLInvoker (MathDSL.class).eval(){

	sum__p0(
[
10,
10
] as int[])
	
	int x = 10;
	∑ x, 10
	
	∑ Math.abs(-10), 10
	
	∑ new Integer(10), 10
	
	∑ buildString("hello"), 10
	∑ new String("hello"), 10
	String s = "hello";
	∑ s, 10
	
}