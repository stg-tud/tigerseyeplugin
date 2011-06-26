package de.tud.stg.popart.dslsupport;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tud.stg.tigerseye.examples.simplesql.SimpleSqlDSL;
import de.tud.stg.tigerseye.examples.setdsl.SetDSL;
import de.tud.stg.tigerseye.examples.setdsl.SetDSL;
import de.tud.stg.tigerseye.examples.simplesql.SimpleSqlDSL;
import de.tud.stg.popart.dslsupport.InterpreterCombiner;

public class InterpreterCombinerTest {

	private static OutputStream original = null;
	
	@BeforeClass
	public static void before() throws Exception {
		original = System.out;
		System.out = new PrintStream(new ByteArrayOutputStream());
		println "should not be printed"		
	}
	
	@AfterClass
	public static void after() throws Exception {
		System.out = original;
//		println "should be printed"
	}
	
	@Test
	public void testInterpreterCombinerInitialization() throws Exception {
		InterpreterCombiner i1 = new InterpreterCombiner(
			new SetDSL())
		InterpreterCombiner i2 = new InterpreterCombiner(
			[new SetDSL(), new SimpleSqlDSL()], [name:"SomeFile"])
		InterpreterCombiner i3 = new InterpreterCombiner(
			new SetDSL(), new SimpleSqlDSL())
	}
	

	@Test
	public void testInterpreterCombinerEvaluation() throws Exception {
		// need to declare with def or InterpreterCombiner type
		InterpreterCombiner interp = new InterpreterCombiner(
			[new SetDSL(), new SimpleSqlDSL() ], [name:"some"])

		interp.eval() {
			Set M = union(
					asSet(
					multiElementedList(
					"a",
					multiElementedList(
					"b",
					singleElementedList(
					"c")))),
					asSet(
					multiElementedList(
					"c",
					singleElementedList(
					"d")))) ;

			println M

			def a = selectFrom(
					["id", "name"
					]as String[],
					[
						"students",
						"teachers"
					]
					as String[])
		}
	}
}
