package de.tud.stg.tigerseye.example.dzoneunits

import org.jscience.physics.amount.Amount;
import javax.measure.unit.Unit;

/**
 * script binding to transform free standing unit reference like 'm', 'h', etc
 */
class UnitBinding extends Binding{

	def getVariable(String symbol) {
		if (symbol == 'out') return System.out
		return Amount.valueOf(1, Unit.valueOf(symbol))
	}
}
