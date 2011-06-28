package de.tud.stg.tigerseye.examples.tinysql;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import utilities.DSLTransformationTestBase;

public class TinySQLTest extends DSLTransformationTestBase {

	@Test
	public void testSelectFrom() throws Exception {
		assertTransformedDSLEqualsExpected("tinysql", TinySQL.class);
	}

}
