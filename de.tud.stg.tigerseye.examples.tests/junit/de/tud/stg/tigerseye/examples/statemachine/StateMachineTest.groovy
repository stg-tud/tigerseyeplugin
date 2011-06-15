package de.tud.stg.tigerseye.examples.statemachine;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.tud.stg.popart.builder.test.statemachine.OutputAction;

public class StateMachineTest {

	StateMachine m

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void executeStateMachineSmokeTest() throws Exception {
		m = new StateMachine("WatchState")

		State reseted = new State("reseted", true)
		m.addState(reseted)
		def running = addStateToMachine("running")
		def paused = addStateToMachine("paused")
		def stopped = addStateToMachine("stopped")
		def off = addStateToMachine("off")

		addTrans reseted, running, "start"
		addTrans reseted, off, "switchOff"
		setEntry reseted, "resetTimer"

		addTrans running, paused, "split"
		addTrans running, stopped, "stop"
		running.setDo m.getAction( "loopRunning")
		setEntry running, "startTimer"

		addTrans paused, running, "unsplit"
		addTrans paused, stopped, "stop"
		setEntry paused, "pauseTimer"

		addTrans stopped, stopped, "reset"
		addTrans stopped, off, "switchOff"
		setEntry stopped, "stopTimer"

		//add off, off, "*"
		//setEntry off, "switchOff"

		m.DEBUG = true
		m.start ()
		m.receiveEvent "start"
		m.receiveEvent "split"
		m.receiveEvent "unsplit"
		m.receiveEvent "stop"
		m.receiveEvent "switchOff"
		//m.receiveEvent "toEnd"
	}

	private void setEntry(State s, String entry){
		def action = m.getAction (entry)
		s.setEntry action
	}

	private void addTrans(State from, State to, String event) {
		Transition t = new Transition( from, to, event)
		from.addTransition t
	}

	private State addStateToMachine(String name){
		State state = new State(name)
		m.addState(state)
		return state
	}
}
