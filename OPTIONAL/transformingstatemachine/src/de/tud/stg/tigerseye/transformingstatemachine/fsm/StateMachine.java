/*jadclipse*/// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.

package de.tud.stg.tigerseye.transformingstatemachine.fsm;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

// Referenced classes of package de.tud.stg.popart.builder.test.statemachine.fsm:
//            State, ActionDelegate

public class StateMachine
{

    public StateMachine(String name)
    {
        this.name = name;
    }

    public State addState(String name, boolean isStartState)
    {
        State newState = new State(name);
        states.put(name, newState);
        if(isStartState)
            startState = newState;
        return newState;
    }

    public void addState(State state, boolean isStartState)
    {
        states.put(state.getName(), state);
        if(isStartState)
            startState = state;
    }

    public void start()
    {
        currentState = startState;
        running = true;
        if(currentState.entry != null)
            currentState.entry.performAction();
        if(currentState.perform != null)
            currentState.perform.performAction();
        while(running) ;
    }

    public void sendEvent(String string)
    {
        String n = (String)currentState.transitions.get(string);
        if(n != null && n.equals("$END"))
        {
            if(currentState.exit != null)
                currentState.exit.performAction();
            running = false;
            return;
        }
        State next = (State)states.get(n);
        if(next == null)
            System.out.println("No such transition available.");
        else
            changeState(next);
    }

    private void changeState(State next)
    {
        if(currentState.exit != null)
            currentState.exit.performAction();
        currentState = next;
        if(currentState.entry != null)
            currentState.entry.performAction();
        if(currentState.perform != null)
            currentState.perform.performAction();
    }

    private State startState;
    private State currentState;
    private final HashMap states = new HashMap();
    private volatile boolean running;
    private final String name;
}


/*
	DECOMPILATION REPORT

	Decompiled from: /home/leo/wss/tigerseyemain/TigerseyeCoreTests/libs/stateMachineDSL.jar
	Total time: 74 ms
	Jad reported messages/errors:
The class file version is 50.0 (only 45.3, 46.0 and 47.0 are supported)
	Exit status: 0
	Caught exceptions:
*/