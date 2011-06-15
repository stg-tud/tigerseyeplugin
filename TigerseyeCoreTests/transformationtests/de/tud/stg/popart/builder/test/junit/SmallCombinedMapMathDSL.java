package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.tigerseye.test.TestUtils.test;

import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.MapDSL;
import de.tud.stg.popart.builder.test.dsls.MathDSL;

public class SmallCombinedMapMathDSL {
	
	//TODO prior version failed because of ambiguity in map dsl language was available
	// The input file used variables so that the concrete type of the variables was unknown 
	// and more than one transformation valid
	@Test
	public void testSmallCombinedDSL() {
		test(true, "SmallCombinedDSL", MathDSL.class, MapDSL.class);
	}
}
