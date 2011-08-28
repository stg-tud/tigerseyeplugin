package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.tigerseye.test.TestUtils.test;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import utilities.LongrunningTest;
import utilities.LongrunningTestRule;

import de.tud.stg.popart.builder.test.dsls.BnfDSL;
import de.tud.stg.tigerseye.test.TestUtils;

public class TestBnfDSL {

	@Rule
	public LongrunningTestRule ptr = new LongrunningTestRule();

	@Test
	@LongrunningTest(24081)
	public void testBnfDSL() {
		test(false, "BnfDSLTest", TestUtils.dslSingle(BnfDSL.class));
	}
}
