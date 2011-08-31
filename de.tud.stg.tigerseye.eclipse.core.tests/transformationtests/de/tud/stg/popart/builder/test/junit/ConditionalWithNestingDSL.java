package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.tigerseye.test.TransformationUtils.test;

import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.ConditionalDSL;
import de.tud.stg.tigerseye.test.TransformationUtils;

public class ConditionalWithNestingDSL {
	@Test
	public void testConditionalDSL() {
		test("ConditionalWithNestingDSL", TransformationUtils.dslSingle(ConditionalDSL.class));
	}
}
