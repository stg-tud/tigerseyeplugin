package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.tigerseye.test.TransformationUtils.test;

import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.ConditionalDSL;
import de.tud.stg.popart.builder.test.dsls.NumberRepresentationDSL;

public class TestNumberRepresentationDSL {
	@Test
	public void testNumberRepresentationDSL() {
		test(true, "NumberRepresentationDSL", NumberRepresentationDSL.class);
	}
}
