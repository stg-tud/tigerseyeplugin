package de.tud.stg.popart.builder.test.dsls;

import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.builder.test.statemachine.StateMachineDSL;
import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;

public class WordMachine extends StateMachineDSL {

	@DSLMethod(production = "when__p0__enter__p1_;")
	public void buildTransition(String event, String stateName) {
		this.p0_rarr_p1_semi(event, stateName);
	}

	@DSLMethod(production = "when__p0__end_;")
	public void buildEndTransition(String event) {
		this.p0_rarr_p1_semi(event, "$END");
	}
}
