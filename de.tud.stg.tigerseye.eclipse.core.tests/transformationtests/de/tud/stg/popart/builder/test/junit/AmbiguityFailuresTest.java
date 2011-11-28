package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.tigerseye.test.TransformationUtils.test;

import org.junit.ComparisonFailure;
import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.MapDSL;
import de.tud.stg.popart.builder.test.dsls.MathDSL;
import de.tud.stg.popart.builder.test.dsls.SimpleSqlDSL;

/**
 * A collection of tests that fail because of unresolved ambiguities.
 * <p>
 * <b>FIXME</b>: obviously these tests represent nonsatisfying functionality
 * which is expected to be implemented or corrected in the future.
 * <p>
 * The test usually fail because
 * <ul>
 * <li>(1)there is no prioritization for Integers before Strings
 * <li>(2)the wrong translation is applied. No heuristic exists to prioritize a
 * translation if more than one matches an expression (more concrete before less
 * concrete for example)
 * </ul>
 * 
 * @author Leo Roos
 */
public class AmbiguityFailuresTest {

	/**
	 * Fails supposedly because of (1)
	 */
	@Test(expected = ComparisonFailure.class)
	public void testMapDSLMultipleEntries() {
		test("MapDSLMultipleEntries", MapDSL.class);
	}

	// /*
	// * Fails supposedly because of (1)
	// */
	// @Test(expected = AssertionFailedError.class)
	// public void testMapDSLMultipleEntries() {
	// test("MapDSLMultipleEntries", MapDSL.class);
	// }

	@Test
	public void testSmallCombinedDSL() {
		test("SmallCombinedDSLAmbiguity", MathDSL.class, SimpleSqlDSL.class);
	}

}
