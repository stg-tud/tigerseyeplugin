package com.example.fsm;

import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test

import com.example.fsm.examples.actions.LoopRunningAction
import com.example.fsm.examples.actions.PauseTimerAction
import com.example.fsm.examples.actions.ResetTimerAction
import com.example.fsm.examples.actions.StartTimerAction
import com.example.fsm.examples.actions.StopTimerAction
import com.example.fsm.examples.actions.SwitchOffAction

public class StateMachineDSLSemanticsTest {

	@Before
	public void setUp() throws Exception {
	}
	
	
	@Test
	public void executeActualMachineSmokeTest() throws Exception {
		StateMachine watch = new StateMachineDSL().eval(name:'WatchState'){
							
				setDEBUG(true)
				
//				fsm(name:"watch") {
					state(name:"reseted",type:"start") {
					  entry "resetTimer";
					  transitions {
						when (event:"start",enter:"running");
						when (event:"switchOff",enter:"off");
					  }
					}
				
					state(name:"running") {
					  entry "startTimer";
					  perform "loopRunning"; //do is a reserved keyword in Groovy
					  transitions {
						when(event:"split",enter:"paused");
						when(event:"stop",enter:"stopped");
					  }
					}
				
					state(name:"paused") {
					  entry "pauseTimer";
					  transitions {
						when(event:"unsplit",enter:"running");
						when(event:"stop",enter:"stopped");
					  }
					}
				
					state(name:"stopped") {
					  entry "stopTimer";
					  transitions {
						when(event:"reset",enter:"stopped");
						when(event:"switchOff",enter:"off");
					  }
					}
				
					state(name:"off") {
					  exit "switchOff";
					  transitions {
						when(enter:"end");
					  }
					}
//				}
			}
		
		
		  
//		StateMachine watch = smDSL.call();
	
		   
			
		println "------------"
		watch.status();
		watch.start();
		watch.status();
	
		println "------------"
			watch.start()
		watch.status();
		watch.receiveEvent("start");
		watch.status();
	
		println "------------"
		watch.status();
		watch.receiveEvent("split");
		watch.status();
	
		watch.multiply(watch)
	}
	
	@Test
	public void testMore() throws Exception {
		fail("Implement some actual tests");
	}
		
}
