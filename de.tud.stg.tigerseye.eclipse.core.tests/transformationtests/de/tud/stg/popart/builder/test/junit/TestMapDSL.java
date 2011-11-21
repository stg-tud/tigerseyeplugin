package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.tigerseye.test.TransformationUtils.test;
import junit.framework.ComparisonFailure;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import utilities.LongrunningTest;
import utilities.SystemPropertyRule;
import utilities.TodoTest;

import de.tud.stg.popart.builder.test.dsls.MapDSL;

public class TestMapDSL {
	
	@Rule
	public SystemPropertyRule spr = new SystemPropertyRule();

//	@Ignore("movded to AmibguityFailuresTest")
//	@Test
//	public void testMapDSLMultipleEntries() {
//		test("MapDSLMultipleEntries", MapDSL.class);
//	}
//	
	
	/*
	 * As SetDSL has Problems with Strings if the method expects an Object
	 * 
	 */
	@TodoTest
	@Test//(expected=ComparisonFailure.class)
	public void testMapDSLOneEntry() {
		test("MapDSLOneEntry", MapDSL.class);
	}
}
