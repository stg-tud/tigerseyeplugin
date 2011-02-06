package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.popart.builder.test.TestUtils.test;

import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.SimpleSqlDSL;

public class TestSimpleSqlDSL {
	@Test
	public void testSimpleSQLDSL() {
		test("SimpleSqlDSL", SimpleSqlDSL.class);
	}
}
