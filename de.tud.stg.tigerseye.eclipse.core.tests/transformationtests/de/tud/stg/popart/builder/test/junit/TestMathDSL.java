package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.tigerseye.test.TransformationUtils.test;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import utilities.SystemPropertyRule;
import utilities.TodoTest;
import de.tud.stg.popart.builder.test.dsls.MathDSL;
import de.tud.stg.tigerseye.dslsupport.DSL;
import de.tud.stg.tigerseye.test.TransformationUtils;

public class TestMathDSL {

	
	@Rule
	public SystemPropertyRule spr = new SystemPropertyRule();
	
	/*
	 * Math in Math.abs disappears for some reason
	 */
	@TodoTest
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
