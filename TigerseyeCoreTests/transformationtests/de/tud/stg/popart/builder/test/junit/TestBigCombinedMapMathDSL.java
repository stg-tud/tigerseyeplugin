package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.tigerseye.test.TestUtils.test;

import org.junit.Ignore;
import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.MapDSL;
import de.tud.stg.popart.builder.test.dsls.MathDSL;
import de.tud.stg.popart.builder.test.dsls.SimpleSqlDSL;
import de.tud.stg.tigerseye.test.TestUtils;

@Ignore("Takes too long, about: 58 seconds")
public class TestBigCombinedMapMathDSL {

	@Test
	public void testBigConditionalDSL() {
		test(false, "BigCombinedDSL", TestUtils.dslsList(MathDSL.class)
				.add(MapDSL.class).add(SimpleSqlDSL.class).toList());
	}

}
