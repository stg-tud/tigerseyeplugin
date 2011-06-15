package de.tud.stg.tigerseye.transformingstatemachine;

import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.dslsupport.DSL;
import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;
import de.tud.stg.tigerseye.eclipse.core.codegeneration.grammars.JavaSpecificGrammar;
import de.tud.stg.tigerseye.transformingstatemachine.fsm.ActionDelegate;
import de.tud.stg.tigerseye.transformingstatemachine.fsm.State;
import de.tud.stg.tigerseye.transformingstatemachine.fsm.StateMachine;
import groovy.lang.Closure;
import java.util.HashMap;

@de.tud.stg.popart.builder.core.annotations.DSL(hostLanguageRules=JavaSpecificGrammar.class,waterSupported=true,arrayDelimiter=" ")
public class StateMachineDSL
    implements DSL
{

    public StateMachineDSL()
    {
    }

    public Object eval(HashMap map, Closure cl)
    {
        cl.setDelegate(this);
        cl.setResolveStrategy(Closure.DELEGATE_FIRST);
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
