/*jadclipse*/// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.

package de.tud.stg.tigerseye.transformingstatemachine;

import de.tud.stg.tigerseye.transformingstatemachine.fsm.ActionDelegate;

import java.io.PrintStream;

public class OutputAction
    implements ActionDelegate
{

    public OutputAction(String s)
    {
        this.s = s;
    }

    public void performAction()
    {
        System.out.println(s);
    }

    private String s;
}


/*
	DECOMPILATION REPORT

	Decompiled from: /home/leo/wss/tigerseyemain/TigerseyeCoreTests/libs/stateMachineDSL.jar
	Total time: 71 ms
	Jad reported messages/errors:
The class file version is 50.0 (only 45.3, 46.0 and 47.0 are supported)
	Exit status: 0
	Caught exceptions:
*/