package de.tud.stg.tigerseye.examples.test.simplesql;

import static org.junit.Assert.*;

import org.junit.Test;

import de.tud.stg.tigerseye.examples.simplesql.SimpleSqlDSL;
import de.tud.stg.tigerseye.examples.test.DSLTransformationTestBase;

public class SimpleSqlTest extends DSLTransformationTestBase {

	@Test
	public void testAsimpleexamplewithstring() throws Exception {
		assertTransformedDSLEqualsExpectedUnchecked("simplesql1", SimpleSqlDSL.class);
	}
	
}
