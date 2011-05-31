package de.tud.stg.tigerseye.example.dzoneunits

import javax.measure.unit.*
import org.jscience.physics.amount.*

class GroovyDefinitionForUnitsDSL {
	
	public static void enableUnits(){
		// Allow ExpandoMetaClass to traverse class hierarchies
		// That way, properties added to Number will also be available for Integer or BigDecimal, etc.
		ExpandoMetaClass.enableGlobally()

		// transform number properties into an mount of a given unit represented by the property
		Number.metaClass.getProperty = { String symbol -> Amount.valueOf(delegate, Unit.valueOf(symbol)) }

		// define opeartor overloading, as JScience doesn't use the same operation names as Groovy
		Amount.metaClass.multiply = { Number factor -> delegate.times(factor) }
		Number.metaClass.multiply = { Amount amount -> amount.times(delegate) }
		Number.metaClass.div = { Amount amount -> amount.inverse().times(delegate) }
		Amount.metaClass.div = { Number factor -> delegate.divide(factor) }
		Amount.metaClass.div = { Amount factor -> delegate.divide(factor) }
		Amount.metaClass.power = { Number factor -> delegate.pow(factor) }
		Amount.metaClass.negative = { -> delegate.opposite() }
		Amount.metaClass.show = { -> delegate.getEstimatedValue().toString() }
	
		// define to() method for unit conversion
		Amount.metaClass.to = { Amount amount -> delegate.to(amount.unit) }
		
	}
	
}
