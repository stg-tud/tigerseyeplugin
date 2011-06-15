package de.tud.stg.tigerseye.examples.statemachine;

public class State {
  private String name;
  private boolean startState = false;
  private HashMap transitions;
  
  /**
   * This action will be executed whenever entering the state.
   */
  private ActionDelegate entryAction = null;

  /**
   * This action will be executed once in the state.
   */
  private ActionDelegate doAction = null;
  
  /**
   * This action will be executed whenever leaving the state.
   */
  private ActionDelegate exitAction = null 
	
  public State(String name) {
	this.name = name;
	transitions = new HashMap();
  }
  public State(String name, HashMap h) {
	this.name = name;
	this.transitions = new HashMap();
        this.transitions = h;
  }
		
  public State(String name, boolean startState) {
	this.name = name;
	this.startState = startState;
	this.transitions = new HashMap();
  }

  public State(String name, HashMap h, boolean startState) {
	this.name = name;
	this.startState = startState;
	this.transitions = new HashMap();
        transitions = h;
  }
		
  public String getName() { return name; }
	
  public void setName(String name) { this.name = name; }
  
  public boolean isStartState() { return startState; }

  public void setStartState(boolean startState) { this.startState = startState; }
	
  public void addTransition(Transition t) { transitions.put(t.event,t); }
  
  /**
   * Defines the state's event handling mechanism.
   * @param The event being received.
   * @return The transition for this event.
   */
  public Transition handleEvent(String event) {
	return transitions.get(event);
  }

  public void setEntry(ActionDelegate ad) {
		entryAction = ad;  
	  }
	  
  public void setDo(ActionDelegate ad) {
		doAction = ad;  
	  }
	  
  public void setExit(ActionDelegate ad) {
		exitAction = ad;  
	  }
	  
  
  public void performEntry() {
	if (entryAction == null) return;
	entryAction.perform();
  }
	  
  public void performDo() {
	if (doAction == null) return;
	doAction.perform();
  }
	  
  public void performExit() {
    if (exitAction == null) return;
	exitAction.perform();
  }
	  
  public String toString() {
	return "State:$name transitions={$transitions}"    
  }

    public HashMap getTransitions(){
      
        return transitions;
    }

    public void setTransitions(HashMap t){

        transitions = t;
    }
}