package de.tud.stg.tigerseye.examples.statemachine;

import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;
import de.tud.stg.tigerseye.examples.statemachine.examples.actions.ClosureAction
import groovy.lang.Closure;


//import de.tud.stg.tigerseye.examples.statemachine.examples.actions.ClosureAction;




public class StateMachineDSL {

	static boolean DEBUG = true;

	private StateMachine currentStateMachine;
	private State currentState;

	private HashMap actionBinding;

	public eval(HashMap params, Closure machineDefinition) {
		return fsm(params,machineDefinition);
	}

	public StateMachineDSL() {

	}
	/**
	 * Defines a new state machine.
	 * @param     params				The named parameters for the fsm abstraction operator.
	 * @param 	machineDefinition 	The body that contains the definition of the states and transitions.  
	 * @return The state machine.
	 */
	public StateMachine fsm(HashMap<String,Object> params, Closure machineDefinition) {
		if (DEBUG) println "MachineDSL.define: defining new machine $params.";
		assert (params.name != null);
		//	assert (params.actionBinding != null);

		StateMachine stateMachine = new StateMachine(params.name/*,params.actionBinding*/);

		setCurrentStateMachine(stateMachine);

		machineDefinition.delegate = this;
		machineDefinition.call();
		return stateMachine;
	}

	protected void setCurrentStateMachine(StateMachine stateMachine) {
		this.currentStateMachine = stateMachine;
	}
	
	/**
	 * Defines a new state for this machine. 
	 */
	public State state(HashMap params,Closure stateDefinition) {
		if (DEBUG) println "StateMachineDSL.state: defining state $params.";
		assert (params.name != null);
		boolean startState = false;
		if (params.type == null) {
			//nothing to do
		} else if (params.type.equals("start")) {
			startState = true;
		} else {
			throw new RuntimeException("Unknown state type $params.type");
		}

		currentState = currentStateMachine.getState(params.name);
		if (currentState == null) {
			currentState = new State(params.name,startState);
			if (DEBUG) println "StateMachineDSL.state: detailed definition create new state $currentState.";
			currentStateMachine.addState(currentState);
		} else {
			//update detailed state definition
			if (DEBUG) println "StateMachineDSL.state: resolve state $currentState.";
			currentState.setStartState(startState);
		}


		if (DEBUG) println "StateMachineDSL.state: interpreting state defintion $currentState.";
		//perform the state definition
		stateDefinition.delegate = this;
		stateDefinition.call();

		if (DEBUG) println "StateMachineDSL.state: add state to machine.";
		return currentState;
	}

	/**
	 * Defines a new block of transitions.  
	 */
	public void transitions(Closure transitionDefinitions) {
		if (DEBUG) println "StateMachineDSL.transition: defining transitions.";
		transitionDefinitions.delegate = this;
		transitionDefinitions.call();
	}

	/**
	 * Defines a new action for the current state that will be executed when entering this current state. 
	 */
	public void entry(String name) {
		if (DEBUG) println "StateMachineDSL.entry: defining entry $name.";
		ActionDelegate ad = currentStateMachine.getAction(name);
		currentState.setEntry(ad)
	}

	/**
	 * Defines a new action for the current state that will be executed when entering this current state. 
	 */
	public void entry(Closure closure) {
		if (DEBUG) println "StateMachineDSL.entry: defining entry with closure "+closure;
		ActionDelegate ad = new ClosureAction(closure);
		closure.delegate = this;
		currentState.setEntry(ad)
	}

	/**
	 * Defines a new action for the current state that will be executed when leaving this current state. 
	 */
	public void exit(String name) {
		if (DEBUG) println "StateMachineDSL.exit: defining exit $name.";
		ActionDelegate ad = currentStateMachine.getAction(name);
		assert (name != null) && (!name.equals("")) && (ad != null);
		currentState.setExit(ad);
	}

	/**
	 * Defines a new action for the current state that will be executed when leaving this current state. 
	 */
	public void exit(Closure closure) {
		if (DEBUG) println "StateMachineDSL.exit: defining exit with closure "+closure;
		ActionDelegate ad = new ClosureAction(closure);
		closure.delegate = this;
		currentState.setExit(ad);
	}

	/**
	 * Defines a new action for the current state that will be executed once in this current state. 
	 */
	public void perform(String name) {
		if (DEBUG) println "StateMachineDSL.perform: defining perform $name.";
		ActionDelegate ad = currentStateMachine.getAction(name);
		currentState.setDo(ad);
	}

	/**
	 * Defines a new action for the current state that will be executed once in this current state. 
	 */
	public void perform(Closure closure) {
		if (DEBUG) println "StateMachineDSL.perform: defining perform with closure "+closure;
		ActionDelegate ad = new ClosureAction(closure);
		closure.delegate = this;
		currentState.setDo(ad);
	}


	@DSLMethod(prettyName = "p0_rarr_p1")
	@PopartType(clazz=PopartOperationKeyword.class, breakpointPossible = 0)
	/*
	 * Example:
	 * <pre>
	 *     "start" → "running"
	 *      when (event:"start",enter:"running");
	 * </pre>
	 */
	public void when_event_enter(String event, String statename){
		when(event:event,enter:statename)
	}
	
	
	@DSLMethod(prettyName = "rarr_p0")
	@PopartType(clazz=PopartOperationKeyword.class, breakpointPossible = 0)
	/*
	 * Example:
	 * <pre>
	 *      → "off"
	 *      when (enter:"running");
	 * </pre>
	 */
	public void when_enter(String statename){
		when(enter:statename)
	}



	/**
	 * Defines a new action for the current state to another state. 
	 */
	public void when(HashMap params) {
		if (DEBUG) println "StateMachineDSL.when: defining transition when $params.";
		assert (params.enter != null);
		State from = currentState;
		State to = currentStateMachine.getState(params.enter);
		if (to == null) {
			if (DEBUG) println "StateMachineDSL.when: creating unresolved state definition.";
			to = new State(params.enter);
			if (DEBUG) println "StateMachineDSL.when: adding unresolved state.";
			currentStateMachine.addState(to);
		}

		Transition t;
		String e;

		if(from.getTransitions().getAt(params.event) == null){
			t = new Transition(from,to,params.event);
			from.addTransition(t);

		}else{
			e = params.event + "_"
			t = new Transition(from,to,e);
			from.addTransition(t);
		}
	}

	/**
	 * Implicit References
	 * @return
	 */
	public State getThisState() {
		return currentStateMachine.getCurrentState();
	}

	/**
	 * Implicit References
	 * @return
	 */
	public StateMachine getThisFsm() {
		return currentStateMachine;
	}

	/* Inline Meta Level */
	protected Object methodMissing(String name, Object args) {
		//println "${this} : functions=$functionNamesToClosure"
		if (environment.get(name) != null) {
			return apply(name).call(*args);
		} else {
			//throw new MissingMethodException(name, this.class, args);
			return super.methodMissing(name, args);
		}
	}

	public void propertyMissing(String name, Object value) {
		if (DEBUG) System.out.print("${this.getClass()}.propertyMissing('"+name+"')="+value);
		super.propertyMissing(name,value);
	}

	public Object propertyMissing(String name) {
		if (DEBUG) System.out.print("${this.getClass()}.propertyMissing('"+name+"')=");
		if (name.equals(currentStateMachine.getName())) {
			return currentStateMachine;
		} else {
			return currentStateMachine.getState(name);
		}
	}
}