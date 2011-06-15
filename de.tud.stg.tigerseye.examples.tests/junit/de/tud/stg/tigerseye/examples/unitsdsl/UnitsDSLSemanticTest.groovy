package de.tud.stg.tigerseye.examples.unitsdsl;

import org.jscience.physics.amount.Amount
import org.junit.Before;
import org.junit.Test;
import javax.measure.unit.Unit;
import static org.junit.Assert.*;

import de.tud.stg.tigerseye.example.dzoneunits.UnitsDSL

/**
 * Tests that the {@link UnitsDSL} API can be used as intended
 *
 */
class UnitsDSLSemanticTest {

	UnitsDSL ud
	
	@Before
	public void setUp() throws Exception {
		ud = new UnitsDSL();
	}

	@Test
	public void testKilogramDouble() {
		println ud.kilogram(5)
	}

	@Test
	public void testCentimeter() {
		println ud.centimeter (5)
	}

	@Test
	public void testMeter() {
		println ud.meter (5)
	}

	@Test
	public void testHours() {
		println ud.hours(5)
	}

	@Test
	public void testSeconds() {
		println ud.seconds(5)
		ud.seconds( 5)
	}
	
	@Test
	public void testShowComplexSeconds() {
		println ud.seconds(5)
		def complexTime = ud.seconds(5) + ud.hours(20)
		Amount expected = amountVal(5,"s").plus(amountVal(20,"h"))
		println expected.compareTo(complexTime.plus(amountVal(3,"min")))
		println expected.compareTo(expected)
		println "$expected and $complexTime"
	}
	
	Amount amountVal(double val, String unit){
		return Amount.valueOf(val, Unit.valueOf("h"))
	}
	
	@Test
	public void testAmountFor() throws Exception {
		println ud.amountFor( 5, "kg")
	}
	
	@Test
	public void testDotSeperated() throws Exception {
		String d1 = ud.toDouble(22,11)
		Double dis = Double.valueOf (d1)
		assertEquals( 22.11d, dis , 0.001)
	}


}
