package de.tud.stg.tigerseye.examples.logo;

import de.tud.stg.tigerseye.*;
import org.javalogo.*;
import java.awt.Color;
import java.awt.EventQueue


/**
 * This version of Logo simulates costs of drawing operation by slowing them down by 100 ms.
 */
public class ConcurrentLogo  extends FunctionalLogo implements IConcurrentLogo {
	protected final static STEP_SIZE = 5;
	
	protected boolean startTurtleDrawingAfterDefinition = false; //immediately start drawing turtles after definition, if false user has to invoke go()
	
    protected List<Thread> threads; //threads that will be started with the next go instruction
	
	protected List<Thread> pastThreads; //threads that have been executed once
	
	protected List<Turtle> turtles;
    
    protected Map<Thread,Turtle> threadToTurtle = new HashMap<Thread,Turtle>();
		 
	public ConcurrentLogo() {
		super();
		threads = new LinkedList<Thread>();
		pastThreads = new LinkedList<Thread>();
		turtles = new LinkedList<Turtle>();
	}

	public ConcurrentLogo(boolean startTurtleDrawingAfterDefinition) {
		super();
		threads = new LinkedList<Thread>();
		pastThreads = new LinkedList<Thread>();
		turtles = new LinkedList<Turtle>();
		this.startTurtleDrawingAfterDefinition = startTurtleDrawingAfterDefinition;
	}
	
	public List<Thread> getPastThreads() {
		return pastThreads;
	}
	
	public List<Turtle> getTurtles() {
		return turtles;
	}
	
	public Thread getThread(Turtle turtle) {
		synchronized (threads) {
			Iterator<Thread> threads = threadToTurtle.keySet().iterator();
			while (threads.hasNext()) {
				Thread thread = threads.next();
				if (threadToTurtle.get(thread).equals(turtle)) {
					return thread;
				}
			}
		}
		return false;
	}
	
	public waitUntilAllThreadsAreFinished() {
        boolean isOneAlive = true; 
        while (isOneAlive) {
			isOneAlive = false; 
			Thread.sleep(10);
			if (DEBUG) println "wait"
			synchronized(pastThreads) {
				pastThreads.each { Thread t ->
			        if (t.isAlive()) isOneAlive = true;
			    }
			}
		} 
        Thread.sleep(10);
	}
		
	/* Literals */

	/* Operations */	
	public void forward(int n) {
		Thread thread = Thread.currentThread();
		Turtle perThreadTurtle = threadToTurtle.get(thread); 
		if (perThreadTurtle != null) {
			int complete = n / STEP_SIZE;
			int remainder = n % STEP_SIZE;
			
			//draw the complete 1st-9th/1st-10th fagments
			for (int i=0; i < complete; i++) {
  			    Thread.sleep(2*STEP_SIZE);
			    synchronized (myTurtleGraphicsWindow) {
  			        perThreadTurtle.forward(STEP_SIZE);
			    }
			}
			
			//draw the remaining 10-th fagments
			Thread.sleep(2*remainder);
			synchronized (myTurtleGraphicsWindow) {
  			    perThreadTurtle.forward(remainder);
			}	
		} else {
		    synchronized (myTurtleGraphicsWindow) {
			    super.forward(n);
		    }
		}
	}
	
	public void backward(int n) { 
		Thread thread = Thread.currentThread();
		Turtle perThreadTurtle = threadToTurtle.get(thread); 
		if (perThreadTurtle != null) {
			//draw lines in STEP-SIZE steps
			int complete = n / STEP_SIZE;
			int remainder = n % STEP_SIZE;
			
			//draw the complete 1st-9th/1st-10th fagments
			for (int i=0; i < complete; i++) {
  			    Thread.sleep(2*STEP_SIZE);
			    synchronized (myTurtleGraphicsWindow) {
  			        perThreadTurtle.backward(STEP_SIZE);
			    }
			}
			
			//draw the remaining 10-th fagments
			Thread.sleep(2*remainder);
			synchronized (myTurtleGraphicsWindow) {
  			    perThreadTurtle.backward(remainder);
			}			
		} else {
		    synchronized (myTurtleGraphicsWindow) {
			    super.backward(n);
		    }
		}
	}
	
	public void right(int n) { 
		Thread thread = Thread.currentThread();
		Turtle perThreadTurtle = threadToTurtle.get(thread); 
		if (perThreadTurtle != null) {
			Thread.sleep(1*n);
			synchronized (myTurtleGraphicsWindow) {
				perThreadTurtle.right(n);
			}
		} else {
		    synchronized (myTurtleGraphicsWindow) {
			    super.right(n);
		    }	    
		}
	}
	
	public void left(int n) { 
		Thread thread = Thread.currentThread();
		Turtle perThreadTurtle = threadToTurtle.get(thread); 
		if (perThreadTurtle != null) {
			Thread.sleep(1*n);
			synchronized (myTurtleGraphicsWindow) { 
				perThreadTurtle.left(n);
			}
		} else {
		    synchronized (myTurtleGraphicsWindow) {
			    super.left(n);
		    }
		}
	}
	
	public void penup() {
		Thread thread = Thread.currentThread();
		Turtle perThreadTurtle = threadToTurtle.get(thread); 
		if (perThreadTurtle != null) {
			synchronized (myTurtleGraphicsWindow) { 
				perThreadTurtle.penUp();
			}
		} else {
			synchronized (myTurtleGraphicsWindow) {
			    super.penUp();
		    }
		}		
	}
	
	public void pendown() {
		Thread thread = Thread.currentThread();
		Turtle perThreadTurtle = threadToTurtle.get(thread); 
		if (perThreadTurtle != null) {
			synchronized (myTurtleGraphicsWindow) { 
				perThreadTurtle.penDown();
			}
		} else {
			synchronized (myTurtleGraphicsWindow) {
			    super.penDown();
		    }
		}				
	}
	
	public void setpencolor(int n) {
		Thread thread = Thread.currentThread();
		Turtle perThreadTurtle = threadToTurtle.get(thread); 
		if (perThreadTurtle != null) {
			synchronized (myTurtleGraphicsWindow) { 
				perThreadTurtle.setPenColor(new Color(n));
			}
		} else {
			synchronized (myTurtleGraphicsWindow) {
			    super.setPenColor(new Color(n));
		    }
		}						
	}
	
	public void go() {
		List<Thread> threadsToRun = null;
		
		if (DEBUG) println "concurrent.go before"
        synchronized(threads) {
        	threadsToRun = new LinkedList<Thread>(threads);
        }
		
		threadsToRun.each { Thread t ->
		    if (DEBUG) println "Starting thread $t"
		    if (!t.isAlive()) t.start();
		}
	
//		threadsToRun.each { Thread t ->
//		    if (DEBUG) println "Joining thread $t"
//		    t.join(); 
//		}

		//wait for all threads to be finished (necessary to prevent concurrent modification of threads list)
        boolean isOneAlive = true; 
        while (isOneAlive) {
			isOneAlive = false; 
			Thread.currentThread().sleep(10);
			if (DEBUG) println "T"+Thread.currentThread().getId()+":concurrent.go wait for thread to finish"
        	threadsToRun.each { Thread t ->
		        if (t.isAlive()) isOneAlive = true;
		    }
		} 

        if (DEBUG) println "All threads have been finished"
        synchronized(pastThreads) {
  		    pastThreads.addAll(threadsToRun);

        }
        synchronized(threads) {
  		    threads.removeAll(threadsToRun); //only to be executed when all thread have finished
        }
        if (DEBUG) println "concurrent.go after"
	}
	
	
	protected void goWithoutWaiting() {
		List<Thread> threadsToRun = null;
		
		if (DEBUG) println "opt.goWithoutWaiting before"
        synchronized(threads) {
        	threadsToRun = new LinkedList<Thread>(threads);
        }
		
		threadsToRun.each { Thread t ->
		    if (DEBUG) println "Starting thread $t"
		    synchronized(t) {
		        if (!t.isAlive()) t.start();
		    }
		}
	
        synchronized(pastThreads) {
		    pastThreads.addAll(threadsToRun);
        }
		synchronized(threads) {
  		    threads.removeAll(threadsToRun); //only to be executed when all thread have finished
        }
        if (DEBUG) println "opt.goWithoutWaiting after"
	}
	
	/* Abstraction Operators */
	public void turtle(HashMap params, Closure choreography) {
	    if (DEBUG) println("Abstraction operator: turtle");
		
  	    String name = params.name;
	    if (name == null) {
		    name = "Noname";
	    }
		
	    int color = params.color;
	    if (color == null) {
		    color = Color.BLACK.value;
	    }
	  
        turtle = new Turtle(name,new Color(color)); //Create a turtle
        turtles.add(turtle);
        if (DEBUG) println "T"+Thread.currentThread().getId()+":concurrent.turtle add turtle to display"
	    synchronized (myTurtleGraphicsWindow) {
            myTurtleGraphicsWindow.add(turtle); //Put bob in our window so bob has a place to draw
	    }
        if (DEBUG) println "T"+Thread.currentThread().getId()+":concurrent.turtle added turtle to display"
      
        if (DEBUG) println "T"+Thread.currentThread().getId()+":concurrent.turtle has delegate $bodyDelegate"        
        choreography.delegate = bodyDelegate;
        
        Thread closureInThread = new Thread(choreography as Runnable);
        if (DEBUG) println "T"+Thread.currentThread().getId()+":concurrent.turtle add thread to list"
        synchronized(threads) {
            threads.add(closureInThread);
        }
        if (DEBUG) println "T"+Thread.currentThread().getId()+":concurrent.turtle added thread to list"
        threadToTurtle.put(closureInThread, turtle);
        
        if (startTurtleDrawingAfterDefinition) goWithoutWaiting();
        
        if (DEBUG) println "Joining Thread $closureInThread"
	}
	
	/* Inline Meta Level */

}