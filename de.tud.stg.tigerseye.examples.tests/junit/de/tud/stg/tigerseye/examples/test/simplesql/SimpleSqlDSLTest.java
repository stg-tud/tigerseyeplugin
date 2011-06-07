package de.tud.stg.tigerseye.examples.test.simplesql;

import org.junit.Ignore;
import org.junit.Test;

import de.tud.stg.tigerseye.examples.simplesql.SimpleSqlDSL;
import de.tud.stg.tigerseye.examples.test.DSLTransformationTestBase;

public class SimpleSqlDSLTest extends DSLTransformationTestBase {

	@Test
	public void shoudlTransformSelectFrom() throws Exception {
		assertTransformedDSLEqualsExpectedUnchecked("simplesqlfrom", SimpleSqlDSL.class);
	}
	
	
	@Ignore("Is acceptable here (recheck when ambiguites are resolved in antoher way)")
	@Test
	public void shouldTransformSelectFromWhere() throws Exception {
		assertTransformedDSLEqualsExpectedUnchecked("simplesqlfromwhere", SimpleSqlDSL.class);
	}
	
	@Test
	public void shouldTransformSelectFromWhereWithSemicolon() throws Exception {
		assertTransformedDSLEqualsExpectedUnchecked("simplesqlfromwherewithsemicolon", SimpleSqlDSL.class);
	}
	
}
