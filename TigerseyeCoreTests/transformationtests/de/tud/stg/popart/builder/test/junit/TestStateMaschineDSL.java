package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.popart.builder.test.TestUtils.test;

import org.junit.Test;

import de.tud.stg.popart.builder.test.statemachine.StateMachineDSL;

public class TestStateMaschineDSL {
	@Test
	public void testStateMachineDSL() {
		test("StateMachineDSL", StateMachineDSL.class);
	}
}
