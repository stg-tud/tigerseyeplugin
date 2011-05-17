package de.tud.stg.tigerseye.examples.logo;

import de.tud.stg.tigerseye.*;
import org.javalogo.*;
import java.awt.Color;

/**
 * This version of Logo simulates costs of drawing operation by slowing them down by 100 ms.
 */
public class FunctionalLogo  extends TimedLogo implements IFunctionalLogo {
	protected HashMap<String,Closure> functionNamesToClosure;
		 
	public FunctionalLogo() {
		super();
		functionNamesToClosure = new HashMap<String,Closure>();
	}
	
	public HashMap<String,Closure> getFunctionNamesToClosure() {
		return functionNamesToClosure;
	}
	
	public void setFunctionNamesToClosure(HashMap<String,Closure> map) {
		this.functionNamesToClosure = map;
	}
	
	/* Literals */

	/* Operations */
	public Closure app(String name) {
		assert name != null;
		assert !name.isEmpty();
		Closure function = functionNamesToClosure.get(name)
		assert function != null;
		return function;
	}
	
	/* Abstraction Operators */
	public void fun(String name, Closure body) {
		functionNamesToClosure.put(name, body);
	}
	
	/* Inline Meta Level */
	private Object methodMissing(String name, Object args) {
		//println "${this} : functions=$functionNamesToClosure"
		if (functionNamesToClosure.get(name) != null) {
          app(name).call(*args);
		} else {
			throw new MissingMethodException(name, this.class, args);
	    }
	}
}