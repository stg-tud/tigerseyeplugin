package de.tud.stg.tigerseye.examples.logo;

import de.tud.stg.popart.dslsupport.DSL;
import de.tud.stg.popart.dslsupport.Interpreter
import de.tud.stg.tigerseye.*;

import org.javalogo.*;
import java.awt.Color;
import java.awt.geom.Point2D;


/**
 * This version of Logo simulates a logo program under and determines the position of the turtle after drawing.
 */
public class PositionTrackingLogo extends AbstractFunctionalLogo implements IFunctionalLogo {
	
	def DEBUG = false; 
	
	public PositionTrackingLogo() {
		super();
		if (DEBUG) println "postrack.new enclosed by null"
	}	
	
	Interpreter enclosingInterpreter = null;
	
	public PositionTrackingLogo(HashMap<String,Closure> enclosedFunctionNamesToClosure) {
		super(enclosedFunctionNamesToClosure);
	}
	
	double deltaX = 0.0;
	
	public double getDeltaX() {
		return deltaX;
	}
		
	double deltaY = 0.0;
	
	public double getDeltaY() {
		return deltaY;
	}
		
	int deltaAngular = 0;
	
	public int getDeltaAngular() {
		return deltaAngular;
	}
	
	public void setDeltaAngular(int deltaAngular) {
		this.deltaAngular = deltaAngular;
	}
	
	private Object callClosure(Object delegate, int resolveStrategy, Closure cl, Object... args) {
	    int savedResolvedStrategy = cl.getResolveStrategy();
	    Object savedDelegate = cl.getDelegate();
	    
		Closure closureToCall = cl.clone();
		closureToCall.setDelegate(delegate);
		closureToCall.setResolveStrategy(resolveStrategy);
		Object result = closureToCall.call(*args);

		cl.setDelegate(savedDelegate);
		cl.setResolveStrategy(savedResolvedStrategy);
		return result;
	}
		
	/* Literals */

	/* Operations */
	public void forward(int n) {  
		if (DEBUG) println "postrack.fw($n) before $deltaX / $deltaY"
		deltaX += Math.sin(deltaAngular * 2.0 * Math.PI/360.0)*n; 
		deltaY += Math.cos(deltaAngular * 2.0 * Math.PI/360.0)*n; 
		if (DEBUG) println "postrack.fw after $deltaX / $deltaY"
	}
	public void backward(int n) { 
		if (DEBUG) println "postrack.bd($n) before $deltaX / $deltaY"
		deltaX -= Math.sin(deltaAngular * 2.0 * Math.PI/360.0)*n; 
		deltaY -= Math.cos(deltaAngular * 2.0 * Math.PI/360.0)*n; 
		if (DEBUG) println "postrack.bd after $deltaX / $deltaY"
	}
	public void right(int n) { 
		if (DEBUG) println "postrack.rt($n) before $deltaAngular"
		deltaAngular = (deltaAngular + n)%360; 
		if (DEBUG) println "postrack.rt after $deltaAngular"
	}
	public void left(int n) { 
		if (DEBUG) println "postrack.lt($n) before $deltaAngular"
		deltaAngular = (deltaAngular + (360 - (n%360)))%360; 
		if (DEBUG) println "postrack.lt after $deltaAngular"
	}

	
	public Closure app(String name) {
		if (DEBUG) println "postrack.app($name) before $deltaX / $deltaY : $deltaAngular"
		assert name != null;
		assert !name.isEmpty();
		Closure function = functionNamesToClosure.get(name)
		assert function != null;
		
		def functionForWrapper = function.clone();

        Closure wrapClosure = { Object... args ->
			DSL positionTracker = new PositionTrackingLogo(functionNamesToClosure);
	        def result = callClosure(positionTracker,Closure.DELEGATE_ONLY,functionForWrapper,args);
	        deltaAngular = (deltaAngular + positionTracker.deltaAngular)%360;
	        //TODO Check the formular for deltaX and deltaY for correctness! (seems not to work for "testTrackingUnclosedShape") 
	        deltaX = deltaX +
                     (Math.sin(deltaAngular * 2.0 * Math.PI/360.0)*positionTracker.deltaX) +
	                 (Math.cos(deltaAngular * 2.0 * Math.PI/360.0)*positionTracker.deltaY);
	        deltaY = deltaY + 
                     (Math.cos(deltaAngular * 2.0 * Math.PI/360.0)*positionTracker.deltaX) +
                     (Math.sin(deltaAngular * 2.0 * Math.PI/360.0)*positionTracker.deltaY);
			return result; 
		}; //return null-object
		if (DEBUG) println "postrack.app($name) returning wrapped clousre"+wrapClosure;
		return wrapClosure
	}
	
	public void go() {
		//ignore because this command does not change the position
	}

	/* Abstraction Operators */
	public void fun(String name, Closure body) {
		if (DEBUG) println "postrack.fun($name) before"
		super.fun(name,body);
		if (DEBUG) println "postrack.fun($name) after"
	}
	
	public void repeat(int _times, Closure _choreography) {
		def choreography = _choreography.clone();
		
		if (DEBUG) println "postrack.repeat($_times) before $deltaX / $deltaY : $deltaAngular"
		DSL positionTracker = new PositionTrackingLogo(functionNamesToClosure)
		callClosure(positionTracker,Closure.DELEGATE_ONLY,choreography,null);
        
        deltaX += (positionTracker.deltaX * _times);
        deltaY += (positionTracker.deltaY * _times);
        deltaAngular = ((deltaAngular + positionTracker.deltaAngular *_times) / 360);
		if (DEBUG) println "postrack.repeat($_times) after $deltaX / $deltaY : $deltaAngular"
   	}
	
	public void turtle(HashMap params, Closure _choreography) {
		def choreography = _choreography.clone();
		if (DEBUG) println "postrack.turtle($params) before $deltaX / $deltaY : $deltaAngular"
		DSL positionTracker = new PositionTrackingLogo(functionNamesToClosure);
		callClosure(positionTracker,Closure.DELEGATE_ONLY,choreography,null);
		
		this.putAllFunctionNamesToClosure(positionTracker.getFunctionNamesToClosure());
        
        deltaX += positionTracker.deltaX;
        deltaY += positionTracker.deltaY;
        deltaAngular += positionTracker.deltaAngular % 360;
		if (DEBUG) println "postrack.turtle($params) after $deltaX / $deltaY : $deltaAngular"
	}
	
}