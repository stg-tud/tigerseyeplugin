package com.example.fsm.examples.actions;

import com.example.fsm.ActionDelegate;
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