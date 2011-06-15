package de.tud.stg.tigerseye.examples.statemachine

import de.tud.stg.tigerseye.examples.statemachine.examples.actions.LoopRunningAction
import de.tud.stg.tigerseye.examples.statemachine.examples.actions.PauseTimerAction
import de.tud.stg.tigerseye.examples.statemachine.examples.actions.ResetTimerAction
import de.tud.stg.tigerseye.examples.statemachine.examples.actions.StartTimerAction
import de.tud.stg.tigerseye.examples.statemachine.examples.actions.StopTimerAction
import de.tud.stg.tigerseye.examples.statemachine.examples.actions.SwitchOffAction



public class StateMachine {

	private DEBUG = false;

	private String name;
	private HashMap states;
	private State currentState;

	private HashMap cross_product = [];
	private HashMap actionBinding;

	public StateMachine(String name){
		this(name, getDefaultActionBinding());
	}

	private static HashMap getDefaultActionBinding(){
		HashMap aMap = new HashMap();
		aMap.put("loopRunning",new LoopRunningAction());
		aMap.put("pauseTimer",new PauseTimerAction());
		aMap.put("resetTimer",new ResetTimerAction());
		aMap.put("startTimer",new StartTimerAction());
		aMap.put("stopTimer",new StopTimerAction());
		aMap.put("switchOff",new SwitchOffAction());
		return aMap;
	}


	public StateMachine(String name, HashMap actionBinding) {
		this(name, actionBinding, new HashMap());
	}

	public StateMachine(String name, HashMap actionBinding, HashMap states) {
		this.name = name;
		this.states = states;
		this.actionBinding = actionBinding;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addState(State state) {
		states.put(state.name,state);
	}

	public State getState(String name) {
		return (State)states.get(name);
	}

	public State getCurrentState() {
		return currentState;
	}

	public status() {
		if (DEBUG) println "StateMachine.status: status of machine .";
		println "${this}";
	}

	public void start() {
		if (DEBUG) println "StateMachine.start: starting machine .";
		State startState = null;
		states.values().each { state ->
			//println "searching state $state"
			assert (state instanceof State)
			if (state.isStartState()) {
				startState = state;
			}
		}
		assert (startState != null);
		this.currentState = startState;

		currentState.performEntry();
		currentState.performDo();
		if (DEBUG) println "found startState ${this.currentState}";
	}

	public void receiveEvents(String[] events) {
		events.each { event -> receiveEvent (event) }
	}

	public void receiveEvent(String event) {
		if (DEBUG) println "StateMachine.input: receiving input '$event' machine .";

		currentState.performExit();

		Transition t = currentState.handleEvent(event);
		if (t == null) throw new RuntimeException("Undefined transition from state '$currentState' for event '$event'.")
		State nextState = t.fire();
		currentState = nextState; //take transition

		currentState.performEntry();
		currentState.performDo();
	}

	public String toString() {
		String result = "StateMachine:$name in $currentState\n"
		states.values().each { result += "  $it\n"; }
		return result;
	}

	public ActionDelegate getAction(String id) {
		return (ActionDelegate)actionBinding.get(id);
	}

	public HashMap getStates(){

		return states;
	}

	public Set getStates_keyset(){

		return states.keySet();
	}

	public Collection getStates_values(){
		return states.values();
	}

	public HashMap do_synchronized_product(State s1, State s2){ // To perform the cross product

		State from = null;
		State to = null;
		String to_name = null
		String from_name = null
		String e = null
		Transition t = null;

		int s1_dim = s1.getTransitions().size()
		int s2_dim = s2.getTransitions().size()

		if(s1_dim != 0 && s2_dim == 0){ // To verify if the second state doesn't have Transitions outgoing from it.

			s1.getTransitions().each(){ event, transition ->

				to_name = "(" + transition.getTo().getName() + "," + s2.getName() +")" // Perform the transition

				from_name = "(" + s1.getName()+ ","+ s2.getName() + ")" // Construct the resulting state's name

				if(from == null){
					from = new State(from_name)
				}

				to = new State(to_name)

				t = new Transition(from,to,event)

				from.addTransition(t)

			}

		}else if (s1_dim == 0 && s2_dim != 0){ // To verify if the first state doesn't have Transitions outgoing from it.

			s2.getTransitions().each(){ event, transition ->

				to_name = "(" + s1.getName() + "," + transition.getTo().getName() +")"

				from_name = "(" + s1.getName()+ ","+ s2.getName() + ")"

				if(from == null){
					from = new State(from_name)
				}

				to = new State(to_name)

				t = new Transition(from,to,event)

				from.addTransition(t)
			}

		}else if(s1_dim != 0 && s2_dim != 0){ // When both states have transitions outgoing from it

			s1.getTransitions().each(){ event, transition ->
				// We try to compare between transitions

				if (s2.getTransitions().getAt(event) == null){ // and determine if one state has transition outgoing

					to_name = "(" + transition.getTo().getName() + "," + s2.getName() +")" // using the same event as the other one. here the two

					from_name = "(" + s1.getName()+ ","+ s2.getName() + ")" // states doesn't have an outgoing transition

					if(from == null){                               // sharing the same event.
						from = new State(from_name)
					}

					to = new State(to_name)

					t = new Transition(from,to,event)

					from.addTransition(t)

				}else{ // To deal with the same event

					to_name = "(" + transition.getTo().getName() + "," + s2.getName() +")"

					from_name = "(" + s1.getName()+ ","+ s2.getName() + ")"

					if(from == null){
						from = new State(from_name)
					}

					to = new State(to_name)

					e = event + "_" // To distinguish between two events with the same syntax (we can't put the same key into a hashmap two times)

					t = new Transition(from,to,e)

					from.addTransition(t)

				}
			}

			s2.getTransitions().each(){ event, transition ->

				if (s1.getTransitions().getAt(event) == null){

					to_name = "(" + s1.getName() + "," + transition.getTo().getName() +")"

					from_name = "(" + s1.getName()+ ","+ s2.getName() + ")"

					if(from == null){
						from = new State(from_name)
					}

					to = new State(to_name)

					t = new Transition(from,to,event)

					from.addTransition(t)

				}else{ // to deal with the same event

					to_name = "(" + s1.getName() + "," + transition.getTo().getName() +")"

					from_name = "(" + s1.getName()+ ","+ s2.getName() + ")"

					if(from == null){
						from = new State(from_name)
					}

					to = new State(to_name)

					t = new Transition(from,to,event)

					from.addTransition(t)

				}
			}
		}


		if(from != null){

			return from.getTransitions();

		}else{
			return null
		}

	}

	public StateMachine multiply(StateMachine fsm2) { /* calculate synchronized product of this state machine with another state machine */

		if(states.size() != 0){

			states.each(){ state_name, state ->

				if(fsm2 != null && fsm2.getStates().size() != 0){

					fsm2.getStates().each(){ fsm2_state_name, fsm2_state ->

						String name = "("+state_name+","+fsm2_state_name+")";

						State s = null;

						if(state.isStartState() && fsm2_state.isStartState()){

							s = new State(name, do_synchronized_product(state, fsm2_state),true);

						}else{
							s = new State(name, do_synchronized_product(state, fsm2_state));
						}
						cross_product.put(name, s)

					}
				}
			}
		}

		cross_product.each(){println it}

		StateMachine st = new StateMachine("synchronized_product_fsm", actionBinding, cross_product);

		return st

	}

	public Boolean StateMachineisDeterministic(){

		int repeated = 0;
		String e = null, e1, e2;

		Boolean decision = true, b = false;

		states.values().each(){ state ->

			if(state.getTransitions() != null){

				state.getTransitions().values().each{ transition ->

					state.getTransitions().values().each(){

						if(transition.getFrom().getName().equals(it.getFrom().getName()) && !transition.getTo().getName().equals(it.getTo().getName())){

							if(transition.getEvent().endsWith("_") && !(it.getEvent().endsWith("_"))){
								e1 = it.getEvent() + "_"

								b = transition.getEvent().equals(e1);

							}else if(it.getEvent().endsWith("_") && !(transition.getEvent().endsWith("_"))){
								e2 = transition.getEvent() + "_"

								b = it.getEvent().equals(e2);
							}
						}
					}
				}
			}
		}

		if(b){
			return false
		}else{
			return true
		}
	}

}