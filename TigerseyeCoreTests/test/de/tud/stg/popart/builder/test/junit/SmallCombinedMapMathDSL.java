package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.popart.builder.test.TestUtils.test;

import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.MapDSL;
import de.tud.stg.popart.builder.test.dsls.MathDSL;

public class SmallCombinedMapMathDSL {
	@Test
	public void testSmallCombinedDSL() {
		test(false, "SmallCombinedDSL", MathDSL.class, MapDSL.class);
	}

	static void foo(Class<?> clazz) {

	}
}
