package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.popart.builder.test.TestUtils.test;

import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.ForEachSyntaxDSL;

public class TestForEachSyntaxDSL {
	@Test
	public void testForEachSyntaxDSL() {
		test("ForEachSyntaxDSL", ForEachSyntaxDSL.class);
	}
}
