package de.tud.stg.tigerseye.examples.statemachine


/**
 * Tigerseye language: com.example.fsm.StateMachineDSL
 *
 * Declared keywords:
 *  void entry(String)
 *  void entry(Closure)
 *  Object eval(HashMap, Closure)
 *  void exit(String)
 *  void exit(Closure)
 *  StateMachine fsm(HashMap, Closure)
 *  boolean getDEBUG()
 *  StateMachine getThisFsm()
 *  State getThisState()
 *  boolean isDEBUG()
 *  void perform(String)
 *  void perform(Closure)
 *  void propertyMissing(String, Object)
 *  Object propertyMissing(String)
 *  void setDEBUG(boolean)
 *  State state(HashMap, Closure)
 *  void transitions(Closure)
 *  void when(HashMap)
 */

//Had to import the Actions manually
import com.example.fsm.examples.actions.*;


state(name:'WatchState'){

	HashMap aMap = new HashMap();
	aMap.put("loopRunning",new LoopRunningAction());
	aMap.put("pauseTimer",new PauseTimerAction());
	aMap.put("resetTimer",new ResetTimerAction());
	aMap.put("startTimer",new StartTimerAction());
	aMap.put("stopTimer",new StopTimerAction());
	aMap.put("switchOff",new SwitchOffAction());
	
	Binding aBinding = new Binding();
	aBinding.setVariable("myActionBinding", aMap);
	
	def myActionBinding = aMap
	
	setDEBUG(true)
	
	fsm(name:"watch",actionBinding:aBinding) {
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
	}

	
	
}
