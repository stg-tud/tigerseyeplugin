package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.tigerseye.test.TestUtils.test;

import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.ConditionalDSL;
import de.tud.stg.tigerseye.test.TestUtils;

public class ConditionalWithNestingDSL {
	@Test
	public void testConditionalDSL() {
		test("ConditionalWithNestingDSL", TestUtils.dslSingle(ConditionalDSL.class));
	}
}
