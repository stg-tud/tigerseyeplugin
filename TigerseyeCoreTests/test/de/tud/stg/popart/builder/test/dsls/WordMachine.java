package de.tud.stg.popart.builder.test.dsls;

import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.builder.test.statemachine.StateMachineDSL;
import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;

public class WordMachine extends StateMachineDSL {

	@DSLMethod(prettyName = "when__p0__enter__p1_;")
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 0)
	public void buildTransition(String event, String stateName) {
		this.p0_rarr_p1_semi(event, stateName);
	}

	@DSLMethod(prettyName = "when__p0__end_;")
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 0)
	public void buildEndTransition(String event) {
		this.p0_rarr_p1_semi(event, "$END");
	}
}
