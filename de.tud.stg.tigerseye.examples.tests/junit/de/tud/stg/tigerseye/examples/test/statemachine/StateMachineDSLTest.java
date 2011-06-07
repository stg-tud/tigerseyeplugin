package de.tud.stg.tigerseye.examples.test.statemachine;

import org.junit.Test;

import com.example.fsm.StateMachineDSL;


import de.tud.stg.tigerseye.examples.test.DSLTransformationTestBase;

public class StateMachineDSLTest extends DSLTransformationTestBase {

	@Test
	public void shoudlTransformSelectFrom() throws Exception {
		assertTransformedDSLEqualsExpectedUnchecked("StateMachineDSL", de.tud.stg.tigerseye.transformingstatemachine.StateMachineDSL.class);
	}	
	
	
	
}
