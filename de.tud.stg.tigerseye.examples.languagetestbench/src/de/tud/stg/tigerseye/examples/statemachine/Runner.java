package de.tud.stg.tigerseye.examples.statemachine;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import de.tud.stg.tigerseye.examples.statemachine.examples.actions.LoopRunningAction;
import de.tud.stg.tigerseye.examples.statemachine.examples.actions.PauseTimerAction;
import de.tud.stg.tigerseye.examples.statemachine.examples.actions.ResetTimerAction;
import de.tud.stg.tigerseye.examples.statemachine.examples.actions.StartTimerAction;
import de.tud.stg.tigerseye.examples.statemachine.examples.actions.StopTimerAction;
import de.tud.stg.tigerseye.examples.statemachine.examples.actions.SwitchOffAction;

public class Runner {

	public static void main(String[] args) throws Exception {

		StateMachineDSL.setDEBUG(true);
		
		HashMap aMap = new HashMap();
		aMap.put("loopRunning",new LoopRunningAction());
		aMap.put("pauseTimer",new PauseTimerAction());
		aMap.put("resetTimer",new ResetTimerAction());
		aMap.put("startTimer",new StartTimerAction());
		aMap.put("stopTimer",new StopTimerAction());
		aMap.put("switchOff",new SwitchOffAction());

		Binding aBinding = new Binding();
		aBinding.setVariable("myActionBinding", aMap);
		aBinding.setVariable("fsm", new Closure(null) {
			public Object doCall(Map<String,Object> params, Closure definition) {
				StateMachineDSL dsl = new StateMachineDSL();
				return dsl.fsm((HashMap<String,Object>)params,definition);
			}
		});
		
		Script script = null;
		GroovyShell gshell = new GroovyShell(aBinding);
		StateMachine watch = null; 
		
		try {
			
			script = gshell.parse(new InputStreamReader(Runner.class.getResourceAsStream("Watch.fsmexample")));

			Object definitionResult = script.run();
			watch = (StateMachine) definitionResult;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		
		watch.status();
		watch.start();
		watch.status();

		watch.status();
		watch.receiveEvent("start");
		watch.status();

		watch.status();
		watch.receiveEvent("split");
		watch.status();
		
	}
}
