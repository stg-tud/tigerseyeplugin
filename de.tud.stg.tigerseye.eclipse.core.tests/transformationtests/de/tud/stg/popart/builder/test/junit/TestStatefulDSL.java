package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.tigerseye.test.TransformationUtils.test;

import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.StatefulDSL;

public class TestStatefulDSL {
	@Test
	public void testStatefulDSL() {
		test("StatefulDSL", StatefulDSL.class);
	}
}
