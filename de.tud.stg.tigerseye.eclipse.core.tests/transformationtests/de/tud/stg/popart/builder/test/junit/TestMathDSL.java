package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.tigerseye.test.TransformationUtils.test;

import java.util.List;

import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.MathDSL;
import de.tud.stg.popart.dslsupport.DSL;
import de.tud.stg.tigerseye.test.TransformationUtils;

public class TestMathDSL {

	/*
	 * XXX(Leo Roos;Aug 16, 2011) The current expected does not expect a
	 * translation when Integer is a variable. See ticket #72.
	 */
	@Test
	public void testMathDSL() {
		test("MathDSL", mathClass());
	}

	private List<Class<? extends DSL>> mathClass() {
		return TransformationUtils.dslsList(MathDSL.class).toList();
	}

	@Test
	public void testMathDSLSimple() {
		test("MathDSLSimple", mathClass());
	}
}
