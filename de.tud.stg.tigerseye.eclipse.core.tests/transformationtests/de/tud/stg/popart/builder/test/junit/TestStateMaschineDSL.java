package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.tigerseye.test.TestUtils.test;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import utilities.LongrunningTest;
import utilities.LongrunningTestRule;

import de.tud.stg.popart.builder.test.statemachine.StateMachineDSL;

@Ignore("API changed this class is no longer consistent")
public class TestStateMaschineDSL {
	
	
	@Test
	public void testStateMachineDSLTooLong() {
		test("StateMachineDSL", StateMachineDSL.class);
	}
	
	@Test
	public void testStateMachineDSLFaster() {
		test("StateMachineDSLFaster", StateMachineDSL.class);
	}
}
