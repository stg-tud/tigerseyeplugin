package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.tigerseye.test.TransformationUtils.test;

import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.PrefixDSL;

public class TestPrefixDSL {
	@Test
	public void testPrefixDSL() {
		test("PrefixDSL", PrefixDSL.class);
	}
}
