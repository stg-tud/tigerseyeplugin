package de.tud.stg.tigerseye.examples.logo;

import de.tud.stg.tigerseye.*;
import org.javalogo.*;
import java.awt.Color;

/**
 * This version of Logo simulates costs of drawing operation by slowing them down by 100 ms.
 */
public class OptimizingLogo  extends ConcurrentLogo implements IConcurrentLogo {
	 
	def DEBUG = false;
	
	def count = 0;
	
	public OptimizingLogo() {
		super();
  		if (DEBUG) println "opt.new";
	}
	
	protected double headingToAngular(double heading) {
		int angular = (90 - heading) % 360;
		if (angular < 0) return angular + 360;
		return angular;
	}

	
	/* Literals */

	/* Operations */
	public Closure app(String name) {
		if (DEBUG) println "opt.app: app retrieves $name";
		Closure function = super.app(name);
		if (DEBUG) println "opt.app: super.app has retrieved "+function;
		assert function != null;


		
		Thread thread = Thread.currentThread();
		Turtle perThreadTurtle = threadToTurtle.get(thread); 
		double oldX = perThreadTurtle.position.x;
		double oldY = perThreadTurtle.position.y;
		double oldAngular = headingToAngular(perThreadTurtle.heading);
		//double oldAngular = perThreadTurtle.heading;
		if (DEBUG) println "opt.app: before calling function $name position was $oldX/$oldY:$oldAngular";
			
		def functionForWrapper = function.clone();
	    functionForWrapper.setDelegate(bodyDelegate);
	    functionForWrapper.setResolveStrategy(Closure.DELEGATE_FIRST);
		
		//calculating position after drawing the turtle
  		if (DEBUG) println "opt.app: wrapping function $name into closure";
        Closure wrappedFunction = { Object... args ->
            if (DEBUG) println ">>>opt.app:T${Thread.currentThread().getId()}: wrappedFunction start ";

			int deltaX = 0;
			int deltaY = 0;
			int deltaAngular = 0;
           
			Object result = null;
			
			if (DEBUG) println "opt.app: create delegate turtle structure that will paint the replace functionin a separate thread"
			int oldColor = perThreadTurtle.getPenColor().value;
            //create new concurrent turtle that draws the function
			result = super.turtle(name:("Opt#"+(name+(count++))),
					color:(oldColor), {
				    if (DEBUG) println ">>>opt.app:T${Thread.currentThread().getId()}: Paralellized Turtle start "
            	   
				    penup(); //we do not have to draw the lines until turtle is set to beginning position
				    
            	    //bring newturtle into position 
            	    if (DEBUG) println "\n\n"+"Opt#"+name+" goto y= "+(deltaY + oldY)
            	    if (deltaY + oldY > 0) { 
            	    	if (DEBUG) println "Opt#"+name+" y.fd "+(deltaY + oldY)
            	    	forward ((int)(deltaY + oldY))
            	    } else {
            	    	if (DEBUG) println "Opt#"+name+" y.bd "+(-1 * (deltaY + oldY))
            	    	backward ((int)(-1 * (deltaY + oldY)))
            	    }
            	    right 90

            	    if (DEBUG) println "Opt#"+name+" goto x= "+(deltaX + oldX)
            	    if (deltaX + oldX > 0) { 
            	    	if (DEBUG) println "Opt#"+name+" x.fd "+(deltaX + oldX)
            	    	forward ((int)(deltaX + oldX))
            	    } else {
            	    	if (DEBUG) println "Opt#"+name+" x.bd "+(-1 * (deltaX + oldX))
            	    	backward ((int)(-1 * (deltaX + oldX)))
            	    }
            	    left 90
            	    
            	    pendown(); //from the beginning position we have to draw again
            	    
            	    if (DEBUG) println "Opt#"+name+" set angle= "+(deltaAngular+oldAngular)            	    
            	    if (deltaAngular+oldAngular > 0) {
            	      if (DEBUG) println "Opt#"+name+" right "+(((deltaAngular+oldAngular)%360))          	    
             	      right ((int)((deltaAngular+oldAngular)%360))
            	    } else {
            	      if (DEBUG) println "Opt#"+name+" left "+(-1*((deltaAngular+oldAngular)%360))          	    
               	      left ((int)(-1*((deltaAngular+oldAngular)%360)))            	    	
            	    }

            	    if (DEBUG) println ">>>opt.app:T${Thread.currentThread().getId()}: Paralellized Turtle calls function $name"
            	    functionForWrapper.call(*args) 
            	    if (DEBUG) println ">>>opt.app:T${Thread.currentThread().getId()}: Paralellized Turtle called function $name"
            	    
            	    //home();
            	    if (DEBUG) println ">>>opt.app:T${Thread.currentThread().getId()}: Paralellized Turtle end "
            	}
            );			
            
            /*
            println "opt.app: forward turtle to the position at which the function ends"
			println "opt.app: Turtle has positions $perThreadTurtle.position.x/$perThreadTurtle.position.y:$perThreadTurtle.heading";
			println "opt.app: PotisionTracker tracked drawing function $deltaX/$deltaY:$deltaAngular";
			println "opt.app: To adjust position ${perThreadTurtle.position.x+deltaX}/${perThreadTurtle.position.y+deltaY}:${perThreadTurtle.heading+deltaAngular}";

			println "opt.app: Turtle before drawing function $oldX/$oldY:$oldAngular";
		    synchronized (myTurtleGraphicsWindow) {
		    	//move this turtle to position after drawing the function
			    println "opt.app: Turtle left $oldAngular";
			    perThreadTurtle.left((int)oldAngular); //turn to up right position
			    println "opt.app: Turtle forward $deltaY";
			    perThreadTurtle.forward((int)deltaY); 
			    println "opt.app: Turtle right 90";
			    perThreadTurtle.right(90); 
			    println "opt.app: Turtle forward $deltaX";
			    perThreadTurtle.forward((int)deltaX);
			
			    double newAngular = (270 + oldAngular + deltaAngular) % 360;
			    println "opt.app: Turtle new Angular $newAngular";
			    if (newAngular < 180) {
				    perThreadTurtle.right((int)newAngular);
			    } else {
				    perThreadTurtle.left((int)(360-newAngular));
			    }
		    }
		    		    
		    go();
		    		    
			println "opt.app: Turtle after drawing function $oldX/$oldY:$oldAngular";
			

			return result;
			*/
			
			if (DEBUG) println ">>>> GO start"
			goWithoutWaiting();
			if (DEBUG) println ">>>> GO end"
			if (DEBUG) println ">>>opt.app:T${Thread.currentThread().getId()}: wrappedFunction end ";
		    return result;
		}
        if (DEBUG) println "opt.app: wrapped function $name into closure "+wrappedFunction;

		
        if (DEBUG) println ">>>opt.app:T${Thread.currentThread().getId()}: setting delegate of wrappedFunction to $bodyDelegate";
//        wrappedFunction.delegate = bodyDelegate;
//        wrappedFunction.resolveStrategy = Closure.DELEGATE_FIRST;
        
		return wrappedFunction;
	}
	
	/* Abstraction Operators */
	public void fun(String name, Closure body) {
		if (DEBUG) println "opt.fun: defining function $name in "+body;
		functionNamesToClosure.put(name, body);
	}
	
}