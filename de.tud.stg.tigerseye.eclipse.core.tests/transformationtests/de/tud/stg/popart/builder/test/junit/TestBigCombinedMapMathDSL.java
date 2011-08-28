package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.tigerseye.test.TestUtils.test;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import utilities.LongrunningTest;
import utilities.LongrunningTestRule;

import de.tud.stg.popart.builder.test.dsls.MapDSL;
import de.tud.stg.popart.builder.test.dsls.MathDSL;
import de.tud.stg.popart.builder.test.dsls.SimpleSqlDSL;
import de.tud.stg.tigerseye.test.TestUtils;

public class TestBigCombinedMapMathDSL {

	
	@Rule
	public LongrunningTestRule ptr = new LongrunningTestRule();

	@Test
	@LongrunningTest(193885)
	public void testBigConditionalDSL() {
		test(false, "BigCombinedDSL", TestUtils.dslsList(MathDSL.class)
				.add(MapDSL.class).add(SimpleSqlDSL.class).toList());
	}

}
