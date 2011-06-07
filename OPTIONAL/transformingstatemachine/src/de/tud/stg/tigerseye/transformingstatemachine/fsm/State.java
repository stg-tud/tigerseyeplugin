/*jadclipse*/// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   State.java

package de.tud.stg.tigerseye.transformingstatemachine.fsm;

import java.util.HashMap;
import java.util.Map;

// Referenced classes of package de.tud.stg.popart.builder.test.statemachine.fsm:
//            ActionDelegate

public class State
{

    public boolean isStartState()
    {
        return startState;
    }

    public void setStartState(boolean startState)
    {
        this.startState = startState;
    }

    public State(String name)
    {
        transitions = new HashMap();
        this.name = name;
    }

    public void setEntry(ActionDelegate event)
    {
        entry = event;
    }

    public void setPerform(ActionDelegate event)
    {
        perform = entry;
    }

    public void setExit(ActionDelegate event)
    {
        exit = entry;
    }

    public String getName()
    {
        return name;
    }

    public void addTransition(String event, String to)
    {
        transitions.put(event, to);
    }

    Map transitions;
    ActionDelegate entry;
    ActionDelegate perform;
    ActionDelegate exit;
    private final String name;
    private boolean startState;
}


/*
	DECOMPILATION REPORT

	Decompiled from: /home/leo/wss/tigerseyemain/TigerseyeCoreTests/libs/stateMachineDSL.jar
	Total time: 56 ms
	Jad reported messages/errors:
The class file version is 50.0 (only 45.3, 46.0 and 47.0 are supported)
	Exit status: 0
	Caught exceptions:
*/