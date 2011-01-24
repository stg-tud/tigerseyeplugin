package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.popart.builder.test.TestUtils.test;

import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.ConditionalDSL;

public class ConditionalWithNestingDSL {
	@Test
	public void testConditionalDSL() {
		test("ConditionalWithNestingDSL", ConditionalDSL.class);
	}
}
