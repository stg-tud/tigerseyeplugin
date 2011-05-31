package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.tigerseye.test.TestUtils.test;

import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.MathDSL;

public class TestMathDSL {
	@Test
	public void testMathDSL() {
		test("MathDSL", MathDSL.class);
	}
	
	@Test
	public void testMathDSLSimple() {
		test("MathDSLSimple", MathDSL.class);
	}
}
