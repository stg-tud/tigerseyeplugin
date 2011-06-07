/*jadclipse*/// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   Transitions.java

package de.tud.stg.tigerseye.transformingstatemachine;

import groovy.lang.Closure;

public class Transitions
{

    public Transitions(Closure c)
    {
        this.c = c;
    }

    public Closure transitions()
    {
        return c;
    }

    private Closure c;
}


/*
	DECOMPILATION REPORT

	Decompiled from: /home/leo/wss/tigerseyemain/TigerseyeCoreTests/libs/stateMachineDSL.jar
	Total time: 61 ms
	Jad reported messages/errors:
The class file version is 50.0 (only 45.3, 46.0 and 47.0 are supported)
	Exit status: 0
	Caught exceptions:
*/