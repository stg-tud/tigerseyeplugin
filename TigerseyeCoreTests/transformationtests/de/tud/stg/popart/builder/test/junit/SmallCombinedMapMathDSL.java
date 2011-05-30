package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.tigerseye.test.TestUtils.test;

import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.MapDSL;
import de.tud.stg.popart.builder.test.dsls.MathDSL;

public class SmallCombinedMapMathDSL {
	@Test
	public void testSmallCombinedDSL() {
		test(true, "SmallCombinedDSL", MathDSL.class, MapDSL.class);
	}

	static void foo(Class<?> clazz) {

	}
}
