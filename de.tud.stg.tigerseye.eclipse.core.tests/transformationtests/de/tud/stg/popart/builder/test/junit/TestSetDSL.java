package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.tigerseye.test.TransformationUtils.test;

import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.SetDSL;

public class TestSetDSL {
	@Test
	public void testSetDSL() {
		test("SetDSL", SetDSL.class);
	}
}
