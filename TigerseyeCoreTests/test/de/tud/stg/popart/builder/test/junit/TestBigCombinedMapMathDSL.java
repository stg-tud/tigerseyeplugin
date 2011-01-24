package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.popart.builder.test.TestUtils.test;

import org.junit.Ignore;
import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.MapDSL;
import de.tud.stg.popart.builder.test.dsls.MathDSL;
import de.tud.stg.popart.builder.test.dsls.SimpleSqlDSL;

public class TestBigCombinedMapMathDSL {

	@Test
	public void testBigConditionalDSL() {
		test(false, "BigCombinedDSL", MathDSL.class, MapDSL.class, SimpleSqlDSL.class);
	}
}
