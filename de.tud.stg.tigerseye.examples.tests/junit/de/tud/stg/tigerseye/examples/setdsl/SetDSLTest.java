package de.tud.stg.tigerseye.examples.setdsl;

import static org.junit.Assert.*;
import junit.framework.ComparisonFailure;

import org.junit.Ignore;
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
		assertTransformedDSLEqualsExpected(setdsl, SetDSL.class);
	}
	
	@Test(expected=ComparisonFailure.class)
	public void shouldTransformNewSetWithoutSemicolon() throws Exception {
		doTest("setdslnewnosemicolon");
	}
	
	@Test
	public void shouldTransformUnion() throws Exception {
		doTest("setdslunion");
	}
	
	@Test(expected=ComparisonFailure.class)
	public void shouldTransformIntersection() throws Exception {
		doTest("setdslintersection");
	}
	
	@Test(expected=ComparisonFailure.class)
	public void shouldTransformNewSetAndUnion() throws Exception {
		doTest("setdslnewandunion");
	}
	
	@Test(expected=ComparisonFailure.class)
	public void shouldTransformSetVarAndUnion() throws Exception {
		doTest("setdslsetvarandunion");
	}
	
}
