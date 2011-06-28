package de.tud.stg.tigerseye.examples.tinysql;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.DSLTransformationTestBase;

public class TinySQLTest extends DSLTransformationTestBase {
	
	
	@Test
	public void testSelectFrom() throws Exception {
		assertTransformedDSLEqualsExpected("tinysql", TinySQL.class);
	}
	
	@Test
	public void shouldEvaluateToSomething() throws Exception {
		redirectOutput new ByteArrayOutputStream();
		Closure dslProgram = {
			selectFrom(["NAME"] as String[],["PERSON"] as String[])
		}		
		List<Map> result = new TinySQL().eval dslProgram		
		assert result != null
	}

}
