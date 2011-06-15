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

 
def watch = state(name:'WatchStateArrows'){
	
	state(name:"reseted",type:"start") {
		entry "resetTimer";
		transitions {
			"start" → "running"
			"switchOff" → "off"
		}
	  }
	  
	  state(name:"running") {
		entry "startTimer";
		perform "loopRunning"; //do is a reserved keyword in Groovy
		transitions {
		"split" → "paused"
		"stop" → "stopped"
		}
	  }
	  
	  state(name:"paused") {
		entry "pauseTimer";
		transitions {
			"unsplit" → "running"
			"stop" → "stopped"
		}
	  }
	  
	  state(name:"stopped") {
		entry "stopTimer";
		transitions {
		"reset" → "stopped"
		"switchOff" → "off"
		}
	  }
	  
	  state(name:"off") {
		exit "switchOff";
		transitions {
		  → "end"
		}
	  }
	
	
}

watch.status();
watch.start();
watch.status();

watch.receiveEvent("start");
watch.status();

watch.receiveEvent("split");
watch.status();

watch.receiveEvent("stop");
watch.status()

watch.receiveEvent("switchOff");
watch.status()