package de.tud.stg.tigerseye.examples.test.unitsdsl;

import org.junit.Ignore;
import org.junit.Test;

import de.tud.stg.tigerseye.example.dzoneunits.UnitsDSL;
import de.tud.stg.tigerseye.examples.test.DSLTransformationTestBase;

public class UnitsDSLTest extends DSLTransformationTestBase{

	

	@Ignore("18kg seems to be implementation problem. We seem to have exactly-one, and plus semantics, but not star semantics. #ticket 65")
	@Test
	public void testvariousunits() throws Exception {
		test("unitsdsl1");
	}

	private void test(String string) throws Exception {
		assertTransformedDSLEqualsExpectedUnchecked(string, UnitsDSL.class);
	}

	@Test
	public void testOnlyUnitsInIntegerFormAndWithWhitespace() throws Exception {
		test("unitsdslonlysingleinteger");
	}
	
}
