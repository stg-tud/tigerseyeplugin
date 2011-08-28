package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.tigerseye.test.TestUtils.test;

import org.junit.Ignore;
import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.MapDSL;

public class TestMapDSL {

//	@Ignore("movded to AmibguityFailuresTest")
//	@Test
//	public void testMapDSLMultipleEntries() {
//		test("MapDSLMultipleEntries", MapDSL.class);
//	}
//	
	
	@Test
	public void testMapDSLOneEntry() {
		test("MapDSLOneEntry", MapDSL.class);
	}
}
