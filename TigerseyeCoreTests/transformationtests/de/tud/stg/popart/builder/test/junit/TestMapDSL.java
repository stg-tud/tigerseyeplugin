package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.popart.builder.test.TestUtils.test;

import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.MapDSL;

public class TestMapDSL {
	@Test
	public void testMapDSL() {
		test("MapDSL", MapDSL.class);
	}
}
