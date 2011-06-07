/*jadclipse*/// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   StateMachineDSL.java

package de.tud.stg.tigerseye.transformingstatemachine;

import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.dslsupport.DSL;
import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;
import de.tud.stg.tigerseye.transformingstatemachine.fsm.ActionDelegate;
import de.tud.stg.tigerseye.transformingstatemachine.fsm.State;
import de.tud.stg.tigerseye.transformingstatemachine.fsm.StateMachine;
import groovy.lang.Closure;
import java.util.HashMap;

// Referenced classes of package de.tud.stg.popart.builder.test.statemachine:
//            ActionType, Transitions, OutputAction

public class StateMachineDSL
    implements DSL
{

    public StateMachineDSL()
    {
    }

    public Object eval(HashMap map, Closure cl)
    {
        cl.setDelegate(this);
        cl.setResolveStrategy(1);
        return cl.call();
    }

    @DSLMethod(prettyName = "machine__p0_lcub_p1_rcub")
    @PopartType(clazz=PopartOperationKeyword.class, breakpointPossible = 0)    
    public StateMachine machine__p0_lcub_p1_rcub(String machine, State states[])
    {
        StateMachine sm = new StateMachine(machine);
        currentStateMachine = sm;
        State astate[];
        int j = (astate = states).length;
        for(int i = 0; i < j; i++)
        {
            State s = astate[i];
            sm.addState(s, s.isStartState());
        }

        return sm;
    }

    @DSLMethod(prettyName = "state__p0_lcub_p1__p2_rcub")
    @PopartType(clazz=PopartOperationKeyword.class, breakpointPossible = 0)
    public State state__p0_lcub_p1__p2_rcub(String stateName, ActionType actions[], Transitions transitions)
    {
        State state = new State(stateName);
        if(actions.length > 0)
            state.setEntry(actions[0].getAction());
        currentState = state;
        transitions.transitions().call();
        return state;
    }

    @DSLMethod(prettyName = "start__state__p0_lcub_p1__p2_rcub")
    @PopartType(clazz=PopartOperationKeyword.class, breakpointPossible = 0)
    public State start__state__p0_lcub_p1__p2_rcub(String stateName, ActionType actions[], Transitions transitions)
    {
        State state = state__p0_lcub_p1__p2_rcub(stateName, actions, transitions);
        state.setStartState(true);
        return state;
    }

    @DSLMethod(prettyName = "entry_colon_p0_semi")
    @PopartType(clazz=PopartOperationKeyword.class, breakpointPossible = 0)
    public ActionType entry_colon_p0_semi(ActionDelegate a)
    {
        return new ActionType(a);
    }

    @DSLMethod(prettyName = "do_colon_p0_semi")
    @PopartType(clazz=PopartOperationKeyword.class, breakpointPossible = 0)
    public ActionType do_colon_p0_semi(ActionDelegate a)
    {
        return new ActionType(a);
    }

    @DSLMethod(prettyName = "exit_colon_p0_semi")
    @PopartType(clazz=PopartOperationKeyword.class, breakpointPossible = 0)
    public ActionType exit_colon_p0_semi(ActionDelegate a)
    {
        return new ActionType(a);
    }

    @DSLMethod(prettyName = "transitions_lcub_p0_rcub")
    @PopartType(clazz=PopartOperationKeyword.class, breakpointPossible = 0)
    public Transitions transitions_lcub_p0_rcub(Closure transitions)
    {
        return new Transitions(transitions);
    }

    @DSLMethod(prettyName = "rarr__p0__p1_semi")
    @PopartType(clazz=PopartOperationKeyword.class, breakpointPossible = 0)
    public void rarr__p0__p1_semi(String event, String stateName)
    {
        State from = currentState;
        from.addTransition(event, stateName);
    }

    @DSLMethod(prettyName = "p0_rarr_p1_semi")
    @PopartType(clazz=PopartOperationKeyword.class, breakpointPossible = 0)
    public void p0_rarr_p1_semi(String event, String stateName)
    {
        rarr__p0__p1_semi(event, stateName);
    }

    @DSLMethod(prettyName = "output__p0")
    @PopartType(clazz=PopartOperationKeyword.class, breakpointPossible = 0)
    public ActionDelegate output__p0(String str)
    {
        return new OutputAction(str);
    }

    private State currentState;
    private StateMachine currentStateMachine;
}


/*
	DECOMPILATION REPORT

	Decompiled from: /home/leo/wss/tigerseyemain/TigerseyeCoreTests/libs/stateMachineDSL.jar
	Total time: 73 ms
	Jad reported messages/errors:
The class file version is 50.0 (only 45.3, 46.0 and 47.0 are supported)
	Exit status: 0
	Caught exceptions:
*/