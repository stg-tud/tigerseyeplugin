package de.tud.stg.tigerseye.examples.statemachine


class Transition {
  private String event;
  private State from;
  private State to;
	
  public Transition(State from, State to, String event) {
	this.from = from;
	this.to = to;
	this.event = event;
  }
	
  public String getEvent() { return event; }
  public State getTo() { return to; }
  public State getFrom() { return from; }
  
  public State fire() { return to; }
  
  public String toString() {
	return "$from.name($event) -> $to.name"
  }
  
}