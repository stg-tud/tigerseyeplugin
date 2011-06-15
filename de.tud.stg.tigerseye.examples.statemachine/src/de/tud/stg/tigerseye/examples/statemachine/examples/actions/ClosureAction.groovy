package de.tud.stg.tigerseye.examples.statemachine.examples.actions;


import de.tud.stg.tigerseye.examples.statemachine.ActionDelegate
import java.util.Date;

class ClosureAction extends ActionDelegate {
	
	Closure closure;

	public ClosureAction(Closure closure) {
		this.closure = closure;
	}
	
	public void perform() {
		closure.call();
	}

}