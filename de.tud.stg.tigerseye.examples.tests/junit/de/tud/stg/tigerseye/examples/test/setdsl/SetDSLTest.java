package de.tud.stg.tigerseye.examples.test.setdsl;

import static org.junit.Assert.*;

import org.junit.Test;

import utilities.DSLTransformationTestBase;

import de.tud.stg.tigerseye.examples.setdsl.SetDSL;

public class SetDSLTest extends DSLTransformationTestBase {

	
	@Test
	public void shouldTransformNewSet() throws Exception {
		String setdsl = "setdslnew";
		doTest(setdsl);
	}

	private void doTest(String setdsl) throws Exception {
		assertTransformedDSLEqualsExpectedUnchecked(setdsl, SetDSL.class);
	}
	
	@Test
	public void shouldTransformNewSetWithoutSemicolon() throws Exception {
		doTest("setdslnewnosemicolon");
	}
	
	@Test
	public void shouldTransformUnion() throws Exception {
		doTest("setdslunion");
	}
	
	@Test
	public void shouldTransformIntersection() throws Exception {
		doTest("setdslintersection");
	}
	
	@Test
	public void shouldTransformNewSetAndUnion() throws Exception {
		doTest("setdslnewandunion");
	}
	
	@Test
	public void shouldTransformSetVarAndUnion() throws Exception {
		doTest("setdslsetvarandunion");
	}
	
}
