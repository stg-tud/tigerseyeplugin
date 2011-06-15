package de.tud.stg.tigerseye.examples.setdsl;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.matchers.JUnitMatchers.*;


import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;

import de.tud.stg.tigerseye.examples.setdsl.SetDSL;

public class SetDSLSemanticsTest {
	
	private SetDSL sd;

	@Before
	public void bf(){
		sd = new SetDSL();
	}
	
	@Test
	public void shouldUnionSets() throws Exception {
		def a = [ 1, 2 ,4 ] as Set
		def b = [ 2 , 3 ,4] as Set
		def c = sd.union( a, b) 
		assertThat c, equalTo ([1,2,3,4] as Set)
	}
	
	@Test
	public void shouldIntersect() throws Exception {
		def a = [ 1, 2 ,4 ] as Set
		def b = [ 2 , 3 ,4] as Set
		def c = sd.intersection ( a, b)
		assertThat c, equalTo ([2,4] as Set)
	}

}
