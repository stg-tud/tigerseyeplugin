package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.tigerseye.test.TestUtils.test;

import org.junit.Ignore;
import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.BnfDSL;
import de.tud.stg.popart.builder.test.dsls.ConditionalDSL;
import de.tud.stg.popart.builder.test.dsls.ImprovedBnfDSL;

@Ignore("Too long, about 5 seconds")
public class TestBnfDSL {
	@Test
	public void testBnfDSL() {
		test(false, "BnfDSLTest", BnfDSL.class);
	}
}
