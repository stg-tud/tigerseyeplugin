package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.popart.builder.test.TestUtils.test;

import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.BnfDSL;
import de.tud.stg.popart.builder.test.dsls.ConditionalDSL;
import de.tud.stg.popart.builder.test.dsls.ImprovedBnfDSL;

public class TestBnfDSL {
	@Test
	public void testBnfDSL() {
		test(false, "BnfDSLTest", BnfDSL.class);
	}
}
