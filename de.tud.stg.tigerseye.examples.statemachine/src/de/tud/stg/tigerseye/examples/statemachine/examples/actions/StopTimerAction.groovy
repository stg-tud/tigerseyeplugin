package de.tud.stg.tigerseye.examples.statemachine.examples.actions;

import de.tud.stg.tigerseye.examples.statemachine.ActionDelegate
import java.util.Date;

class StopTimerAction extends ActionDelegate {


	public void perform() {
		System.out.println(this.getClass().toString()+":"+(new Date()));
	}

}