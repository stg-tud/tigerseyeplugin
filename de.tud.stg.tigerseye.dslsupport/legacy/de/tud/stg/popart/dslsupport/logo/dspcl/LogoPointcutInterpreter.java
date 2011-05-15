package de.tud.stg.popart.dslsupport.logo.dspcl;

import de.tud.stg.popart.aspect.PointcutDSL;
import de.tud.stg.popart.pointcuts.AndPCD;
import de.tud.stg.popart.pointcuts.Pointcut;

import de.tud.stg.popart.aspect.extensions.instrumentation.InstrumentationActivator;
import de.tud.stg.popart.dslsupport.logo.dsjpm.TurtleLeftJoinPointInstrumentation;
import de.tud.stg.popart.dslsupport.logo.dsjpm.TurtleRightJoinPointInstrumentation;
import de.tud.stg.popart.dslsupport.logo.dsjpm.TurtleBackwardJoinPointInstrumentation;
import de.tud.stg.popart.dslsupport.logo.dsjpm.TurtleForwardJoinPointInstrumentation;

import org.javalogo.Turtle;

/**
 * A pointcut interpreter for the Logo language.
 * Because the pointcut interpreter that registers a JPM must be singleton, otherwise the classes are instrumented twice.
 * @author dinkelaker
 */
public class LogoPointcutInterpreter extends PointcutDSL 
                                     implements ILogoPointcutLanguage {

	/**
	 * Singleton instance.
	 */
	private static LogoPointcutInterpreter instance = null; 
	
	public static LogoPointcutInterpreter getInstance() {
		if (instance == null) instance = new LogoPointcutInterpreter();
		return instance;
	}
	
	private LogoPointcutInterpreter() {
		// TODO move to setMetaClass after refactoring
		InstrumentationActivator.declareJoinPoint(Turtle.class, "left", TurtleLeftJoinPointInstrumentation.class);
		InstrumentationActivator.declareJoinPoint(Turtle.class, "lt", TurtleLeftJoinPointInstrumentation.class);
		InstrumentationActivator.declareJoinPoint(Turtle.class, "right", TurtleRightJoinPointInstrumentation.class);
		InstrumentationActivator.declareJoinPoint(Turtle.class, "rt", TurtleRightJoinPointInstrumentation.class);
		InstrumentationActivator.declareJoinPoint(Turtle.class, "forward", TurtleForwardJoinPointInstrumentation.class);
		InstrumentationActivator.declareJoinPoint(Turtle.class, "fd", TurtleForwardJoinPointInstrumentation.class);
		InstrumentationActivator.declareJoinPoint(Turtle.class, "backward", TurtleBackwardJoinPointInstrumentation.class);
		InstrumentationActivator.declareJoinPoint(Turtle.class, "bd", TurtleBackwardJoinPointInstrumentation.class);
	}

	public Pointcut pbackward() {
		return new TurtleMovingBackwardPCD();
	}

	public Pointcut pbackward(int steps) {
		return new AndPCD( 
		  new TurtleMovingBackwardPCD(),
		  new TurtleMovingStepsPCD(steps));
	}

	public Pointcut pbackward(int minSteps, int maxSteps) {
		return new AndPCD( 
		  new TurtleMovingBackwardPCD(),
		  new TurtleMovingStepsRangePCD(minSteps,maxSteps));
	}

	public Pointcut pforward() {
		return new TurtleMovingForwardPCD();
	}

	public Pointcut pforward(int steps) {
		return new AndPCD( 
				  new TurtleMovingForwardPCD(),
				  new TurtleMovingStepsPCD(steps));
	}

	public Pointcut pforward(int minSteps, int maxSteps) {
		return new AndPCD( 
		  new TurtleMovingForwardPCD(),
		  new TurtleMovingStepsRangePCD(minSteps,maxSteps));
	}

	public Pointcut pleft() {
		return new TurtleTurningLeftPCD();
	}

	public Pointcut pleft(int degrees) {
		return new AndPCD(
			new TurtleTurningLeftPCD(),
			new TurtleTurningDegreesPCD(degrees));
	}

	public Pointcut pleft(int minDegrees, int maxDegrees) {
		return new AndPCD(
			new TurtleTurningLeftPCD(),
			new TurtleTurningDegreesRangePCD(minDegrees,maxDegrees));
	}

	public Pointcut pmotion() {
		return new TurtleMotionPCD();
	}

	public Pointcut pmoving() {
		return new TurtleMovingPCD();
	}

	public Pointcut pmoving(int steps) {
		return new AndPCD(
				new TurtleMovingPCD(),
				new TurtleMovingStepsPCD(steps));
	}

	public Pointcut pmoving(int minSteps, int maxSteps) {
		return new AndPCD(
				new TurtleMovingPCD(),
				new TurtleMovingStepsRangePCD(minSteps,maxSteps));
	}

	public Pointcut pright() {
		return new TurtleTurningRightPCD();
	}

	public Pointcut pright(int degrees) {
		return new AndPCD(
				new TurtleTurningRightPCD(),
				new TurtleTurningDegreesPCD(degrees));
	}

	public Pointcut pright(int minDegrees, int maxDegrees) {
		return new AndPCD(
			new TurtleTurningRightPCD(),
			new TurtleTurningDegreesRangePCD(minDegrees,maxDegrees));
	}
	
	public Pointcut pturning() {
		return new TurtleTurningPCD();
	}

	public Pointcut pturning(int degrees) {
		return new TurtleTurningDegreesPCD(degrees);
	}
	
	public Pointcut pturning(int minDegrees, int maxDegrees) {
	    return new TurtleTurningDegreesRangePCD(minDegrees,maxDegrees);
	}

}
