package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.tigerseye.test.TestUtils.test;

import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.MapDSL;
import de.tud.stg.popart.builder.test.dsls.MathDSL;
import de.tud.stg.popart.builder.test.dsls.SimpleSqlDSL;

public class SmallCombinedMapMathDSL {
	
	@Test
	public void testSmallCombinedDSL() {
		test("SmallCombinedDSL", MathDSL.class, SimpleSqlDSL.class);
	}
	
	@Test
	public void testSmallCombinedDSLwWhereAnd() {
		test("SmallCombinedDSLwWhereAnd", MathDSL.class, SimpleSqlDSL.class);
	}
	
	
}
