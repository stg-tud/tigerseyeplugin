package de.tud.stg.tigerseye.example.dzoneunits

import javax.measure.unit.Unit;
import org.jscience.physics.amount.Amount;

import de.tud.stg.popart.builder.core.annotations.DSL;
import de.tud.stg.popart.builder.core.annotations.DSLMethod;
import de.tud.stg.popart.dslsupport.Interpreter
import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.AnnotationExtractor.DoubleElementHandler;

class UnitsDSL extends Interpreter{

	static{
		//useful in order to avoid defining arithmetic operations for the language as well
		GroovyDefinitionForUnitsDSL.enableUnits()
	}

	public Object eval(HashMap map, Closure cl) {
		cl.setDelegate(this);
		cl.setResolveStrategy(Closure.DELEGATE_FIRST);
		return cl.call();
	}

	@DSLMethod(prettyName="p0.p1")
	@PopartType(clazz = PopartOperationKeyword.class)
	public String toDouble(Integer i1,Integer i2){
		return (i1.doubleValue() + Double.valueOf ( "0."+ i2.toString())).toString()
	}

	@DSLMethod(prettyName="p0__kg")
	@PopartType(clazz = PopartOperationKeyword.class)
	public Amount kilogram(Object n){
		return amountFor(n,"kg");
	}
	
	@DSLMethod(prettyName="p0__g")
	@PopartType(clazz = PopartOperationKeyword.class)
	public Amount gram(Object n){
		return amountFor(n,"g");
	}


	Amount amountFor(Object value, String unit){
		String strVal;
		if(value instanceof String)
			strVal = Double.valueOf(value.toString)
	    else if (value instanceof Number)
			strVal = ((Double) value).toString()
	    else
			throw new IllegalArgumentException("Unexpected type: " + value);
			
		return Amount.valueOf(Double.valueOf(strVal), Unit.valueOf(unit))
	}

	@DSLMethod(prettyName="p0__cm")
	@PopartType(clazz = PopartOperationKeyword.class)
	public Amount centimeter(Object n){
		return amountFor(n,"cm")
	}

	@DSLMethod(prettyName="p0__km")
	@PopartType(clazz = PopartOperationKeyword.class)
	public Amount kilometer(Object n){
		return amountFor(n,"km")
	}

	@DSLMethod(prettyName="p0__min")
	@PopartType(clazz = PopartOperationKeyword.class)
	public Amount minutes(Object n){
		return amountFor(n,"min")
	}

	@DSLMethod(prettyName="p0__m")
	@PopartType(clazz = PopartOperationKeyword.class)
	public Amount meter(Object n){
		return amountFor(n,"m")
	}

	@DSLMethod(prettyName="p0__h")
	@PopartType(clazz = PopartOperationKeyword.class)
	public Amount hours(Object n){
		return amountFor(n,"h")
	}

	@DSLMethod(prettyName="p0__s")
	@PopartType(clazz = PopartOperationKeyword.class)
	public Amount seconds(Object n){
		return amountFor(n,"s")
	}

	@DSLMethod(prettyName="p0__in")
	@PopartType(clazz = PopartOperationKeyword.class)
	public Amount inch(Object n){
		return amountFor(n,"in")
	}

	@DSLMethod(prettyName = "SELECT__p0__FROM__p1")
	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 0)
	public void selectFrom(String[] columns, String[] tables) {
		System.out.println("SimpleSqlDSL.selectFrom()"
				+ Arrays.toString(columns) + Arrays.toString(tables));
	}
	//
	//
	//	@DSLMethod(prettyName = "SELECT__p0__FROM__p1__WITH__p2")
	//	@PopartType(clazz = PopartOperationKeyword.class, breakpointPossible = 0)
	//	public void selectFromWith(String[] columns, String[] tables, String[] with) {
	//		System.out.println("SimpleSqlDSL.selectFrom()"
	//				+ Arrays.toString(columns) + Arrays.toString(tables) + Arrays.toString(with));
	//	}
}
