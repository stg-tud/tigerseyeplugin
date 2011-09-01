package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.tigerseye.test.TransformationUtils.test;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import utilities.LongrunningTest;
import utilities.SystemPropertyRule;

import de.tud.stg.popart.builder.test.dsls.MapDSL;
import de.tud.stg.popart.builder.test.dsls.MathDSL;
import de.tud.stg.popart.builder.test.dsls.SimpleSqlDSL;
import de.tud.stg.tigerseye.test.TransformationUtils;

public class TestBigCombinedMapMathDSL {

	
	@Rule
	public SystemPropertyRule ptr = new SystemPropertyRule();

	@Test
	@LongrunningTest(186906)
	public void testBigConditionalDSL() {
		test(false, "BigCombinedDSL", TransformationUtils.dslsList(MathDSL.class)
				.add(MapDSL.class).add(SimpleSqlDSL.class).toList());
	}

}
