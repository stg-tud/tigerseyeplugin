/*jadclipse*/// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.

package de.tud.stg.tigerseye.transformingstatemachine;

import de.tud.stg.tigerseye.transformingstatemachine.fsm.ActionDelegate;

public class ActionType
{

    public ActionType(ActionDelegate action)
    {
        this.action = action;
    }

    public ActionDelegate getAction()
    {
        return action;
    }

    private ActionDelegate action;
}


/*
	DECOMPILATION REPORT

	Decompiled from: /home/leo/wss/tigerseyemain/TigerseyeCoreTests/libs/stateMachineDSL.jar
	Total time: 65 ms
	Jad reported messages/errors:
The class file version is 50.0 (only 45.3, 46.0 and 47.0 are supported)
	Exit status: 0
	Caught exceptions:
*/