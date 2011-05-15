package de.tud.stg.tigerseye.examples.dsldefinitions.conditionaldsl;

import groovy.lang.Closure;

import java.util.HashMap;
import de.tud.stg.popart.builder.core.annotations.DSL;
import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;

import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartLiteralKeyword;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;

/**
 * {@link ConditionalDSL} is a DSL with support for conditional statements in an alternative if-then-else syntax
 * 
 * @author Kamil Erhard
 * 
 */
@DSL(whitespaceEscape = " ")
public class ConditionalDSL implements de.tud.stg.popart.dslsupport.DSL {

	public Object eval(HashMap map, Closure cl) {
		cl.setDelegate(this);
		cl.setResolveStrategy(Closure.DELEGATE_FIRST);
		return cl.call();
	}

	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 0)
	@DSLMethod(prettyName = "if ( p0 ) then { p1 } else { p2 }")
	public boolean ifThenElse(boolean check, Closure thenBlock, Closure elseBlock) {
		if (check) {
			thenBlock.call();
		} else {
			elseBlock.call();
		}  

		return check;
	}
}
