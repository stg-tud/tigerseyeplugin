package de.tud.stg.tigerseye.examples.simplesql;

import org.junit.Ignore;
import org.junit.Test;

import utilities.DSLTransformationTestBase;

import de.tud.stg.tigerseye.examples.simplesql.SimpleSqlDSL;

public class SimpleSqlDSLTest extends DSLTransformationTestBase {

	@Test
	public void shoudlTransformSelectFrom() throws Exception {
		assertTransformedDSLEqualsExpected("simplesqlfrom", SimpleSqlDSL.class);
	}
	
	
	@Ignore("Is acceptable here (recheck when ambiguites are resolved in antoher way)")
	@Test
	public void shouldTransformSelectFromWhere() throws Exception {
		assertTransformedDSLEqualsExpected("simplesqlfromwhere", SimpleSqlDSL.class);
	}
	
	@Test
	public void shouldTransformSelectFromWhereWithSemicolon() throws Exception {
		assertTransformedDSLEqualsExpected("simplesqlfromwherewithsemicolon", SimpleSqlDSL.class);
	}
	
}
